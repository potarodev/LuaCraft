package com.luacraft.sandbox.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.sandbox.item.ItemStackLib;

public class PlayerInventoryLib extends InventoryLib {
    public PlayerInventoryLib(Player player) {
        super(player.getInventory());

        rawset("GetMiniCraftGrid", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                InventoryView view = player.getOpenInventory();
                CraftingInventory craftingInv;
                if (view.getTopInventory() instanceof CraftingInventory) {
                    craftingInv = (CraftingInventory) view.getTopInventory();
                    return new InventoryLib(craftingInv);
                }
                return LuaValue.NIL;
            } 
        });

        rawset("GetItemInMainHand", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new ItemStackLib(player.getInventory().getItemInMainHand());
            }
        });

        rawset("SetItemInMainHand", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue itemstack) {
                ItemStack item = ((ItemStackLib) itemstack).getItemStack();
                player.getInventory().setItemInMainHand(item);

                return LuaValue.NIL;
            }
        });

        rawset("GetItemInOffHand", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new ItemStackLib(player.getInventory().getItemInOffHand());
            }
        });

        rawset("SetItemInOffHand", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue itemstack) {
                ItemStack item = ((ItemStackLib) itemstack).getItemStack();
                player.getInventory().setItemInOffHand(item);

                return LuaValue.NIL;
            }
        });
    }
}
