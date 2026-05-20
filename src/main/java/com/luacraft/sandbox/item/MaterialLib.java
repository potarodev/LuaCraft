package com.luacraft.sandbox.item;

import org.bukkit.Material;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;

public class MaterialLib extends LuaTable {
    public MaterialLib() {
        rawset("IsBlock", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsBlock", 1, null));
                
                return LuaValue.valueOf(material.isBlock());
            }
        });

        rawset("IsAir", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsAir", 1, null));

                return LuaValue.valueOf(material.isAir());
            }
        });

        rawset("IsBurnable", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsBurnable", 1, null));

                return LuaValue.valueOf(material.isBurnable());
            }
        });

        rawset("GetMaxStackSize", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "GetMaxStackSize", 1, null));

                return LuaValue.valueOf(material.getMaxStackSize());
            }
        });

        rawset("GetHardness", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "GetHardness", 1, null));

                return LuaValue.valueOf(material.getHardness());
            }
        });

        rawset("HasGravity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "HasGravity", 1, null));

                return LuaValue.valueOf(material.hasGravity());
            }
        });

        rawset("IsEdible", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsEdible", 1, null));

                return LuaValue.valueOf(material.isEdible());
            }
        });

        rawset("IsFuel", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsFuel", 1, null));

                return LuaValue.valueOf(material.isFuel());
            }
        });


        rawset("GetBlastResistance", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "GetBlastResistance", 1, null));

                return LuaValue.valueOf(material.getBlastResistance());
            }
        });

        rawset("IsCompostable", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsCompostable", 1, null));

                return LuaValue.valueOf(material.isCompostable());
            }
        });

        rawset("IsItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsItem", 1, null));

                return LuaValue.valueOf(material.isItem());
            }
        });

        rawset("IsSolid", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "IsSolid", 1, null));

                return LuaValue.valueOf(material.isSolid());
            }
        });

        rawset("GetMaxDurability", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue str) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(str, "GetMaxDurability", 1, null));

                return LuaValue.valueOf(material.getMaxDurability());
            }
        });
    }
}