package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.ComponentUtils;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public class AsyncChat implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public AsyncChat(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnAsyncChat");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Player", new PlayerLib(player));
            luaEvent.set("SetChatFormat", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue newFormat) {
                    Component component;

                    if (!newFormat.isnil()) {
                        component = ComponentUtils.luaValueToComponent(newFormat);

                        event.renderer((source, sourceDisplayName, message, viewer) -> component);
                    }

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("GetMessage", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    LuaComponent holder = new LuaComponent(event.message());
                    
                    return new ComponentLib(holder.getComponent());
                }
            });
            luaEvent.set("ShouldChat", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue shouldChat) {
                    if (shouldChat.isboolean() || shouldChat.isnil()) {
                        event.setCancelled(!LuaErrorAssert.checkBoolean(shouldChat, "ShouldChat", 1, player));
                    }

                    return LuaValue.NIL;
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, new PlayerLib(player), new ComponentLib(new LuaComponent(event.message()).getComponent()));
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
