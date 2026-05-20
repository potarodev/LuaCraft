package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.inventory.EquipmentSlotLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class PlayerArmorChange implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public PlayerArmorChange(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack oldItem = event.getOldItem();
        ItemStack newItem = event.getNewItem();
        EquipmentSlot slot = event.getSlot();
        
        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnPlayerArmorChange");

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.invoke(LuaValue.varargsOf(new LuaValue[]{new PlayerLib(player), new ItemStackLib(oldItem), new ItemStackLib(newItem), new EquipmentSlotLib(slot)}));
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