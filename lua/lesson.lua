#!/usr/bin/env lua

function lesson_meta_table () --{
	local base = {
		type_name = function (self)
			return "base"
		end
		, this = function (self)
			return self
		end
	}
	local derived = {
		super = base
		, __tostring = function (x)
			return "my name is " .. x.name
		end
		, name = "no-name"
		, type_name = function (self)
			return "derived"
		end
		, get_name = function (self)
			return self.name
		end
	}
	derived.__index = function (self, key)
		val = derived[key]
		if val then
			return val
		end
		return derived.super[key]
	end
	derived.new = function (table)
		table = table or {}
		setmetatable (table, derived)
		return table
	end
	x = derived.new ({name = "hello"})
	print (x)
	print (x:type_name ())
	print (x:get_name ())
	print (x:this ())
	print (x.super:type_name ())
end --}

lesson_meta_table ()
