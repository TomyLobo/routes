dofile("docgen/libextra.lua")
dofile("docgen/template.lua")

-- constants
JAVA_IDENTIFIER = "[A-Za-z_][A-Za-z0-9_]*"

-- states
NONE = 0
IN_COMMENT = 1
AFTER_COMMENT = 2

-- constants that might show up in annotations
Command = { DISABLED = "DISABLED" }

-- folders to scan for commands and config items
local folders = {
	"src/main/java/eu/tomylobo/routes"
}

-- Converts a method name/@Command.names entry to a readable command.
local function nameToLabel(name)
	return "/"..name:gsub("_", " ")
end

-- Accounts for javadoc's peculiarities
local function parseJD(comment)
	-- TODO: parse @links
	return comment
end

-- Precedence: variables > record > globals. record will be flattened and treated with string.htmlEntities.
local function mergeGlobalsAndRecord(variables, globals, record)
	-- Initialize with the globals.
	local ret = table.clone(globals)

	-- Merge the record in, skipping existing entries, concatenating arrays and encoding special characters.
	table.merge(ret, record, function(v, k)
		if type(v) == "table" then
			ret[k] = table.concat(v, ", ")
		else
			ret[k] = tostring(v):htmlEntities()
		end
	end)

	-- Finally, write the specially handled variables.
	table.merge(ret, variables)

	return ret
end

