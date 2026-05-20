package com.luacraft.sandbox.inventory;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.item.ItemStackLib;

import io.papermc.paper.persistence.PersistentDataContainerView;

public class InventoryLib extends LuaTable {
    public InventoryLib(Inventory inventory) {

        rawset("GiveItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                if (inventory != null) {
                    ItemStack i = ((ItemStackLib) item).stack;
                    inventory.addItem(i);
                }

                return LuaValue.NIL;
            }
        });

        rawset("SetItem", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue slot, LuaValue item) {
                if (inventory != null) {
                    ItemStack i = ((ItemStackLib) item).stack;
                    inventory.setItem(LuaErrorAssert.checkInt(slot, "SetItem", 1, null), i);
                }

                return LuaValue.NIL;
            }
        });

        rawset("ClearSlot", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue slot) {
                inventory.clear(LuaErrorAssert.checkInt(slot, "RemoveItem", 1, null));

                return LuaValue.NIL;
            }
        });

        rawset("RemoveItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                ItemStack i = ((ItemStackLib) item).stack;

                if (i == null) {
                    throw new LuaError("RemoveItem requires a valid item to be passed");
                }

                inventory.removeItemAnySlot(i);

                return LuaValue.NIL;
            }
        });

        rawset("Open", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ply) {
                Player player = null;

                if (ply instanceof PlayerLib lib) {
                    player = lib.getPlayer();
                }

                if (player != null) {
                    player.updateInventory();
                    player.openInventory(inventory);
                }
                    

                return LuaValue.NIL;
            }
        });

        rawset("UpdateInventory", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                for (HumanEntity viewer : inventory.getViewers()) {
                    if (viewer instanceof Player player) {
                        player.updateInventory();
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset("SetMetaTag", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue value) {
                InventoryHolder holder = inventory.getHolder();
                if (holder instanceof LuaInventoryHolder) {
                    LuaInventoryHolder luaHolder = (LuaInventoryHolder) holder;
                    if (luaHolder != null) {
                        luaHolder.getScriptData().set(LuaErrorAssert.checkString(value, "SetMetaTag", 1, null), value);
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset("GetMetaTag", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                InventoryHolder holder = inventory.getHolder();
                if (holder instanceof LuaInventoryHolder) {
                    LuaInventoryHolder luaHolder = (LuaInventoryHolder) holder;
                    if (luaHolder != null) {
                        return luaHolder.getScriptData().get(LuaErrorAssert.checkString(key, "GetMetaTag", 1, null));
                    }
                }

                return LuaValue.NIL;
            }
        });

        rawset("GetSize", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(inventory.getSize());
            }
        });

        rawset("GetSlots", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable items = new LuaTable();
                ItemStack[] contents = inventory.getContents();
                for (int i = 0; i < contents.length; i++) {
                    ItemStack item = contents[i];
                    if (item != null && item.getType() != Material.AIR) {
                        items.set(i + 1, new ItemStackLib(item));
                    }
                }

                return items;
            }
        });

        rawset("Contains", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue item) {
                ItemStack itemstack = ((ItemStackLib) item).getItemStack();
                if (itemstack != null) {
                    return LuaValue.valueOf(inventory.containsAtLeast(itemstack, itemstack.getAmount()));
                } else {
                    throw new LuaError("Contains requires a valid itemstack");
                }
            }
        });

        rawset("ContainsWithMetaData", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue item, LuaValue key) {
                ItemStack itemstack = ((ItemStackLib) item).getItemStack();
                NamespacedKey nameKey = new NamespacedKey(LuaCraft.getPlugin(), LuaErrorAssert.checkString(key, "ContainsWithMetaData", 2, null));

                if (itemstack == null || key.isnil())
                    throw new LuaError("ContainsWithMetaData requires a valid itemstack and String");

                PersistentDataContainerView pdc = itemstack.getPersistentDataContainer();

                return LuaValue.valueOf(inventory.containsAtLeast(itemstack, itemstack.getAmount()) && pdc.has(nameKey));
            }
        });
    }
}