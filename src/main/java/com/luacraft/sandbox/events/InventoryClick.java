package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.inventory.InventoryLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class InventoryClick implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public InventoryClick(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnInventoryClick");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("GetClickType", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getClick().toString());
                }
            });
            luaEvent.set("GetClickedInventory", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new InventoryLib(event.getClickedInventory());
                }
            });
            luaEvent.set("GetSlotItem", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getCurrentItem());
                }
            });
            luaEvent.set("GetCursorItem", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getCursor());
                }
            });
            luaEvent.set("GetHotbarButton", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getHotbarButton());
                }
            });
            luaEvent.set("GetSlot", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getSlot());
                }
            });
            luaEvent.set("GetTitle", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ComponentLib(event.getView().title());
                }
            });
            luaEvent.set("IsLeftclick", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isLeftClick());
                }
            });
            luaEvent.set("IsRightClick", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isRightClick());
                }
            });
            luaEvent.set("IsShiftClick", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isShiftClick());
                }
            });
            luaEvent.set("ShouldInteract", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldInteract", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("Inventory", new InventoryLib(event.getInventory()));
            luaEvent.set("Player", new PlayerLib(player));
            
            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player), new InventoryLib(event.getInventory()));
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
