package com.luacraft.sandbox.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.block.BlockLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class PlayerBlockBreak implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerBlockBreak(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnBlockBreak");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("ShouldBreak", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldBreak", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldDropItems", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setDropItems(LuaErrorAssert.checkBoolean(should, "ShouldDropItems", 1, player));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("GetDrops", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    Collection<ItemStack> drops = block.getDrops();

                    LuaTable allDrops = new LuaTable();
                    int index = 1;

                    for (ItemStack drop : drops) {
                        allDrops.set(index++, new ItemStackLib(drop));
                    }

                    return allDrops;
                }
            });
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("Block", new BlockLib(block));

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player), new BlockLib(block));
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

        PersistentDataContainer pdc = block.getChunk().getPersistentDataContainer();
        Location loc = block.getLocation();

        String blockKeyPrefix = loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getWorld().getName() + "-";

        for (String metaKey : BlockLib.getKeys()) {
            NamespacedKey key = new NamespacedKey(LuaCraft.getPlugin(), blockKeyPrefix + metaKey);
            pdc.remove(key);
        }
    }
}
