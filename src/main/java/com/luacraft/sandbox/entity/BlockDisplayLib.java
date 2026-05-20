package com.luacraft.sandbox.entity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;

public class BlockDisplayLib extends EntityLib {
    private final BlockDisplay display;
    public BlockDisplayLib(BlockDisplay display) {
        super(display);
        this.display = display;

        rawset("SetBlock", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue block) {
                Material material = Material.matchMaterial(LuaErrorAssert.checkString(block, "SetBlock", 1, null));
                BlockData newBlock = Bukkit.getServer().createBlockData(material);

                display.setBlock(newBlock);

                return LuaValue.NIL;
            }
        });
    }

    public BlockDisplay getBlockDisplay() {
        return display;
    }
}
