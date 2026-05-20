package com.luacraft.sandbox.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.block.BlockLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.EntityTypeUtil;
import com.luacraft.sandbox.vector.VectorLib;
import com.luacraft.sandbox.world.WorldLib;

public class BoundsLib extends LuaTable {
    BoundingBox boundingBox;

    public static LuaFunction boundsFactory() {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue loc1, LuaValue loc2) {
                Location location1 = ((LocationLib) loc1).getLocation();
                Location location2 = ((LocationLib) loc2).getLocation();
                World world = location1.getWorld();

                BoundingBox boundingBox = BoundingBox.of(location1, location2);

                return new BoundsLib(boundingBox, world);
            }
        };
    }

    public BoundsLib(BoundingBox boundingBox, World world) {
        this.boundingBox = boundingBox;

        rawset("GetCenter", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Location location = new Location(world, boundingBox.getCenterX(), boundingBox.getCenterY(), boundingBox.getCenterZ());
                return new LocationLib(location);
            }
        });

        rawset("Expand", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue vec) {
                Vector vector = ((VectorLib) vec).getVector();

                boundingBox.expand(vector);

                return LuaValue.NIL;
            }
        });

        rawset("Contains", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue loc) {
                Location location = ((LocationLib) loc).getLocation();
            
                return LuaValue.valueOf(boundingBox.contains(location.x(), location.y(), location.z()));
            }
        });

        rawset("GetWorld", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new WorldLib(world);
            }
        });

        rawset("Overlaps", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue box) {
                BoundingBox boundBox = ((BoundsLib) box).getBoundingBox();

                return LuaValue.valueOf(boundingBox.overlaps(boundBox));
            }
        });

        rawset("GetMin", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Location location = new Location(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ());
                return new LocationLib(location);
            }
        });

        rawset("GetMax", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Location location = new Location(world, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
                return new LocationLib(location);
            }
        });

        rawset("GetPlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable players = new LuaTable();
                world.getNearbyEntities(boundingBox).forEach(entity -> {
                    if (entity instanceof Player player) {
                        players.insert(players.length() + 1, new PlayerLib(player));
                    }
                });

                return players;
            }
        });

        rawset("GetEntities", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable entities = new LuaTable();
                world.getNearbyEntities(boundingBox).forEach(entity -> {
                    entities.insert(entities.length() + 1, EntityTypeUtil.wrapEntity(entity));
                });

                return entities;
            }
        });

        rawset("GetBlocks", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable blocks = new LuaTable();

                for (double x = boundingBox.getMinX(); x <= boundingBox.getMaxX(); x++) {
                    for (double y = boundingBox.getMinY(); y <= boundingBox.getMaxY(); y++) {
                        for (double z = boundingBox.getMinZ(); z <= boundingBox.getMaxZ(); z++) {
                            blocks.insert(blocks.length() + 1, new BlockLib(world.getBlockAt((int) x, (int) y, (int) z)));
                        }
                    }
                }

                return blocks;
            }
        });
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    };
}