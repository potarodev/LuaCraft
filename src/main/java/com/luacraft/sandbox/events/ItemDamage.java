package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class ItemDamage implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public ItemDamage(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void ItemDamageEvent(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnItemDamage");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("GetDamage", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getDamage());
                }
            });
            luaEvent.set("GetOriginalDamage", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getOriginalDamage());
                }
            });
            luaEvent.set("SetDamage", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue damage) {
                    event.setDamage(LuaErrorAssert.checkInt(damage, "SetDamage", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldDamage", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldDamage", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("Item", new ItemStackLib(item));
            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player), new ItemStackLib(item));
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
