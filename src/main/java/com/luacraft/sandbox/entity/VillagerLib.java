package com.luacraft.sandbox.entity;

import org.bukkit.Registry;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

public class VillagerLib extends LivingEntityLib {
    private final Villager villager;
    public VillagerLib(Villager villager) {
        super(villager);
        this.villager = villager;

        rawset("SetVillagerType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue type) {
                String name = LuaErrorAssert.checkString(type, "SetVillagerType", 1, null);
                NamespacedKey key = NamespacedKey.minecraft(name.toLowerCase());
                Registry<Villager.Type> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.VILLAGER_TYPE);
                Villager.Type newType = registry.get(key);

                villager.setVillagerType(newType);

                return LuaValue.NIL;
            }
        });

        rawset("GetVillagerType", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Villager.Type type = villager.getVillagerType();
                String typeString = type.getKey().getKey();

                return LuaValue.valueOf(typeString);
            }
        });
    }

    public Villager getVillager() {
        return villager;
    }
}
