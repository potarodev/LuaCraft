package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.location.LocationLib;

public class PlayerRespawn implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerRespawn(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnPlayerQuit(PlayerRespawnEvent event) {
        LuaValue player = new PlayerLib(event.getPlayer());
        LuaValue location = new LocationLib(event.getRespawnLocation());

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnPlayerSpawn");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", player);
            luaEvent.set("Location", location);
            luaEvent.set("SetRespawnLocation", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue loc) {
                    event.setRespawnLocation(((LocationLib) loc).getLocation());

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("IsBedSpawn", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isBedSpawn());
                }
            });
            luaEvent.set("IsAnchorSpawn", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isAnchorSpawn());
                }
            });
            luaEvent.set("IsMissingRespawnBlock", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isMissingRespawnBlock());
                }
            });
            luaEvent.set("GetRespawnReason", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getRespawnReason().toString());
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, player, location);
                } catch(LuaError e) {
                    String baseMsg = e.getMessage();
                    String trueLocation = "";

                    for (StackTraceElement element : e.getStackTrace()) {
                        String fileName = element.getFileName();
                        if (fileName != null && fileName.endsWith(".lua")) {
                            trueLocation = fileName + " related to line number " + element.getLineNumber();
                            break;
                        }
                    }

                    if (!trueLocation.isEmpty()) {
                        if (baseMsg != null && baseMsg.contains("?")) {
                            baseMsg = trueLocation + ": " + baseMsg;
                        } else {
                            baseMsg = trueLocation + ": " + baseMsg;
                        }
                    }

                    Bukkit.getLogger().warning("Lua Script Error: " + baseMsg);
                }
            }
        }
    }
}
