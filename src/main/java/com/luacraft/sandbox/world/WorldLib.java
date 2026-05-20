package com.luacraft.sandbox.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.EntityLib;
import com.luacraft.sandbox.item.ItemStackLib;
import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.EntityConfigUtil;
import com.luacraft.sandbox.util.EntityTypeUtil;

public class WorldLib extends LuaTable {
    public WorldLib(World world) {
        rawset("SetTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue time) {
                world.setTime(LuaErrorAssert.checkLong(time, "SetTime", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("SpawnEntity", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue loc, LuaValue ent, LuaValue callb) {
                LuaValue spawned;
                Location location = ((LocationLib) loc).getLocation();
                String entName = LuaErrorAssert.checkString(ent, "SpawnEntity", 2, null);
                EntityType type = EntityType.fromName(entName);
                Class<? extends Entity> clazz = type.getEntityClass();

                if (callb.isfunction()) {
                    LuaFunction callback = LuaErrorAssert.checkFunction(callb, "SpawnEntity", 3, null);

                    spawned = EntityTypeUtil.wrapEntity(world.spawn(location, clazz, entity -> {
                        LuaValue wrapped = EntityTypeUtil.wrapEntity(entity);
                        callback.call(wrapped);
                    }));
                } else if (callb.istable()) {
                    LuaTable configTable = LuaErrorAssert.checkTable(callb, "SpawnEntity", 3, null);

                    spawned = EntityTypeUtil.wrapEntity(world.spawn(location, clazz, entity -> {
                        EntityConfigUtil.apply(configTable, ((EntityLib) EntityTypeUtil.wrapEntity(entity)).getConfigMap());
                    }));
                } else {
                    spawned = LuaValue.NIL;
                }

                return spawned;
            }
        });

        rawset("SpawnItem", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue loc, LuaValue stack, LuaValue callb) {
                LuaFunction callback = LuaErrorAssert.checkFunction(callb, "SpawnEntity", 3, null);
                Location location = ((LocationLib) loc).getLocation();
                ItemStack itemstack = ((ItemStackLib) stack).getItemStack();

                LuaValue spawned = EntityTypeUtil.wrapEntity(world.dropItem(location, itemstack, entity -> {
                    LuaValue wrapped = EntityTypeUtil.wrapEntity(entity);
                    callback.call(wrapped);
                }));

                return spawned;
            }
        });

        rawset("GetSeed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.getSeed());
            }
        });
    }
}