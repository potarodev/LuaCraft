package com.luacraft.sandbox.vector;

import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;

public class VectorLib extends LuaTable {
    private final Vector vector;

    public static LuaTable createTable() {
        LuaTable vec = new LuaTable();

        vec.set("New", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
                Vector vector = new Vector(LuaErrorAssert.checkDouble(x, "Vector", 1, null), LuaErrorAssert.checkDouble(y, "Vector", 2, null), LuaErrorAssert.checkDouble(z, "Vector", 3, null));

                return new VectorLib(vector); 
            }
        });

        return vec;
    }

    public VectorLib(Vector vector) {
        this.vector = vector;

        rawset("GetX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getX());
            }
        });

        rawset("GetY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getY());
            }
        });

        rawset("GetZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getZ());
            }
        });

        rawset("SetX", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue x) {
                vector.setX(LuaErrorAssert.checkFloat(x, "SetX", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetY", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue y) {
                vector.setX(LuaErrorAssert.checkFloat(y, "SetY", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetZ", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue z) {
                vector.setX(LuaErrorAssert.checkFloat(z, "SetZ", 1, null));

                return LuaValue.NIL;
            }
        });
    }

    public Vector getVector() {
        return vector;
    }
}
