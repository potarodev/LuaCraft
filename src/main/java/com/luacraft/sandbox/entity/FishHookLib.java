package com.luacraft.sandbox.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.joml.Math;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class FishHookLib extends EntityLib {
    private final FishHook hook;
    public FishHookLib(FishHook hook) {
        super(hook);
        this.hook = hook;

        rawset("GetApplyLure", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getApplyLure());
            }
        });

        rawset("GetHookedEntity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return EntityTypeUtil.wrapEntity(hook.getHookedEntity());
            }
        });

        rawset("GetMaxLureAngle", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getMaxLureAngle());
            }
        });

        rawset("GetMaxLureTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getMaxLureTime());
            }
        });

        rawset("GetMaxWaitTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getMaxWaitTime());
            }
        });

        rawset("GetMinLureAngle", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getMinLureAngle());
            }
        });

        rawset("GetMinLureTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getMinLureTime());
            }
        });

        rawset("GetMinWaitTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getMinWaitTime());
            }
        });

        rawset("GetTimeUntilBite", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getTimeUntilBite());
            }
        });

        rawset("GetWaitTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.getWaitTime());
            }
        });

        rawset("IsInOpenWater", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.isInOpenWater());
            }
        });

        rawset("IsRainInfluenced", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.isRainInfluenced());
            }
        });

        rawset("IsSkyInfluenced", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hook.isSkyInfluenced());
            }
        });

        rawset("PullHookedEntity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                hook.pullHookedEntity();

                return LuaValue.NIL;
            }
        });

        rawset("ResetFishingState", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                hook.resetFishingState();

                return LuaValue.NIL;
            }
        });

        rawset("SetApplyLure", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue applied) {
                hook.setApplyLure(LuaErrorAssert.checkBoolean(applied, "SetApplyLure", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetHookedEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue entity) {
                Entity ent = ((EntityLib) entity).getEntity();
                hook.setHookedEntity(ent);

                return LuaValue.NIL;
            }
        });

        rawset("SetLureAngle", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue min, LuaValue max) {
                hook.setLureAngle(LuaErrorAssert.checkFloat(min, "SetLureAngle", 1, null), LuaErrorAssert.checkFloat(max, "SetLureAngle", 2, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetLureTime", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue min, LuaValue max) {
                hook.setLureTime(LuaErrorAssert.checkInt(min, "SetLureTime", 1, null), LuaErrorAssert.checkInt(max, "SetLureTime", 2, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetMaxLureAngle", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue max) {
                hook.setMaxLureAngle(LuaErrorAssert.checkFloat(max, "SetMaxLureAngle", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetMaxLureTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue max) {
                hook.setMaxLureTime(LuaErrorAssert.checkInt(max, "SetMaxLureTime", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetMaxWaitTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue max) {
                hook.setMaxWaitTime(LuaErrorAssert.checkInt(max, "SetMaxWaitTime", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetMinLureAngle", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue min) {
                hook.setMinLureAngle(LuaErrorAssert.checkFloat(min, "SetMinLureAngle", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetMinLureTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue min) {
                hook.setMinLureTime(LuaErrorAssert.checkInt(min, "SetMinLureTime", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetMinWaitTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue min) {
                hook.setMinWaitTime(LuaErrorAssert.checkInt(min, "SetMinWaitTime", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetRainInfluenced", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue influenced) {
                hook.setRainInfluenced(LuaErrorAssert.checkBoolean(influenced, "SetRainInfluenced", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetSkyInfluenced", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue influenced) {
                hook.setSkyInfluenced(LuaErrorAssert.checkBoolean(influenced, "SetSkyInfluenced", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetTimeUntilBite", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue time) {
                hook.setTimeUntilBite(Math.clamp(1, Integer.MAX_VALUE, LuaErrorAssert.checkInt(time, "SetTimeUntilBite", 1, null)));

                return LuaValue.NIL;
            }
        });

        rawset("SetWaitTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue time) {
                hook.setWaitTime(LuaErrorAssert.checkInt(time, "SetWaitTime", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetMinMaxWaitTime", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue min, LuaValue max) {
                hook.setWaitTime(LuaErrorAssert.checkInt(min, "SetMinMaxWaitTime", 1, null), LuaErrorAssert.checkInt(max, "SetMinMaxWaitTime", 2, null));

                return LuaValue.NIL;
            }
        });
    }

    public FishHook getFishHook() {
        return hook;
    }
}