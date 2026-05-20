package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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
import com.luacraft.sandbox.item.ItemStackLib;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;

public class PlayerInventorySlotChange implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerInventorySlotChange(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnPlayerInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        LuaValue player = new PlayerLib(event.getPlayer());
        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnPlayerInventorySlotChange");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", player);
            luaEvent.set("GetNewItemStack", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getNewItemStack());
                }
            });
            luaEvent.set("GetOldItemStack", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getOldItemStack());
                }
            });
            luaEvent.set("GetRawSlot", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getRawSlot());
                }
            });
            luaEvent.set("GetSlot", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getSlot());
                }
            });
            luaEvent.set("ShouldTriggerAdvancements", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setShouldTriggerAdvancements(LuaErrorAssert.checkBoolean(should, "ShouldTriggerAdvancements", 1, event.getPlayer()));

                    return LuaValue.NIL;
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, player);
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