package com.luacraft.sandbox.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.chunk.ChunkLib;
import com.luacraft.sandbox.item.ItemStackLib;

public class BlockStateLib extends LuaTable {
    public BlockStateLib(BlockState state) {
        rawset("Copy", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new BlockStateLib(state.copy());
            }
        });

        rawset("GetBlockData", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new BlockDataLib(state.getBlockData());
            }
        });

        rawset("IsCollidable", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(state.isCollidable());
            }
        });

        rawset("GetChunk", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new ChunkLib(state.getChunk());
            }
        });

        rawset("GetBlock", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new BlockLib(state.getBlock());
            }
        });

        rawset("GetType", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(state.getType().toString());
            }
        });

        rawset("GetDrops", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable drops = new LuaTable();

                for (ItemStack drop : state.getDrops()) {
                    drops.insert(drops.length() + 1, new ItemStackLib(drop));
                }

                return drops;
            }
        });

        rawset("IsPlaced", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(state.isPlaced());
            }
        });

        rawset("SetType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue mat) {
                Material material = Material.valueOf(LuaErrorAssert.checkString(mat, "SetType", 1, null));

                state.setType(material);

                return BlockStateLib.this;
            }
        });

        rawset("GetContainer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (state instanceof Container) {
                    return new ContainerLib((Container) state);
                } else {
                    return LuaValue.NIL;
                }
            }
        });
    }
}
