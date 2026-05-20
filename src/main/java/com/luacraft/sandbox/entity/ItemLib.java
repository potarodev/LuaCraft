package com.luacraft.sandbox.entity;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.item.ItemStackLib;


public class ItemLib extends EntityLib {
    private final Item entity;

    public ItemLib(Item entity) {
        super(entity);
        this.entity = entity;

        rawset("GetItemStack", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new ItemStackLib(entity.getItemStack());
            }
        });

        rawset("SetItemStack", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue material, LuaValue amount) {
                Material mat = Material.matchMaterial(LuaErrorAssert.checkString(material, "SetItemStack", 1, null));
                int amt = LuaErrorAssert.checkInt(amount, "SetItemStack", 2, null);

                if (mat == null) return LuaValue.NIL;

                ItemStack stack = new ItemStack(mat, amt);

                entity.setItemStack(stack);

                return LuaValue.NIL;
            }
        });

        rawset("SetPickupDelay", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue delay) {
                int newDelay = LuaErrorAssert.checkInt(delay, "SetPickupDelay", 1, null);

                entity.setPickupDelay(newDelay);

                return LuaValue.NIL;
            }
        });

        rawset("GetPickupDelay", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getPickupDelay());
            }
        });
    }

    public Item getItemEntity() {
        return entity;
    }
}
