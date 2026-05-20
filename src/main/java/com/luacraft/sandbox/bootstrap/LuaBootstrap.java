package com.luacraft.sandbox.bootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.luacraft.LuaErrorAssert;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;  
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class LuaBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        File bootstrapFile = new File(context.getDataDirectory().toFile() + "/bootstrapper", "bootstrap.lua");
        if (!bootstrapFile.exists()) {
            context.getLogger().info(context.getDataDirectory().toFile().toString());
            return; 
        };

        String source = null;
        try {
            source = Files.readString(bootstrapFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Globals globals = JsePlatform.standardGlobals();
        globals.set("os", LuaValue.NIL);
        globals.set("io", LuaValue.NIL);
        globals.set("debug", LuaValue.NIL);
        globals.set("luajava", LuaValue.NIL);
        globals.set("DefineEnchantment", DefineEnchantmentLib.DefineEnchantment());

        globals.load(source, "@bootstrap.lua").call();

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            for (EnchantmentDefinitions def : EnchantmentRegistry.getDefinitions()) {
                String[] parts = def.key().split(":");
                String namespace = parts[0];
                String key = parts[1];

                event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(namespace, key)),
                    builder -> {
                        LuaTable config = def.config();

                        LuaValue description = config.get("description");
                        LuaValue maxLevel = config.get("maxLevel");
                        LuaValue weight = config.get("weight");
                        LuaValue anvilCost = config.get("anvilCost");
                        LuaValue minimumCost = config.get("minimumCost");
                        LuaValue maximumCost = config.get("maximumCost");
                        LuaValue activeSlots = config.get("activeSlots");
                        LuaValue supportedItems = config.get("supportedItems");

                        if (!description.isnil()) builder.description(Component.text(description.tojstring()));
                        if (!maxLevel.isnil()) builder.maxLevel(maxLevel.toint());
                        if (!weight.isnil()) builder.weight(weight.toint());
                        if (!anvilCost.isnil()) builder.anvilCost(anvilCost.toint());
                        if (!minimumCost.isnil()) builder.minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(LuaErrorAssert.checkInt(minimumCost.get("baseCost"), "DefineEnchantment", 1, null), LuaErrorAssert.checkInt(minimumCost.get("additionalCostPerLevel"), "DefineEnchantment", 1, null)));
                        if (!maximumCost.isnil()) builder.maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(LuaErrorAssert.checkInt(maximumCost.get("baseCost"), "DefineEnchantment", 1, null), LuaErrorAssert.checkInt(maximumCost.get("additionalCostPerLevel"), "DefineEnchantment", 1, null)));
                        if (!activeSlots.isnil()) builder.activeSlots(EquipmentSlotGroup.getByName(LuaErrorAssert.checkString(activeSlots, "DefineEnchantment", 1, null)));
                        if (!supportedItems.isnil()) {
                            Map<String, TagKey<ItemType>> tagMap = Map.of(
                                "armor", ItemTypeTagKeys.ENCHANTABLE_ARMOR,
                                "sword", ItemTypeTagKeys.ENCHANTABLE_SWORD,
                                "weapon", ItemTypeTagKeys.ENCHANTABLE_WEAPON,
                                "bow", ItemTypeTagKeys.ENCHANTABLE_BOW,
                                "crossbow", ItemTypeTagKeys.ENCHANTABLE_CROSSBOW,
                                "fishing", ItemTypeTagKeys.ENCHANTABLE_FISHING,
                                "trident", ItemTypeTagKeys.ENCHANTABLE_TRIDENT,
                                "breakable", ItemTypeTagKeys.ENCHANTABLE_DURABILITY
                            );

                            String tagName = supportedItems.tojstring().toLowerCase();
                            TagKey<ItemType> tag = tagMap.get(tagName);
                            if (tag != null) builder.supportedItems(event.getOrCreateTag(tag));
                        };
                    }
                );
                context.getLogger().info("Registering " + def.key());
            }
        }));
    }
}
