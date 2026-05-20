package com.luacraft.sandbox.block;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.location.LocationLib;

public class BlockLib extends LuaTable {
    private Block block;
    private static Set<String> keys = new HashSet<>();

    public BlockLib(Block block) {
        this.block = block;

        rawset("GetType", new ZeroArgFunction() {
           @Override
           public LuaValue call() {
                return LuaValue.valueOf(block.getType().toString());
           } 
        });
        
        rawset("GetName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                String prettyName = WordUtils.capitalizeFully(block.getType().toString().replace("_", " "));

                return LuaValue.valueOf(prettyName);
            }
        });

        rawset("IsType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue type) {
                String prettyName = WordUtils.capitalizeFully(block.getType().toString().replace("_", " "));
                String blockName = LuaErrorAssert.checkString(type, "IsType", 1, null);

                return LuaValue.valueOf(prettyName.equals(blockName));
            }
        });

        rawset("SetBlock", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue newBlock) {
                Material material = Material.matchMaterial(LuaErrorAssert.checkString(newBlock, "SetBlock", 1, null));
                block.setType(material);

                return LuaValue.NIL;
            }
        });

        rawset("GetLocation", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LocationLib(block.getLocation());
            }
        });

        rawset("HasMetaData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                TileState tileState = getTileState(block);
                PersistentDataContainer pdc;
                NamespacedKey nameKey;
                if (tileState == null) {
                    pdc = block.getChunk().getPersistentDataContainer();
                    Location loc = block.getLocation();
                    String blockKey = loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getWorld().getName() + "-" + LuaErrorAssert.checkString(key, "HasMetaData", 1, null);
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), blockKey);
                } else {
                    pdc = tileState.getPersistentDataContainer();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), LuaErrorAssert.checkString(key, "HasMetaData", 1, null));
                }

                return LuaValue.valueOf(pdc.has(nameKey));
            }
        });

        rawset("SetMetaData", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue value) {
                TileState tileState = getTileState(block);
                PersistentDataContainer pdc;
                NamespacedKey nameKey;
                if (tileState == null) {
                    pdc = block.getChunk().getPersistentDataContainer();
                    Location loc = block.getLocation();
                    String blockKey = loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getWorld().getName() + "-" + LuaErrorAssert.checkString(key, "SetMetaData", 1, null);
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), blockKey);
                    keys.add(key.tojstring());
                } else {
                    pdc = tileState.getPersistentDataContainer();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), LuaErrorAssert.checkString(key, "SetMetaData", 1, null));
                    keys.add(key.tojstring());
                }

                if (value.isstring()) {
                    pdc.set(nameKey, PersistentDataType.STRING, LuaErrorAssert.checkString(key, "SetMetaData", 2, null));
                } else if (value.isnumber()) {
                    pdc.set(nameKey, PersistentDataType.DOUBLE, LuaErrorAssert.checkDouble(value, "SetMetaData", 2, null));
                } else if (value.isboolean()) {
                    pdc.set(nameKey, PersistentDataType.BOOLEAN, LuaErrorAssert.checkBoolean(value, "SetMetaData", 2, null));
                }

                if (tileState != null)
                    tileState.update();
                return LuaValue.NIL;
            }
        });

        rawset("GetMetaData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                TileState tileState = getTileState(block);
                PersistentDataContainer pdc;
                NamespacedKey nameKey;
                if (tileState == null) {
                    pdc = block.getChunk().getPersistentDataContainer();
                    Location loc = block.getLocation();
                    String blockKey = loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getWorld().getName() + "-" + LuaErrorAssert.checkString(key, "GetMetaData", 1, null);
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), blockKey);
                } else {
                    pdc = tileState.getPersistentDataContainer();
                    nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());
                }

                if (pdc.has(nameKey, PersistentDataType.STRING)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.STRING));
                } else if (pdc.has(nameKey, PersistentDataType.DOUBLE)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.DOUBLE));
                } else if (pdc.has(nameKey, PersistentDataType.BOOLEAN)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.BOOLEAN));
                }

                return LuaValue.NIL;
            }
        });

        rawset("GetBlockState", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new BlockStateLib(block.getState());
            }
        });

        rawset("ApplyBoneMeal", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue face) {
                if (!face.isnil()) {
                    block.applyBoneMeal(BlockFace.valueOf(LuaErrorAssert.checkString(face, "ApplyBoneMeal", 1, null)));
                } else {
                    block.applyBoneMeal(BlockFace.UP);
                }

                return BlockLib.this;
            }
        });
    }

    private TileState getTileState(Block block) {
        if (block.getState() instanceof TileState tileState) {
            return tileState;
        }

        return null;
    }

    public Block getBlock() {
        return block;
    }

    public static Set<String> getKeys() {
        return keys;
    }
}
