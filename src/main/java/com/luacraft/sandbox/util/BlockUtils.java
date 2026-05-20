package com.luacraft.sandbox.util;

import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;

public class BlockUtils extends LuaTable {
    public BlockUtils() {
        rawset(LuaValue.valueOf("GetAllBlocks"), new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable table = new LuaTable();

                for (Material mat : Material.values()) {
                    if (mat.isBlock()) {
                        table.set(table.length() + 1, mat.name());
                    }
                }

                return table;
            }
        });

        rawset(LuaValue.valueOf("GetPrettyName"), new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue blockMaterial) {
                String prettyName = WordUtils.capitalizeFully(LuaErrorAssert.checkString(blockMaterial, "GetPrettyName", 1, null).toString().replace("_", " "));

                return LuaValue.valueOf(prettyName);
            }
        });
    }
}