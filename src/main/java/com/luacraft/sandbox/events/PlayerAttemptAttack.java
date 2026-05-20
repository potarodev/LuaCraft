package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;

public class PlayerAttemptAttack implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerAttemptAttack(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnPlayerAttemptAttack(PrePlayerAttackEntityEvent event) {
        Player player = event.getPlayer();
        Entity attacked = event.getAttacked();

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnPlayerAttemptAttack");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("ShouldAttack", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldAttack", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("IsAttackable", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.willAttack());
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player), EntityTypeUtil.wrapEntity(attacked));
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
