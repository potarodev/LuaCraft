package com.luacraft.sandbox.particle;

import org.bukkit.Location;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.location.LocationLib;

public class ParticleFactory extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue name, LuaValue location) {
        Location loc = ((LocationLib) location).getLocation();
        
        return new ParticleLib(LuaErrorAssert.checkString(name, "Particle", 1, null), loc);
    }
}