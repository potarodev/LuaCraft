package com.luacraft.sandbox.entity;

import org.bukkit.Location;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.destroystokyo.paper.entity.Pathfinder.PathResult;
import com.luacraft.sandbox.location.LocationLib;

public class PathFinderResultLib extends LuaTable {
    private PathResult result;
    public PathFinderResultLib(PathResult pathResult) {
        this.result = pathResult;

        rawset("CanReachFinalPoint", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(pathResult.canReachFinalPoint());
            }
        });

        rawset("GetFinalPoint", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(pathResult.getFinalPoint());
            }
        });

        rawset("GetNextPoint", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(pathResult.getNextPoint());
            }
        });

        rawset("GetNextPointIndex", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(pathResult.getNextPointIndex());
            }
        });

        rawset("GetPoints", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable points = new LuaTable();

                for (Location location : pathResult.getPoints()) {
                    points.insert(points.length() + 1, new LocationLib(location));
                }

                return points;
            }
        });
    }

    public PathResult getPathResult() {
        return result;
    }
}