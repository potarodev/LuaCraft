package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class EntitySpawn implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public EntitySpawn(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void EntitySpawnEvent(EntitySpawnEvent event) {
        LuaValue entity = EntityTypeUtil.wrapEntity(event.getEntity());

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnEntitySpawn");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Entity", entity);
            luaEvent.set("GetLocation", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new LocationLib(event.getLocation());
                }
            });
            luaEvent.set("ShouldSpawn", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldSpawn", 1, null));

                    return LuaValue.NIL;
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, entity);
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
