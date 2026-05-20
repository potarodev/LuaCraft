package com.luacraft.sandbox.entity;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.item.ItemStackLib;

public class ItemDisplayLib extends EntityLib {
    private final ItemDisplay display;
    public ItemDisplayLib(ItemDisplay display) {
        super(display);
        this.display = display;

        rawset("SetItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                ItemStack newItem = ((ItemStackLib) item).getItemStack();

                display.setItemStack(newItem);

                return LuaValue.NIL;
            }
        });

        rawset("SetItemTransformation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue transformation) {
                String transform = LuaErrorAssert.checkString(transformation, "SetItemTransformation", 1, null);

                display.setItemDisplayTransform(ItemDisplayTransform.valueOf(transform));

                return LuaValue.NIL;
            }
        });
    }

    public ItemDisplay getItemDisplay() {
        return display;
    }
}
