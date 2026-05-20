package com.luacraft.sandbox.damage;

import org.bukkit.damage.DamageSource;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class DamageSourceLib extends LuaTable {
    public DamageSourceLib(DamageSource source) {
        rawset("GetCausingEntity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return EntityTypeUtil.wrapEntity(source.getCausingEntity());
            }
        });
        
        rawset("GetDamageLocation", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(source.getDamageLocation());
            }
        });

        rawset("GetDamageType", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(source.getDamageType().toString());
            }
        });

        rawset("GetDirectCausingEntity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return EntityTypeUtil.wrapEntity(source.getDirectEntity());
            }
        });

        rawset("GetFoodExhaustion", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(source.getFoodExhaustion());
            }
        });

        rawset("GetSourceLocation", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(source.getSourceLocation());
            }
        });

        rawset("IsIndirect", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(source.isIndirect());
            }
        });

        rawset("ScalesWithDifficulty", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(source.scalesWithDifficulty());
            }
        });
    }
}
