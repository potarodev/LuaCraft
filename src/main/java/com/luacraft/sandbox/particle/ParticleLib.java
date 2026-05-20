package com.luacraft.sandbox.particle;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.destroystokyo.paper.ParticleBuilder;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.color.ColorLib;
import com.luacraft.sandbox.vector.VectorLib;

public class ParticleLib extends LuaTable {
    private ParticleBuilder builder;
    private Color pendingColor;
    private Float pendingSize;

    public ParticleLib(String type, Location location) {
        Particle particle = Particle.valueOf(type);
        
        builder = new ParticleBuilder(particle).location(location);

        rawset("Count", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue num) {
                builder = builder.count(LuaErrorAssert.checkInt(num, "Count", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("Offset", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue offset) {
                Vector vec = ((VectorLib) offset).getVector();

                builder = builder.offset(vec.getX(), vec.getY(), vec.getZ());

                return LuaValue.NIL;
            }
        });

        rawset("Color", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue color) {
                ColorLib colorLib = (ColorLib) color.checkuserdata(ColorLib.class);
                
                if (builder.particle().getDataType() == Particle.DustOptions.class || builder.particle().getDataType() == Color.class) {
                    pendingColor = colorLib.getBukkitColor();
                } else {
                    throw new LuaError("Particle " + builder.particle().name() + " type does not support Coloring");
                }
                
                return LuaValue.NIL;
            }
        });

        rawset("Size", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue size) {
                float sz = LuaErrorAssert.checkFloat(size, "Size", 1, null);

                if (builder.particle().getDataType() == Particle.DustOptions.class || builder.particle().getDataType() == Color.class) {
                    pendingSize = sz;
                } else {
                    throw new LuaError("Particle " + builder.particle().name() + " type does not support a custom size");
                }

                return LuaValue.NIL;
            }
        });

        rawset("Power", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue power) {
                builder = builder.data(LuaErrorAssert.checkFloat(power, "Power", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("Receivers", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue radius) {
                builder = builder.receivers(LuaErrorAssert.checkInt(radius, "Receivers", 1, null), true);

                return LuaValue.NIL;
            }
        });

        rawset("Spawn", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (pendingColor != null || pendingSize != null) {
                    builder = builder.color(pendingColor != null ? pendingColor : Color.WHITE, pendingSize != null ? pendingSize : 1.0f);
                }

                builder.spawn();

                return LuaValue.NIL;
            }
        });
    }
}