package com.luacraft.sandbox.entity;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.destroystokyo.paper.entity.Pathfinder;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class PathFinderLib extends LuaTable {
    public PathFinderLib(Pathfinder pathfinder) {
        rawset("CanFloat", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(pathfinder.canFloat());
            }
        });

        rawset("CanOpenDoors", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(pathfinder.canOpenDoors());
            }
        });

        rawset("CanPassDoors", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(pathfinder.canPassDoors());
            }
        });

        rawset("FindPath", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue goal) {
                if (goal instanceof LocationLib) {
                    Location location = ((LocationLib) goal).getLocation();

                    Pathfinder.PathResult result = pathfinder.findPath(location);

                    return new PathFinderResultLib(result);
                } else if(goal instanceof LivingEntityLib) {
                    LivingEntity entity = ((LivingEntityLib) goal).getLivingEntity();

                    Pathfinder.PathResult result = pathfinder.findPath(entity);

                    return new PathFinderResultLib(result);
                } else {
                    throw new LuaError("[LuaCraft] FindPath() requires a Location or Entity object");
                }
            }
        });

        rawset("GetCurrentPath", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new PathFinderResultLib(pathfinder.getCurrentPath());
            }
        });

        rawset("GetEntity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return EntityTypeUtil.wrapEntity(pathfinder.getEntity());
            }
        });

        rawset("HasPath", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(pathfinder.hasPath());
            }
        });

        rawset("MoveTo", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue goal, LuaValue speed) {
                double spd = (speed.isnil()) ? 1 : LuaErrorAssert.checkDouble(speed, "MoveTo", 2, null);
                
                switch (goal) {
                    case PathFinderResultLib result -> pathfinder.moveTo(result.getPathResult(), spd);
                    case LivingEntityLib target -> pathfinder.moveTo(target.getLivingEntity(), spd);
                    case LocationLib location -> pathfinder.moveTo(location.getLocation(), spd);
                    default -> throw new LuaError("[LuaCraft] MoveTo() requires either a PathFinderResult, LivingEntity or a Location as the goal");
                }

                return new PathFinderLib(pathfinder);
            }
        });

        rawset("SetCanFloat", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue can) {
                pathfinder.setCanFloat(LuaErrorAssert.checkBoolean(can, "SetCanFloat", 1, null));

                return new PathFinderLib(pathfinder);
            }
        });

        rawset("SetCanOpenDoors", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue can) {
                pathfinder.setCanOpenDoors(LuaErrorAssert.checkBoolean(can, "SetCanOpenDoors", 1, null));

                return new PathFinderLib(pathfinder);
            }
        });

        rawset("SetCanPassDoors", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue can) {
                pathfinder.setCanPassDoors(LuaErrorAssert.checkBoolean(can, "SetCanPassDoors", 1, null));

                return new PathFinderLib(pathfinder);
            }
        });

        rawset("StopPathfinding", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                pathfinder.stopPathfinding();

                return new PathFinderLib(pathfinder);
            }
        });
    }
}