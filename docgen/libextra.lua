-- io --
function io.readfile(filename)
	local file = assert(io.open(filename))
	local contents = file:read("*a")
	file:close()
	return contents
end

function io.readfile_popen(filename)
	local file = assert(io.popen(filename))
	local contents = file:read("*a")
	file:close()
	return contents
end

function io.writefile(filename, contents)
	local file = io.open(filename, "w")
	file:write(contents)
	file:close()
end

function io.writefile_popen(filename, contents)
	local file = io.popen(filename, "w")
	file:write(contents)
	file:close()
end

function io.listRecursive(folder)
	local file = io.popen("find "..folder)
	return file:lines()
end

-- string --
function string:trim()
	if #self == 0 then return self end
	return self:match("^%s*(%S*)$") or self:match("^%s*(.*%S)%s*$")
end

local stringToEntity = {
	['"'] = "&quot;",
	["'"] = "&apos;",
	['&'] = "&amp;",
	['<'] = "&lt;",
	['>'] = "&gt;",
}
function string:htmlEntities()
	return self:gsub("[\"'&<>]", stringToEntity)
end


-- table --
function table.dump(tbl)
	print("----------------")
	for k,v in pairs(tbl) do
		if type(v) == "table" then
			if #v == 0 then
				v = "{ }"
			else
				v = "{ "..table.concat(v, ", ").." }"
			end
		elseif type(v) == "string" then
			v = string.format("%q", v)
		end
		print("key = "..k.."\tvalue = "..tostring(v))
	end
	print("----------------")
end

function table.merge(target, source, map)
	if type(map) == "table" then
		for k,v in pairs(source) do
			v = map[v]
			if v ~= nil then
				target[k] = v
			end
		end
	elseif type(map) == "function" then
		for k,v in pairs(source) do
			v = map(v, k)
			if v ~= nil then
				target[k] = v
			end
		end
	else
		for k,v in pairs(source) do
			target[k] = v
		end
	end
end
