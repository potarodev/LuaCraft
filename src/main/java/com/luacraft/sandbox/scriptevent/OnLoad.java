package com.luacraft.sandbox.scriptevent;

import java.util.Map;

import org.bukkit.Bukkit;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class OnLoad {

    public static void OnScriptLoad(Map<String, Globals> allGlobals) {
        for (Globals globals : allGlobals.values()) {
            LuaValue serverEvent = globals.get("ServerEvent");
            LuaValue function = serverEvent.get("OnLoad");

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call();
                } catch(LuaError e) {
                    Bukkit.getLogger().info("Lua Script Error: " + e.getMessage());
                }
            }
        }
    }

    public static void OnScriptUnLoad(Map<String, Globals> allGlobals) {
        for (Globals globals : allGlobals.values()) {
            LuaValue serverEvent = globals.get("ServerEvent");
            LuaValue function = serverEvent.get("OnUnload");

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call();
                } catch(LuaError e) {
                    Bukkit.getLogger().info("Lua Script Error: " + e.getMessage());
                }
            }
        }
    }
}