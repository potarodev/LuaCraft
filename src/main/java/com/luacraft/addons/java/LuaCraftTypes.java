package com.luacraft.addons.java;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaValue;

import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.EntityTypeUtil;
import com.luacraft.sandbox.vector.VectorLib;

public class LuaCraftTypes {
    public static LuaValue wrapEntity(Entity entity) {
        return EntityTypeUtil.wrapEntity(entity);
    }

    public static Object unwrapEntity(LuaValue value) {
        return EntityTypeUtil.unwrapEntity(value);
    }

    public static LuaValue wrapVector(Vector vector) {
        return new VectorLib(vector);
    }
    
    public static Object unwrapVector(LuaValue value) {
        return ((VectorLib) value).getVector();
    }

    public static LuaValue wrapLocation(Location location) {
        return new LocationLib(location);
    }

    public static Object unwrapLocation(LuaValue value) {
        return ((LocationLib) value).getLocation();
    }
}