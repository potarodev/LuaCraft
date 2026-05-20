package com.luacraft.sandbox.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.util.ComponentUtils;

import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class ItemStackLib extends LuaTable {
    public static LuaFunction itemStackFactory() {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue material, LuaValue amount) {
                Material mat = Material.matchMaterial(LuaErrorAssert.checkString(material, "Itemstack", 1, null));
                int amt = LuaErrorAssert.checkInt(amount, "Itemstack", 2, null);
                
                if (mat == null) return LuaValue.NIL;

                ItemStack stack = ItemStack.of(mat, amt);
                return new ItemStackLib(stack);
            }
        };
    }

    public final ItemStack stack;

    public ItemStackLib(ItemStack itemstack) {
        this.stack = itemstack;

        rawset("SetName", new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                ItemMeta meta = itemstack.getItemMeta();
                Component comp = ComponentUtils.luaValueToComponent(arg);
                Component nonItalic = comp.decoration(TextDecoration.ITALIC, false);
                meta.displayName(nonItalic);
                itemstack.setItemMeta(meta);

                return ItemStackLib.this;
            }
        });

        rawset("GetName", new ZeroArgFunction() {
           public LuaValue call() {
                ItemMeta meta = itemstack.getItemMeta();
                Component itemName = meta.displayName();

                if (itemName == null) {
                    String prettyName = itemstack.getType().name().toLowerCase().replace("_", " ");
                    prettyName = Character.toUpperCase(prettyName.charAt(0)) + prettyName.substring(1);

                    itemName = Component.text(prettyName);
                }

                LuaComponent holder = new LuaComponent(itemName);

                return new ComponentLib(holder.getComponent());
           } 
        });

        rawset("GetType", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(itemstack.getType().toString());
            }
        });

        rawset("SetLore", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (itemstack == null) {
                    return LuaValue.NIL;
                }
                List<Component> loreList = new ArrayList<>();
                if (arg.isstring()) {
                    String lore = LuaErrorAssert.checkString(arg, "SetLore", 1, null);
                    for (String line : lore.split("\n")) {
                        loreList.add(ComponentUtils.parseLegacy(line));
                    }
                } else if (arg.istable()) {
                    int n = arg.length();
                    for (int i = 1; i <= n; i++) {
                        LuaValue elem = arg.get(i);
                        if (elem.isstring()) {
                            loreList.add(ComponentUtils.parseLegacy(elem.checkjstring()));
                        } else {
                            loreList.add(ComponentUtils.luaValueToComponent(elem));
                        }
                    }
                } else if (arg instanceof ComponentLib) {
                    loreList.add(((ComponentLib) arg).getComponent());
                }

                itemstack.lore(loreList);
                return ItemStackLib.this;
            }
        });

        rawset("SetFlags", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {

                ItemMeta meta = itemstack.getItemMeta();

                Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.LUCK);

                if (modifiers == null || modifiers.isEmpty()) {
                    meta.addAttributeModifier(Attribute.LUCK, new AttributeModifier(
                        new NamespacedKey(LuaCraft.getPlugin(), "hidden_attr"),
                        0,
                        AttributeModifier.Operation.ADD_NUMBER
                    ));
                }

                if (arg.isstring()) {
                    String flag = LuaErrorAssert.checkString(arg, "SetFlags", 1, null);
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                    itemstack.setItemMeta(meta);
                } else if (arg.istable()) {
                    int n = arg.length();
                        for (int i = 1; i <= n; i++) {
                            LuaValue elem = arg.get(i);
                        if (elem.isstring()) {
                            meta.addItemFlags(ItemFlag.valueOf(LuaErrorAssert.checkString(elem, "SetFlags", 1, null)));
                            itemstack.setItemMeta(meta);
                        }
                    }
                }

                return ItemStackLib.this;
            }
        });

        rawset("HasMetaData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                PersistentDataContainerView pdc = itemstack.getPersistentDataContainer();
                NamespacedKey nameKey = new NamespacedKey(LuaCraft.getPlugin(), LuaErrorAssert.checkString(key, "HasMetaData", 1, null));

                return LuaValue.valueOf(pdc.has(nameKey));
            }
        });

        rawset("SetMetaData", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue value) {
                NamespacedKey nameKey = new NamespacedKey(LuaCraft.getPlugin(), LuaErrorAssert.checkString(key, "SetMetaData", 1, null));

                if (value.isstring()) {
                    itemstack.editPersistentDataContainer(pdc -> {
                        pdc.set(nameKey, PersistentDataType.STRING, LuaErrorAssert.checkString(value, "SetMetaData", 2, null));
                    });
                } else if (value.isnumber()) {
                    itemstack.editPersistentDataContainer(pdc -> {
                        pdc.set(nameKey, PersistentDataType.DOUBLE, LuaErrorAssert.checkDouble(value, "SetMetaData", 2, null));
                    });
                } else if (value.isboolean()) {
                    itemstack.editPersistentDataContainer(pdc -> {
                        pdc.set(nameKey, PersistentDataType.BOOLEAN, LuaErrorAssert.checkBoolean(value, "SetMetaData", 2, null));
                    });
                } else {
                    throw new LuaError("[LuaCraft] Expected Boolean/String/Number, received unexpected type of " + value.typename());
                }

                return ItemStackLib.this;
            }
        });

        rawset("GetMetaData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                PersistentDataContainerView pdc = itemstack.getPersistentDataContainer();
                NamespacedKey nameKey = new NamespacedKey(LuaCraft.getPlugin(), key.checkjstring());

                if (pdc.has(nameKey, PersistentDataType.STRING)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.STRING));
                } else if (pdc.has(nameKey, PersistentDataType.DOUBLE)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.DOUBLE));
                } else if (pdc.has(nameKey, PersistentDataType.BOOLEAN)) {
                    return LuaValue.valueOf(pdc.get(nameKey, PersistentDataType.BOOLEAN));
                }

                return ItemStackLib.this;
            }
        });

        rawset("Add", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue amount) {
                Integer amt = amount.checkint();

                itemstack.add(amt);

                return ItemStackLib.this;
            }
        });

        rawset("Subtract", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue amount) {
                Integer amt = amount.checkint();

                itemstack.subtract(amt);

                return ItemStackLib.this;
            }
        });

        rawset("Unbreakable", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue unbreaking) {
                ItemMeta meta = itemstack.getItemMeta();

                meta.setUnbreakable(unbreaking.checkboolean());

                itemstack.setItemMeta(meta);

                return ItemStackLib.this;
            }
        });

        rawset("Enchant", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue name, LuaValue power) {
                var enchRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
                Key enchkey = Key.key("minecraft:" + LuaErrorAssert.checkString(name, "Enchant", 1, null));
                Enchantment enchantment = enchRegistry.get(TypedKey.create(RegistryKey.ENCHANTMENT, enchkey));
                itemstack.addUnsafeEnchantment(enchantment, LuaErrorAssert.checkInt(power, "Enchant", 2, null));

                return ItemStackLib.this;
            }
        });

        rawset("RemoveEnchant", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ench) {
                var enchRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
                Key enchkey = Key.key("minecraft:" + LuaErrorAssert.checkString(ench, "Enchant", 1, null));
                Enchantment enchantment = enchRegistry.get(TypedKey.create(RegistryKey.ENCHANTMENT, enchkey));

                if (itemstack.containsEnchantment(enchantment)) {
                    itemstack.removeEnchantment(enchantment);
                }

                return ItemStackLib.this;
            }
        });

        rawset("RemoveAllEnchants", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                itemstack.removeEnchantments();

                return ItemStackLib.this;
            }
        });

        rawset("HasEnchantment", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ench) {
                var enchRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
                Key enchkey = Key.key(LuaErrorAssert.checkString(ench, "Enchant", 1, null));
                Enchantment enchantment = enchRegistry.get(TypedKey.create(RegistryKey.ENCHANTMENT, enchkey));

                return LuaValue.valueOf(itemstack.containsEnchantment(enchantment));
            }
        });

        rawset("Damage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue dmg) {
                ItemMeta meta = itemstack.getItemMeta();

                if (meta instanceof org.bukkit.inventory.meta.Damageable damageable) {
                    damageable.setDamage(damageable.getDamage() - LuaErrorAssert.checkInt(dmg, "Damage", 1, null));
                    itemstack.setItemMeta(meta);
                }

                return ItemStackLib.this;
            }
        });

        rawset("SetCustomModelData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue data) {
                ItemMeta meta = itemstack.getItemMeta();

                meta.setCustomModelData(LuaErrorAssert.checkInt(data, "SetCustomModelData", 1, null));

                itemstack.setItemMeta(meta);

                return ItemStackLib.this;
            }
        });

        rawset("GetCustomModelData", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                ItemMeta meta = itemstack.getItemMeta();

                return LuaValue.valueOf(meta.getCustomModelData());
            }
        });
    }

    public ItemStack getItemStack() {
        return stack;
    }
}
