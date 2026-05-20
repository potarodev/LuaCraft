package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class CraftItem implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public CraftItem(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnCraftItem(CraftItemEvent event) {
        LuaValue player;
        LuaValue result = new ItemStackLib(event.getRecipe().getResult());
        if (event.getWhoClicked() instanceof Player ply) {
            player = new PlayerLib(ply);
        } else {
            player = LuaValue.NIL;
        }

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnCraft");

            LuaValue luaEvent = new LuaTable();
            luaEvent.set("ShouldCraft", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue can) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(can, "CanCraft", 1, (Player) event.getWhoClicked()));

                    return LuaValue.NIL;
                }
            });
            

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, player, result);
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
