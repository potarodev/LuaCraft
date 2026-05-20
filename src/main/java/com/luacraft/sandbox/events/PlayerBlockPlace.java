package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.block.BlockLib;
import com.luacraft.sandbox.block.BlockStateLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.inventory.EquipmentSlotLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class PlayerBlockPlace implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerBlockPlace(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnBlockPlace");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("Block", new BlockLib(block));
            luaEvent.set("CanBuild", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.canBuild());
                }
            });
            luaEvent.set("GetBlockAgainst", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new BlockLib(event.getBlockAgainst());
                }
            });
            luaEvent.set("GetHand", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new EquipmentSlotLib(event.getHand());
                }
            });
            luaEvent.set("GetItemInHand", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new ItemStackLib(event.getItemInHand());
                }
            });
            luaEvent.set("ShouldBuild", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setBuild(LuaErrorAssert.checkBoolean(should, "ShouldBuild", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldPlace", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldPlace", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("GetBlockReplacedState", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new BlockStateLib(event.getBlockReplacedState());
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player), new BlockLib(block));
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