-- This function reads all the javadoc comments followed by the specified annotation.
-- It also looks for a method/variable after that and reads that too.
local function scanfile(filename, records, annotationName)
	local class = filename:match("("..JAVA_IDENTIFIER..")%.java")

	local state = NONE
	local comment
	for line in io.lines(filename) do repeat -- this makes break a continue
		line = line:gsub("%*/", "$end$")
		line = line:match("^%s*%*?%s*(.*)$")

		if state == NONE then
			line = line:match("/%*%*(.*)$")
			if not line then
				break -- continue
			end

			state = IN_COMMENT
			comment = ""
		end

		if state == IN_COMMENT then
			local fullLine,commentPart
			fullLine,commentPart,line = line,line:match("^(.*)%$end$(.*)$")
			if not line then
				comment = comment.."\n"..fullLine
				break -- continue
			end

			state = AFTER_COMMENT
			comment = comment.."\n"..commentPart
		end

		if state == AFTER_COMMENT then
			if not line:match("^%s*@%s*"..annotationName) then
				if line:match("[;{]") then
					state = NONE
				end

				break -- continue
			end

			local record = {
				class = class,
				comment = comment:trim(),
			}

			local args = line:match("^%s*@%s*"..annotationName.."%s*%((.*)%)%s*$")
			if args then
				assert(loadstring(string.format("globaltmp = {%s}", args)))()
				for k,v in pairs(globaltmp) do
					record[k] = v
				end
			end

			records[#records+1] = record

			state = PARSE_ELEMENT_NAME
		elseif state == PARSE_ELEMENT_NAME then
			local elementType,elementName = line:match("^%s*public%s+("..JAVA_IDENTIFIER..")%s+("..JAVA_IDENTIFIER..")[ (]")
			if not elementName then
				if line:match("[;{]") then
					state = NONE
					records[#records] = nil
				end

				break -- continue
			end

			local record = records[#records]
			record.elementType = elementType
			record.elementName = elementName

			state = NONE
		end
	until true end -- this makes break a continue

	if state ~= NONE then
		records[#records] = nil
	end
end

-- read templates
local templates = {}
for filename in io.listRecursive("docgen/templates") do
	local name = filename:match("[/\\]([^/\\]+)%.txt$")
	if name then
		templates[name] = io.readfile(filename)
	end
end

-- parse source files
local commands = {}
local configs = {}
for _,folder in ipairs(folders) do
	for filename in io.listRecursive(folder) do
		if filename:match("%.java$") then
			scanfile(filename, commands, "Command")
			scanfile(filename, configs, "ConfigItem")
		end
	end
end

print(#commands.." commands found.")
print(#configs.." configs found.")

-- A table of global variables. Initially contains a version string obtained from git describe.
local globals = {
	version = io.readfile_popen("git describe"):trim(),
}

-- Make an output array, initially containing the header. It is later concatenated and written to output.txt
local output = { pcallTemplate(templates.header, globals) }
for _,command in ipairs(commands) do
	if command.names == nil or #command.names == 0 then
		-- The names property was not given or is empty, so use the method name
		command.names = { command.elementName }
	elseif type(command.names) ~= "table" then
		-- Braces were elided, so add them back :)
		command.names = { command.names }
	end

	-- Handle explicitly disabled permissions
	if command.permissions == Command.DISABLED or (type(command.permissions) == "table" and command.permissions[1] == Command.DISABLED) then
		command.permissions = nil
	end

	-- A table of local variables to be used with the "entry" template.
	local variables = {
		name = command.names[1], -- a clean identifier for the command
		comment = parseJD(command.comment), -- the comment field may contain html, so it's left unencoded
		permissions = command.permissions ~= Command.DISABLED and command.permissions or nil
	}

	-- Make readable names
	for k,v in pairs(command.names) do
		command.names[k] = nameToLabel(v)
	end

	-- Split up names into label (1st name) and aliases (the rest)
	variables.label = command.names[1]

	if #command.names > 1 then
		variables.aliases = table.concat(command.names, ", ", 2)
	end

	-- Merge globals and the remaining ConfigItem annotation properties in
	variables = mergeGlobalsAndRecord(variables, globals, command)

	-- Finally, parse the template with the variables
	output[#output+1] = pcallTemplate(templates.entry, variables, "<blockquote>Error parsing entry</blockquote>\n")
end
-- Append the footer
output[#output+1] = pcallTemplate(templates.footer, globals)

-- And finally write the whole thing into a text file.
io.writefile("output.txt", table.concat(output))


-- make a table of the format { "sectionName", "sectionName", sectionName = part = { partpart, partpart, partpart } }
local sections = { }
for _,configItem in ipairs(configs) do
	-- parse the "value" property of the annotation into section and key
	local section,key = configItem[1]:match("^([^.]*)%.(.*)$")

	-- predefine the variables array with some entries that need special handling
	local variables = {
		section = section,
		key = key,
		comment = parseJD(configItem.comment), -- the comment field may contain html, so it's left unencoded
	}

	-- Merge globals and the remaining ConfigItem annotation properties in
	variables = mergeGlobalsAndRecord(variables, globals, configItem)

	-- Retrieve the section to write into from the sections table
	local part = sections[section]
	if part == nil then
		-- If it doesn't exist, create a new part and write it back into the sections table
		part = {}
		sections[section] = part
		-- Make sure we can later retrieve these in order.
		sections[#sections+1] = section
	end

	-- Finally, parse the template with the variables
	part[#part+1] = pcallTemplate(templates.config_entry, variables, "<blockquote>Error parsing entry</blockquote>\n")
end

-- Make an output array, initially containing the header. It is later concatenated and written to config_output.txt
local config_output = { pcallTemplate(templates.config_header, globals) }
for _,section in ipairs(sections) do
	-- retrieve the section contents
	local part = sections[section]

	-- Add the section header to the output
	config_output[#config_output+1] = pcallTemplate(templates.section_header, table.merge({ section = section }, globals))

	-- Add the already parsed section contents to the output
	for _,partpart in ipairs(part) do
		config_output[#config_output+1] = partpart
	end

	-- Add the section footerto the output
	config_output[#config_output+1] = pcallTemplate(templates.section_footer, table.merge({ section = section }, globals))
end
-- Append the footer
config_output[#config_output+1] = pcallTemplate(templates.config_footer, globals)

-- And finally write the whole thing into a text file.
io.writefile("config_output.txt", table.concat(config_output))
