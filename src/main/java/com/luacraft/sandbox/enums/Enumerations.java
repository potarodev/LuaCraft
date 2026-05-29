package com.luacraft.sandbox.enums;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class Enumerations {
    public static LuaTable BlockEnums() {
        LuaTable blockEnums = new LuaTable();

        for (Material mat : Registry.MATERIAL) {
            if (!mat.isBlock()) continue;
            
            blockEnums.set(mat.toString(), LuaValue.valueOf(mat.toString()));
        }

        return blockEnums;
    }

    public static LuaTable CommandTypeEnums() {
        LuaTable commandTypeEnums = new LuaTable();

        commandTypeEnums.set("PLAYER", LuaValue.valueOf("PLAYER"));
        commandTypeEnums.set("INTEGER", LuaValue.valueOf("INTEGER"));
        commandTypeEnums.set("NUMBER", LuaValue.valueOf("INTEGER"));
        commandTypeEnums.set("STRING", LuaValue.valueOf("STRING"));
        commandTypeEnums.set("TEXT", LuaValue.valueOf("STRING"));
        commandTypeEnums.set("WORD", LuaValue.valueOf("WORD"));
        commandTypeEnums.set("GREEDY", LuaValue.valueOf("GREEDYSTRING"));
        commandTypeEnums.set("BOOLEAN", LuaValue.valueOf("BOOLEAN"));
        commandTypeEnums.set("BOOL", LuaValue.valueOf("BOOLEAN"));

        return commandTypeEnums;
    }

    public static LuaTable ItemEnums() {
        LuaTable itemEnums = new LuaTable();

        for (Material mat : Registry.MATERIAL) {
            if (!mat.isItem()) continue;

            itemEnums.set(mat.toString(), LuaValue.valueOf(mat.toString()));
        }

        return itemEnums;
    }
} 
