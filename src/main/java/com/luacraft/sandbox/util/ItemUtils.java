package com.luacraft.sandbox.util;

import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;

public class ItemUtils extends LuaTable {
    public ItemUtils() {
        rawset(LuaValue.valueOf("GetAllItems"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable table = new LuaTable();

                for (Material mat : Material.values()) {
                    if (mat.isItem()) {
                        table.set(table.length() + 1, mat.name());
                    }
                }

                return table;
            }
        });

        rawset(LuaValue.valueOf("GetPrettyName"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                String prettyName = WordUtils.capitalizeFully(LuaErrorAssert.checkString(item, "GetPrettyName", 1, null).toString().replace("_", " "));

                return LuaValue.valueOf(prettyName);
            }
        });
    }
}
