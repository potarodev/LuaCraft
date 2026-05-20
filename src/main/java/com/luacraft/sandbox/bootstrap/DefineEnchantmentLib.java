package com.luacraft.sandbox.bootstrap;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import com.luacraft.LuaErrorAssert;

public class DefineEnchantmentLib extends LuaTable {
    public static LuaValue DefineEnchantment() {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue config) {
                EnchantmentRegistry.define(new EnchantmentDefinitions(LuaErrorAssert.checkString(key, "DefineEnchantment", 1, null), LuaErrorAssert.checkTable(config, "DefineEnchantment", 2, null)));
                return LuaValue.NIL;
            }
        };
    }
}
