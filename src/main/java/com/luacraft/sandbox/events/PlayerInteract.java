package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.block.BlockLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.inventory.EquipmentSlotLib;
import com.luacraft.sandbox.item.ItemStackLib;
import com.luacraft.sandbox.location.LocationLib;

public class PlayerInteract implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerInteract(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnPlayerInteract");
            
            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("GetActionType", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getAction().toString());
                }
            });
            luaEvent.set("GetClickedBlock", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new BlockLib(event.getClickedBlock());
                }
            });
            luaEvent.set("GetHand", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new EquipmentSlotLib(event.getHand());
                }
            });
            luaEvent.set("GetInteractionPoint", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new LocationLib(event.getInteractionPoint());
                }
            });
            luaEvent.set("GetItem", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    ItemStack item = event.getItem();
                    if (item == null || item.getType().isAir()) {
                        return LuaValue.NIL;
                    }

                    return new ItemStackLib(item);
                }
            });
            luaEvent.set("InvolvedBlock", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasBlock());
                }
            });
            luaEvent.set("InvolvedItem", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.hasItem());
                }
            });
            luaEvent.set("IsBlockInHand", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.isBlockInHand());
                }
            });
            luaEvent.set("ShouldInteract", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldInteract", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldUseInteractedBlock", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    String can = "DEFAULT";

                    if (should.toboolean()) {
                        can = "ALLOW";
                    } else {
                        can = "DENY";
                    }

                    Event.Result result = Event.Result.valueOf(can);

                    event.setUseInteractedBlock(result);

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldUseItemInHand", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    String can = "DEFAULT";

                    if (should.toboolean()) {
                        can = "ALLOW";
                    } else {
                        can = "DENY";
                    }

                    Event.Result result = Event.Result.valueOf(can);

                    event.setUseItemInHand(result);

                    return LuaValue.NIL;
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player));
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
