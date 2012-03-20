local fake_entities = setmetatable({
	lbr = "[",
	rbr = "]",
	lcur = "{",
	rcur = "}",
}, {__index=function(_,x) return "&"..x..";" end})
function parseTemplate(template, variables)
	template = template:gsub("%[([^]]+)%]", function(subTemplate)
		return pcallTemplate(subTemplate, variables)
	end)

	template = template:gsub("{([A-Za-z0-9_]+)}", function(name)
		local value = variables[name]
		if not value then error("meep") end
		return value
	end)

	template = template:gsub("&([^;]+);", fake_entities)

	return template
end

function pcallTemplate(template, variables)
	local ok,result = pcall(parseTemplate, template, variables)
	if ok then return result end

	if result:match(": meep$") then return "" end

	error(result)
end
