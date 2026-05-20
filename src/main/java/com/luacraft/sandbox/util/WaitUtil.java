package com.luacraft.sandbox.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.LuaGuard;
import com.luacraft.ScriptLoader;

public class WaitUtil extends LuaTable {
    public static LuaFunction Wait(Plugin plugin, String fileName) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg, LuaValue callback) {
                if (!callback.isfunction()) return LuaValue.NIL;

                LuaGuard.prepare(10_000, fileName);

                int waitTime = arg.checkint();

                if (waitTime < 0) waitTime = 1;

                LuaValue func = callback.checkfunction();

                //Runnable task = new Runnable() {
                //    @Override
                //    public void run() {
                //        if (ScriptLoader.isDead(fileName)) return;
                //        func.call();
                //    }
                //    
                //};

                int generation = ScriptLoader.getGeneration(fileName);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (ScriptLoader.isDead(fileName)) return;
                    if (ScriptLoader.getGeneration(fileName) != generation) return;
                    func.call();
                }, waitTime);

                return LuaValue.NIL;
            }
        };
    }

    public static LuaFunction Halt(Plugin plugin, Globals globals, String fileName) {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ticks) {
                int waitTime = Math.max(LuaErrorAssert.checkInt(ticks, "Halt", 1, null), 1);

                LuaGuard.prepare(10_000, fileName);

                LuaThread thread = globals.running;

                if (thread == null || thread.isMainThread()) {
                    throw new LuaError("[LuaCraft] Halt() cannot be used in this scope, try using a Wait(number, callback) function instead");
                }

                int generation = ScriptLoader.getGeneration(fileName);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (ScriptLoader.isDead(fileName)) return;
                    if (ScriptLoader.getGeneration(fileName) != generation) return;
                    thread.resume(LuaValue.NIL);
                }, waitTime);
                
                return globals.get("coroutine").get("yield").call();
            }
        };
    }
}