package com.luacraft.sandbox.util;

import java.util.Map;
import java.util.function.Consumer;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class EntityConfigUtil {
    public static void apply(LuaTable config, Map<String, Consumer<LuaValue>> configMap) {
        if (configMap == null) return;
        
        LuaValue key = LuaValue.NIL;
        while (true) {
            Varargs entry = config.next(key);
            key = entry.arg1();
            if (key.isnil()) break;

            Consumer<LuaValue> setter = configMap.get(key.tojstring());
            if (setter != null) setter.accept(entry.arg(2));
        }
    }
}