package com.luacraft.sandbox.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.world.WorldLib;

public class LocationLib extends LuaTable {
    public static VarArgFunction locationFactory() {
        return new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() != 4) {
                    throw new LuaError("Location() requires 4 arguments (x, y, z, world)");
                }

                double x = LuaErrorAssert.checkDouble(args.arg(1), "Location", 1, null);
                double y = LuaErrorAssert.checkDouble(args.arg(2), "Location", 2, null);
                double z = LuaErrorAssert.checkDouble(args.arg(3), "Location", 3, null);
                String worldName = LuaErrorAssert.checkString(args.arg(4), "Location", 4, null);
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    throw new LuaError("World '" + worldName + "' does not exist or is not loaded");
                }

                Location location = new Location(world, x, y, z);

                return new LocationLib(location);
            }
        };
    }

    private final Location location;

    public LocationLib(Location location) {
        this.location = location;

        rawset("GetX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(location.getX());
            }
        });

        rawset("GetY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(location.getY());
            }
        });
        
        rawset("GetZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(location.getZ());
            }
        });

        rawset("SetX", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue x) {
                location.setX(LuaErrorAssert.checkDouble(x, "SetX", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SetY", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue y) {
                location.setY(LuaErrorAssert.checkDouble(y, "SetY", 1, null));

                return LuaValue.NIL;
            }
        });
        
        rawset("SetZ", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue z) {
                location.setZ(LuaErrorAssert.checkDouble(z, "SetZ", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("GetWorld", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new WorldLib(location.getWorld());
            }
        });

        rawset("DistanceTo", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue loc) {
                if (loc instanceof LocationLib) {
                    Location otherLoc = ((LocationLib) loc).getLocation();

                    return LuaValue.valueOf(location.distance(otherLoc));
                }
                throw new LuaError("DistanceTo requires a Location object");
            }
        });

        rawset("IsWithinBounds", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue loc1, LuaValue loc2) {
                if (loc1 instanceof LocationLib && loc2 instanceof LocationLib) {
                    Location location1 = ((LocationLib) loc1).getLocation();
                    Location location2 = ((LocationLib) loc2).getLocation();

                    double minX = Math.min(location1.getX(), location2.getX());
                    double maxX = Math.max(location1.getX(), location2.getX());
                    
                    double minY = Math.min(location1.getY(), location2.getY());
                    double maxY = Math.max(location1.getY(), location2.getY());
                    
                    double minZ = Math.min(location1.getZ(), location2.getZ());
                    double maxZ = Math.max(location1.getZ(), location2.getZ());
                    
                    boolean inRegion = location.getX() >= minX && location.getX() <= maxX && location.getY() >= minY && location.getY() <= maxY && location.getZ() >= minZ && location.getZ() <= maxZ;
                    
                    return LuaValue.valueOf(inRegion);
                }
                throw new LuaError("IsWithinBounds requires two Location objects");
            }
        });

        rawset("SetBlockAtLocation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Material material = Material.matchMaterial(LuaErrorAssert.checkString(arg, "SetBlockAtLocation", 1, null));

                location.getBlock().setType(material);

                return LuaValue.NIL;
            }
        });

        rawset("Compare", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue loc) {
                Location otherLoc = ((LocationLib) loc).getLocation();

                return LuaValue.valueOf(location.equals(otherLoc));
            }
        });
    }

    public Location getLocation() {
        return location;
    }
}