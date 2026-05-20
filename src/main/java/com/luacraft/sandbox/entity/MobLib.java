package com.luacraft.sandbox.entity;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.inventory.EquipmentLib;
import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class MobLib extends LivingEntityLib {
    private Mob mob;

    public MobLib(Mob mob) {
        super(mob);

        this.mob = mob;

        rawset("GetAmbientSound", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.getAmbientSound().toString());
            }
        });

        rawset("GetEquipment", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new EquipmentLib(mob.getEquipment());
            }
        });

        rawset("GetHeadRotationSpeed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.getHeadRotationSpeed());
            }
        });

        rawset("GetMaxHeadPitch", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.getMaxHeadPitch());
            }
        });

        rawset("GetPossibleExperienceReward", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.getPossibleExperienceReward());
            }
        });

        rawset("GetTarget", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (mob.getTarget() == null) return LuaValue.NIL;

                return EntityTypeUtil.wrapEntity(mob.getTarget());
            }
        });

        rawset("IsAggressive", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.isAggressive());
            }
        });

        rawset("IsAware", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.isAware());
            }
        });

        rawset("IsInDaylight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.isInDaylight());
            }
        });

        rawset("IsLeftHanded", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mob.isLeftHanded());
            }
        });

        rawset("LookAtLocation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue loc) {
                Location location = ((LocationLib) loc).getLocation();

                mob.lookAt(location);

                return LuaValue.NIL;
            }
        });

        rawset("LookAtEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ent) {
                mob.lookAt(EntityTypeUtil.unwrapEntity(ent));

                return LuaValue.NIL;
            }
        });

        rawset("SetAggressive", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue aggressive) {
                mob.setAggressive(LuaErrorAssert.checkBoolean(aggressive, "SetAggressive", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetAware", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue aware) {
                mob.setAware(LuaErrorAssert.checkBoolean(aware, "SetAware", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetLeftHanded", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue leftHanded) {
                mob.setLeftHanded(LuaErrorAssert.checkBoolean(leftHanded, "SetLeftHanded", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetTarget", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ent) {
                LivingEntity entity = (LivingEntity) EntityTypeUtil.unwrapEntity(ent);
                mob.setTarget(entity);

                return LuaValue.NIL;
            }
        });

        rawset("GetPathFinder", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new PathFinderLib(mob.getPathfinder());
            }
        });
    }

    public Mob getMob() {
        return mob;
    }
}