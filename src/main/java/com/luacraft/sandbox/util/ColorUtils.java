package com.luacraft.sandbox.util;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.color.ColorLib;

public class ColorUtils extends LuaTable {
    public static LuaFunction Color() {
        return new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue r, LuaValue g, LuaValue b) {
                int red = LuaErrorAssert.checkInt(r, "Color", 1, null);
                int green = LuaErrorAssert.checkInt(g, "Color", 2, null);
                int blue = LuaErrorAssert.checkInt(b, "Color", 3, null);
                return LuaValue.userdataOf(new ColorLib(red, green, blue));
            }
        };
    }
}