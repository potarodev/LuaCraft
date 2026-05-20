package com.luacraft.sandbox.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class EntityDeath implements Listener {
    private Map<String, Globals> allGlobals = new HashMap<>();

    public EntityDeath(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent event) {
        LuaValue entity = EntityTypeUtil.wrapEntity(event.getEntity());

        for (Map.Entry<String, Globals> entry : allGlobals.entrySet()) {
            LuaValue serverEvent = entry.getValue().get("ServerEvent");
            LuaValue function = serverEvent.get("OnEntityDeath");

            LuaTable luaEvent = new LuaTable();
            luaEvent.set("Entity", entity);
            luaEvent.set("ShouldDie", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setCancelled(!LuaErrorAssert.checkBoolean(should, "ShouldDie", 1, null));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetDeathSound", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue sound) {
                    NamespacedKey key = NamespacedKey.fromString(LuaErrorAssert.checkString(sound, "SetDeathSound", 1, null));
                    Sound newSound = Registry.SOUNDS.get(key);
                    event.setDeathSound(newSound);

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetDeathSoundCategory", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue soundCategory) {
                    SoundCategory category = SoundCategory.valueOf(LuaErrorAssert.checkString(soundCategory, "SetDeathSoundCategory", 1, null));
                    event.setDeathSoundCategory(category);

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetDeathSoundPitch", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue pitch) {
                    event.setDeathSoundPitch(LuaErrorAssert.checkFloat(pitch, "setDeathSoundPitch", 1, null));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetDeathSoundVolume", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue volume) {
                    event.setDeathSoundVolume(LuaErrorAssert.checkFloat(volume, "SetDeathSoundVolume", 1, null));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetDroppedEXP", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue exp) {
                    event.setDroppedExp(LuaErrorAssert.checkInt(exp, "SetDroppedEXP", 1, null));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("SetReviveHealth", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue health) {
                    event.setReviveHealth(LuaErrorAssert.checkDouble(health, "SetReviveHealth", 1, null));

                    return LuaValue.NIL;
                }
            });
            luaEvent.set("ShouldPlayDeathSound", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue should) {
                    event.setShouldPlayDeathSound(LuaErrorAssert.checkBoolean(should, "ShouldPlayDeathSound", 1, null));

                    return LuaValue.NIL;
                }
            });
            if (!function.isnil() && function.isfunction()) {
                try {
                    function.call(luaEvent, entity);
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
