local fake_entities = setmetatable({
	lbr = "[",
	rbr = "]",
	lcur = "{",
	rcur = "}",
	pipe = "|"
}, {__index=function(_,x) return "&"..x..";" end})
function parseTemplate(template, variables)
	template = template:gsub("%[([^]]+)%]", function(subTemplate)
		local newSubTemplate,default = subTemplate:match("^([^|]*)|(.*)$")

		return pcallTemplate(newSubTemplate or subTemplate, variables, default)
	end)

	template = template:gsub("{([A-Za-z0-9_]+)}", function(name)
		local value = variables[name]
		if not value then error("catch this") end

		return value
	end)

	template = template:gsub("&([^;]+);", fake_entities)

	return template
end

function pcallTemplate(template, variables, default)
	local ok,result = pcall(parseTemplate, template, variables)
	if ok then return result end

	if result:match(": catch this$") then return default or "" end

	error(result)
end
