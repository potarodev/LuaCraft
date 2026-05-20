package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.ComponentUtils;
import com.luacraft.sandbox.util.EntityTypeUtil;

import net.kyori.adventure.text.Component;

public class PlayerDeath implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerDeath(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnPlayerDeath");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("Killer", EntityTypeUtil.wrapEntity(event.getDamageSource().getCausingEntity()));
            luaEvent.set("SetDeathMessage", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue message) {
                    if (message.isnil()) {
                        event.deathMessage(null);
                    } else {
                        Component component = ComponentUtils.luaValueToComponent(message);
                        event.deathMessage(component);
                    }
                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldKeepInventory", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setKeepInventory(LuaErrorAssert.checkBoolean(should, "ShouldKeepInventory", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldKeepLevel", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setKeepLevel(LuaErrorAssert.checkBoolean(should, "ShouldKeepLevel", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetNewEXP", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue exp) {
                    event.setNewExp(LuaErrorAssert.checkInt(exp, "SetNewEXP", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetNewLevel", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue level) {
                    event.setNewLevel(LuaErrorAssert.checkInt(level, "SetNewLevel", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetNewTotalEXP", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue totalEXP) {
                    event.setNewTotalExp(LuaErrorAssert.checkInt(totalEXP, "SetNewTotalEXP", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldDropEXP", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setShouldDropExperience(LuaErrorAssert.checkBoolean(should, "ShouldDropEXP", 1, player));

                    return LuaValue.NIL;
                }
            });
            
            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player), EntityTypeUtil.wrapEntity(event.getDamageSource().getCausingEntity()));
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
