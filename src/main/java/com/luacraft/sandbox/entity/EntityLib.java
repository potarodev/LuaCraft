package com.luacraft.sandbox.entity;

import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.ComponentUtils;
import com.luacraft.sandbox.util.EntityTypeUtil;
import com.luacraft.sandbox.vector.VectorLib;
import com.luacraft.sandbox.world.WorldLib;

import net.kyori.adventure.text.Component;

public class EntityLib extends LuaTable {
    private Entity entity;

    public Map<String, Consumer<LuaValue>> getConfigMap() {
        return null;
    }

    public EntityLib(Entity entity) {
        this.entity = entity;

        rawset("GetLocation", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(entity.getLocation());
            }
        });

        rawset("Teleport", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue location) {
                Location newLocation = ((LocationLib) location).getLocation();

                entity.teleport(newLocation);

                return EntityLib.this;
            }
        });

        rawset("SetGlow", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue glow) {
                entity.setGlowing(LuaErrorAssert.checkBoolean(glow, "SetGlow", 1, null));

                return EntityLib.this;
            }
        });

        rawset("SetVelocity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue velocity) {
                Vector vel = ((VectorLib) velocity).getVector();

                entity.setVelocity(vel);

                return EntityLib.this;
            }
        });

        rawset("GetVelocity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new VectorLib(entity.getVelocity());
            }
        });

        rawset("SetVisibleByDefault", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue visible) {

                entity.setVisibleByDefault(LuaErrorAssert.checkBoolean(visible, "SetVisisbleByDefault", 1, null));

                return EntityLib.this;
            }
        });

        rawset("SetInvisible", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue invisible) {
                entity.setInvisible(LuaErrorAssert.checkBoolean(invisible, "SetInvisible", 1, null));

                return EntityLib.this;
            }
        });

        rawset("Ride", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue otherEntity) {
                EntityTypeUtil.unwrapEntity(otherEntity).addPassenger(entity);

                return EntityLib.this;
            }
        });

        rawset("Kill", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                entity.remove();

                return EntityLib.this;
            }
        });

        rawset("IsPlayer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity instanceof Player);
            }
        });

        rawset("GetWorld", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new WorldLib(entity.getWorld());
            }
        });

        rawset("SetName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue newName) {
                Component name = ComponentUtils.luaValueToComponent(newName);

                entity.customName(name);

                return EntityLib.this;
            }
        });

        rawset("GetName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getName());
            } 
        });

        rawset("SetNameVisibility", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue visibility) {
                boolean isVisible = LuaErrorAssert.checkBoolean(visibility, "SetNameVisiblity", 1, null);
                    
                entity.setCustomNameVisible(isVisible);

                return EntityLib.this;
            }
        });

        rawset("SetRotation", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue yaw, LuaValue pitch) {
                entity.setRotation(LuaErrorAssert.checkFloat(yaw, "SetRotation", 1, null), LuaErrorAssert.checkFloat(pitch, "SetRotation", 2, null));
                
                return EntityLib.this;
            }
        });

        rawset("AddTag", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue tag) {
                entity.addScoreboardTag(LuaErrorAssert.checkString(tag, "AddTag", 1, null));

                return EntityLib.this;
            }
        });

        rawset("RemoveTag", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue tag) {
                entity.removeScoreboardTag(LuaErrorAssert.checkString(tag, "RemoveTag", 1, null));

                return EntityLib.this;
            }
        });

        rawset("HasTag", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue tag) {
                return LuaValue.valueOf(entity.getScoreboardTags().contains(LuaErrorAssert.checkString(tag, "HasTag", 1, null)));
            }
        });

        rawset("IsInWater", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.isInWater());
            }
        });
    }

    public Entity getEntity() {
        return entity;
    }
}