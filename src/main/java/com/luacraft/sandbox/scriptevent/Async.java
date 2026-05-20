package com.luacraft.sandbox.scriptevent;

import org.bukkit.Bukkit;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;

public class Async extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue callback) {
        Bukkit.getScheduler().runTaskAsynchronously(LuaCraft.getPlugin(), () -> {
            LuaErrorAssert.checkFunction(callback, "Async", 1, null).call();

        });
        
        return LuaValue.NIL;
    }
}
