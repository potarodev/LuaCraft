package com.luacraft.sandbox.block;

import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.inventory.InventoryLib;

public class ContainerLib extends LuaTable {
    public ContainerLib(Container container) {
        rawset("IsDoubleChest", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(
                    container.getInventory().getHolder() instanceof DoubleChest
                );
            } 
        });

        rawset("Inventory", new InventoryLib(container.getInventory()));
    }
}
