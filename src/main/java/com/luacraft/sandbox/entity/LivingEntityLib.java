package com.luacraft.sandbox.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.inventory.EquipmentLib;
import com.luacraft.sandbox.inventory.InventoryLib;

public class LivingEntityLib extends EntityLib {
    private final LivingEntity entity;
    public LivingEntityLib(LivingEntity entity) {
        super(entity);
        this.entity = entity;

        rawset("GetHealth", new ZeroArgFunction() {
            public LuaValue call() {
                return LuaValue.valueOf(entity.getHealth());
            }
        });

        rawset("SetHealth", new OneArgFunction() {
            public LuaValue call(LuaValue health) {
                double newHealth = LuaErrorAssert.checkDouble(health, "SetHealth", 1, null);

                entity.setHealth(newHealth);

                return LuaValue.NIL;
            }
        });

        rawset("SetMaxHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue mHealth) {
               double maxHealth = LuaErrorAssert.checkDouble(mHealth, "SetMaxHealth", 1, null);
        
                AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
                if (attr != null) attr.setBaseValue(maxHealth);
                
                return LuaValue.NIL;
            }
        });

        rawset("GetMaxHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
                if (attr != null) return LuaValue.valueOf(attr.getBaseValue());

                return LuaValue.NIL;
            }
        });

        rawset("SetAttribute", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue att, LuaValue val) {
                String nameKey = LuaErrorAssert.checkString(att, "SetAttribute", 1, null).toLowerCase().replace(" ", "_");

                AttributeInstance attr = entity.getAttribute(Registry.ATTRIBUTE.get(NamespacedKey.minecraft(nameKey)));
                if (attr != null) attr.setBaseValue(LuaErrorAssert.checkDouble(val, "SetAttribute", 2, null));

                return LuaValue.NIL;
            }
        });

        rawset("GetAttribute", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue att) {
                String nameKey = LuaErrorAssert.checkString(att, "GetAttribute", 1, null).toLowerCase().replace(" ", "_");
                AttributeInstance attribute = entity.getAttribute(Registry.ATTRIBUTE.get(NamespacedKey.minecraft(nameKey)));

                return LuaValue.valueOf(attribute.getBaseValue());
            }
        });

        rawset("Equipment", new EquipmentLib(entity.getEquipment()));

        if (entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            rawset("Inventory", new InventoryLib(inv));
        }
    }

    public LivingEntity getLivingEntity() {
        return entity;
    }
}
