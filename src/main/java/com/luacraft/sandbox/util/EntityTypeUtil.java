package com.luacraft.sandbox.util;

import org.bukkit.entity.Animals;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Villager;
import org.luaj.vm2.LuaValue;

import com.luacraft.sandbox.entity.AnimalsLib;
import com.luacraft.sandbox.entity.BlockDisplayLib;
import com.luacraft.sandbox.entity.EntityLib;
import com.luacraft.sandbox.entity.FishHookLib;
import com.luacraft.sandbox.entity.ItemDisplayLib;
import com.luacraft.sandbox.entity.ItemLib;
import com.luacraft.sandbox.entity.LivingEntityLib;
import com.luacraft.sandbox.entity.MobLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.entity.TextDisplayLib;
import com.luacraft.sandbox.entity.VillagerLib;

public class EntityTypeUtil {
    public static LuaValue wrapEntity(Entity entity) {
        if (entity instanceof Player player) return new PlayerLib(player);
        if (entity instanceof Villager villager) return new VillagerLib(villager);
        if (entity instanceof Animals animals) return new AnimalsLib(animals);
        if (entity instanceof Mob mob) return new MobLib(mob);
        if (entity instanceof LivingEntity living) return new LivingEntityLib(living);
        if (entity instanceof Item item) return new ItemLib(item);
        if (entity instanceof FishHook fishHook) return new FishHookLib(fishHook);
        if (entity instanceof ItemDisplay itemDisplay) return new ItemDisplayLib(itemDisplay);
        if (entity instanceof TextDisplay textDisplay) return new TextDisplayLib(textDisplay);
        if (entity instanceof BlockDisplay blockDisplay) return new BlockDisplayLib(blockDisplay);

        return new EntityLib(entity);
    }

    public static Entity unwrapEntity(LuaValue value) {
        if (value instanceof PlayerLib lib) return lib.getPlayer();
        if (value instanceof VillagerLib lib) return lib.getVillager();
        if (value instanceof AnimalsLib lib) return lib.getAnimal();
        if (value instanceof MobLib lib) return lib.getMob();
        if (value instanceof LivingEntityLib lib) return lib.getLivingEntity();
        if (value instanceof ItemLib lib) return lib.getItemEntity();
        if (value instanceof FishHookLib lib) return lib.getFishHook();
        if (value instanceof ItemDisplayLib lib) return lib.getItemDisplay();
        if (value instanceof TextDisplayLib lib) return lib.getTextDisplay();
        if (value instanceof BlockDisplayLib lib) return lib.getBlockDisplay();
        if (value instanceof EntityLib lib) return lib.getEntity();
        
        return null;
    }
}