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

local folders = {
	"src/main/java/eu/tomylobo"
}

local function nameToLabel(name)
	return "/"..name:gsub("_", " ")
end

local function scanfile(filename, records, annotationName, lookForMethod)
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

			if lookForMethod then
				state = PARSE_METHOD_NAME
			else
				state = NONE
			end
		elseif state == PARSE_METHOD_NAME then
			local method = line:match("^%s*public%s+void%s+("..JAVA_IDENTIFIER..")%s*%(%s*Context%s+"..JAVA_IDENTIFIER.."%s*%)")
			if not method then
				if line:match("[;{]") then
					state = NONE
					records[#records] = nil
				end

				break -- continue
			end

			records[#records].method = method

			state = NONE
		end
	until true end -- this makes break a continue

	if state ~= NONE then
		records[#records] = nil
	end
end

local templates = {}
for filename in io.listRecursive("docgen/templates") do
	local name = filename:match("[/\\]([^/\\]+)%.txt$")
	if name then
		templates[name] = io.readfile(filename)
	end
end

local commands = {}
local configs = {}
for _,folder in ipairs(folders) do
	for filename in io.listRecursive(folder) do
		if filename:match("%.java$") then
			scanfile(filename, commands, "Command", true)
			scanfile(filename, configs, "ConfigItem", false)
		end
	end
end

print(#commands.." commands found.")
print(#configs.." configs found.")

local globals = {
	version = io.readfile_popen("git describe"):trim(),
}

local output = { pcallTemplate(templates.header, globals) }
for _,command in ipairs(commands) do
	if command.names == nil or #command.names == 0 then
		command.names = { command.method }
	elseif type(command.names) ~= "table" then
		command.names = { command.names }
	end
	if command.permissions == Command.DISABLED then
		command.permissions = nil
	end

	local name = command.names[1]:htmlEntities()
	local variables = {
		name = name,
		label = nameToLabel(name),
		comment = command.comment, -- the comment field may contain html, so it's left unencoded
		permissions = command.permissions ~= Command.DISABLED and command.permissions or nil
	}
	for k,v in pairs(command.names) do
		command.names[k] = nameToLabel(v)
	end

	if #command.names > 1 then
		variables.aliases = table.concat(command.names, ", ", 2)
	end

	table.merge(variables, command, function(v, k)
		if variables[k] ~= nil then return nil end
		if type(v) == "table" then
			v = table.concat(v, ", ")
		else
			variables[k] = v:htmlEntities()
		end
	end)

	table.merge(variables, globals)

	output[#output+1] = pcallTemplate(templates.entry, variables, "<blockquote>Error parsing entry</blockquote>\n")
end
output[#output+1] = pcallTemplate(templates.footer, globals)

io.writefile("output.txt", table.concat(output))


local parts = { }
for _,configItem in ipairs(configs) do
	table.dump(configItem)
	local section,key = configItem[1]:match("^([^.]*)%.(.*)$")

	local variables = {
		section = section,
		key = key,
		comment = configItem.comment, -- the comment field may contain html, so it's left unencoded
	}

	table.merge(variables, configItem, function(v, k)
		if variables[k] ~= nil then return nil end
		if type(v) == "table" then
			v = table.concat(v, ", ")
		else
			variables[k] = v:htmlEntities()
		end
	end)

	table.merge(variables, globals)

	local part = parts[section]
	if part == nil then
		part = {}
		parts[section] = part
		parts[#parts+1] = section
	end
	part[#part+1] = pcallTemplate(templates.config_entry, variables, "<blockquote>Error parsing entry</blockquote>\n")
end

local output = { pcallTemplate(templates.config_header, globals) }
for _,section in ipairs(parts) do
	local part = parts[section]
	output[#output+1] = pcallTemplate(templates.section_header, table.merge({ section = section }, globals))
	for _,partpart in ipairs(part) do
		output[#output+1] = partpart
	end
	output[#output+1] = pcallTemplate(templates.section_footer, table.merge({ section = section }, globals))
end
output[#output+1] = pcallTemplate(templates.config_footer, globals)
io.writefile("config_output.txt", table.concat(output))
