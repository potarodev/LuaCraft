package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.block.BlockLib;
import com.luacraft.sandbox.damage.DamageSourceLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class EntityDamage implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public EntityDamage(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void OnEntityDamage(EntityDamageEvent event) {
        LuaValue entity = EntityTypeUtil.wrapEntity(event.getEntity());
        LuaValue attacker = EntityTypeUtil.wrapEntity(event.getDamageSource().getCausingEntity());
        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnEntityDamage");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Entity", entity);
            luaEvent.set("Attacker", attacker);
            luaEvent.set("GetCause", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getCause().toString());
                }
            });
            luaEvent.set("GetDamage", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getDamage());
                }
            });
            luaEvent.set("IsCritical", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event instanceof EntityDamageByEntityEvent e ? e.isCritical() : false);
                }
            });
            luaEvent.set("GetDamagerBlock", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return event instanceof EntityDamageByBlockEvent e ? new BlockLib(e.getDamager()) : LuaValue.NIL;
                }
            });
            luaEvent.set("GetDamageSource", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return new DamageSourceLib(event.getDamageSource());
                }
            });
            luaEvent.set("GetFinalDamage", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(event.getFinalDamage());
                }
            });
            luaEvent.set("ShouldDamage", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldDamage", 1, null));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetDamage", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue damage) {
                    event.setDamage(LuaErrorAssert.checkDouble(damage, "SetDamage", 1, null));

                    return LuaValue.NIL;
                }
            });

            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, entity, attacker);
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