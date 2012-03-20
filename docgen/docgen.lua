dofile("docgen/libextra.lua")
dofile("docgen/template.lua")

-- constants
JAVA_IDENTIFIER = "[A-Za-z_][A-Za-z0-9_]*"

-- states
NONE = 0
IN_COMMENT = 1
AFTER_COMMENT = 2


local folders = {
	"src/main/java/eu/tomylobo"
}

local function nameToLabel(name)
	return "/"..name:gsub("_", " ")
end

local function scanfile(filename, records)
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
			if not line:match("^%s*@%s*Command") then
				if line:match("[;{]") then
					state = NONE
				end

				break -- continue
			end

			local record = {
				class = class,
				names = {},
				permissions = DISABLED,
				comment = comment:trim(),
			}

			local args = line:match("^%s*@%s*Command%s*%((.*)%)%s*$")
			if args then
				assert(loadstring(string.format("globaltmp = {%s}", args)))()
				for k,v in pairs(globaltmp) do
					record[k] = v
				end
			end

			records[#records+1] = record

			state = PARSE_METHOD_NAME
		elseif state == PARSE_METHOD_NAME then
			local method = line:match("^%s*public%s+void%s+("..JAVA_IDENTIFIER..")%s*%(%s*Context%s+"..JAVA_IDENTIFIER.."%s*%)")
			if not method then
				if line:match("[;{]") then
					state = NONE
					records[#records] = nil
				end

				break -- continue
			end

			local record = records[#records]
			if type(record.names) ~= "table" then
				record.names = { record.names }
			elseif #record.names == 0 then
				record.names = { method }
			end
			record.method = method

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

local records = {}
for _,folder in ipairs(folders) do
	for filename in io.listRecursive(folder) do
		if filename:match("%.java$") then
			scanfile(filename, records)
		end
	end
end
print(#records.." commands found.")

local globals = {
	version = io.readfile_popen("git describe"):trim(),
}

local output = { pcallTemplate(templates.header, globals) }
for _,record in ipairs(records) do
	local names = record.names
	local name = names[1]:htmlEntities()
	local variables = {
		name = name,
		label = nameToLabel(name),
		comment = record.comment, -- the comment field may contain html, so it's left unencoded
	}
	for k,v in pairs(names) do
		names[k] = nameToLabel(v)
	end

	if #record.names > 1 then
		variables.aliases = table.concat(record.names, ", ", 2)
	end

	table.merge(variables, record, function(v, k)
		if variables[k] ~= nil then return nil end
		if type(v) == "table" then
			v = table.concat(v, ", ")
		else
			variables[k] = v:htmlEntities()
		end
	end)

	table.merge(variables, globals)

	output[#output+1] = pcallTemplate(templates.entry, variables)
end
output[#output+1] = pcallTemplate(templates.footer, globals)

io.writefile("output.txt", table.concat(output))
