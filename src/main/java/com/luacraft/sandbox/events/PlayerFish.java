package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.FishHookLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.inventory.EquipmentSlotLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class PlayerFish implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerFish(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void PlayerFishEvent(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getCaught();
        FishHook hook = event.getHook();

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnPlayerFish");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("Entity", EntityTypeUtil.wrapEntity(entity));
            luaEvent.set("Hook", new FishHookLib(hook));
            luaEvent.set("GetHand", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new EquipmentSlotLib(event.getHand());
                }
            });
            luaEvent.set("GetEXPToDrop", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getExpToDrop());
                }
            });
            luaEvent.set("SetEXPToDrop", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue exp) {
                    event.setExpToDrop(LuaErrorAssert.checkInt(exp, "SetEXPToDrop", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldFish", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldFish", 1, player));

                    return LuaValue.NIL;
                }
            });
            
            if (!function.isnil() && function.isfunction()) {
                try {
                    function.invoke(LuaValue.varargsOf(new LuaValue[]{luaEvent, new PlayerLib(player), EntityTypeUtil.wrapEntity(entity), EntityTypeUtil.wrapEntity(hook)}));
                } catch(LuaError e) {
                    String baseMsg = e.getMessage();
                    String trueLocation = "";

                    for (StackTraceElement element : e.getStackTrace()) {
                        String fileName = element.getFileName();
                        if (fileName != null && fileName.endsWith(".lua")) {
                            trueLocation = fileName + ":" + element.getLineNumber();
                            break;
                        }
                    }

                    if (!trueLocation.isEmpty()) {
                        if (baseMsg != null && baseMsg.contains("?")) {
                            baseMsg = trueLocation + " " + baseMsg;
                        } else {
                            baseMsg = trueLocation + " " + baseMsg;
                        }
                    }

                    Bukkit.getLogger().warning("[" + entry.getKey() + "] Lua Script Error: " + baseMsg);
                }
            }
        }
    }
}