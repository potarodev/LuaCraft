package com.luacraft.sandbox.entity;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.joml.Math;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.component.LuaComponent;
import com.luacraft.sandbox.inventory.InventoryLib;
import com.luacraft.sandbox.inventory.PlayerInventoryLib;
import com.luacraft.sandbox.item.ItemStackLib;
import com.luacraft.sandbox.location.LocationLib;
import com.luacraft.sandbox.util.ComponentUtils;
import com.luacraft.sandbox.util.EntityTypeUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public class PlayerLib extends LivingEntityLib {
    private final Player player;

    public PlayerLib(Player player) {
        super(player);
        this.player = player;

        rawset("IsFlying", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.isFlying());
            }
        });

        rawset("SetFlying", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                boolean fly = LuaErrorAssert.checkBoolean(arg, "SetFlying", 1, player);

                if (!fly) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                } else {
                    player.setAllowFlight(true);
                }

                return PlayerLib.this;
            }
        });

        rawset("SetWalkspeed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue speed) {
                player.setWalkSpeed(LuaErrorAssert.checkFloat(speed, "SetWalkspeed", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("SetFlyspeed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue speed) {
                player.setFlySpeed(LuaErrorAssert.checkFloat(speed, "SetFlyspeed", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("SetSneaking", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue sneaking) {
                player.setSneaking(LuaErrorAssert.checkBoolean(sneaking, "SetSneaking", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("SetSprinting", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue sprinting) {
                player.setSprinting(LuaErrorAssert.checkBoolean(sprinting, "Sprinting", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("SetTime", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue time, LuaValue relative) {
                player.setPlayerTime(LuaErrorAssert.checkLong(time, "SetTime", 1, player), LuaErrorAssert.checkBoolean(relative, "SetTime", 2, player));

                return PlayerLib.this;
            }
        });

        rawset("GetDisplayName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaComponent holder;
                if (player.displayName() != null) {
                    holder = new LuaComponent(player.displayName());
                    return new ComponentLib(holder.getComponent());
                } else {
                    return new ComponentLib(Component.text(player.getName()));
                }
            }
        });

        rawset("SetDisplayName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue newDisplayName) {
                Component displayName;
                
                if (!newDisplayName.isnil()) {
                    displayName = ComponentUtils.luaValueToComponent(newDisplayName);
                    player.displayName(displayName);
                }

                return PlayerLib.this;
            }
        });

        rawset("GetPrefix", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (!LuaCraft.getInstance().hasVault()) {
                    Bukkit.getLogger().warning("[LuaCraft] Attempt to use a Vault function without having Vault installed");
                    player.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] Attempt to use a Vault function without having Vault installed"));
                    return LuaValue.NIL;
                }

                String rawPrefix = LuaCraft.chat.getPlayerPrefix(player);
                
                return LuaValue.valueOf(rawPrefix);
            }
        });

        rawset("SetPrefix", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue prefix) {
                if (!LuaCraft.getInstance().hasVault()) {
                    Bukkit.getLogger().warning("[LuaCraft] Attempt to use a Vault function without having Vault installed");
                    player.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] Attempt to use a Vault function without having Vault installed"));
                    return LuaValue.NIL;
                }

                String newPrefix;

                if (!prefix.isnil()) {
                    newPrefix = LuaErrorAssert.checkString(prefix, "SetPrefix", 1, null);
                    LuaCraft.chat.setPlayerPrefix(player, newPrefix);
                } else {
                    LuaCraft.chat.setPlayerPrefix(player, null);
                }

                return PlayerLib.this;
            }
        });

        rawset("GetSuffix", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (!LuaCraft.getInstance().hasVault()) {
                    Bukkit.getLogger().warning("[LuaCraft] Attempt to use a Vault function without having Vault installed");
                    player.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] Attempt to use a Vault function without having Vault installed"));
                    return LuaValue.NIL;
                }

                String rawSuffix = LuaCraft.chat.getPlayerSuffix(player);
                
                return LuaValue.valueOf(rawSuffix);
            }
        });

        rawset("SetSuffix", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue suffix) {
                if (!LuaCraft.getInstance().hasVault()) {
                    Bukkit.getLogger().warning("[LuaCraft] Attempt to use a Vault function without having Vault installed");
                    player.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] Attempt to use a Vault function without having Vault installed"));
                    return LuaValue.NIL;
                }

                String newSuffix;

                if (!suffix.isnil()) {
                    newSuffix = LuaErrorAssert.checkString(suffix, "SetSuffix", 1, player);
                    LuaCraft.chat.setPlayerSuffix(player, newSuffix);
                } else {
                    LuaCraft.chat.setPlayerSuffix(player, null);
                }

                return PlayerLib.this;
            }
        });

        rawset("GetUUID", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getUniqueId().toString());
            }
        });

        rawset("IsOnline", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.isOnline());
            }
        });

        rawset("SendMessage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                Component text = ComponentUtils.luaValueToComponent(message);

                player.sendMessage(text);

                return PlayerLib.this;
            }
        });

        rawset("HasPermission", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue permission) {
                return LuaValue.valueOf(player.hasPermission(LuaErrorAssert.checkString(permission, "HasPermission", 1, player)));
            }
        });

        rawset("IsOp", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.isOp());
            }
        });

        rawset("SendFakeBlockDamage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue loc, LuaValue damage) {
                Float newDamage = damage.tofloat();
                Location location = ((LocationLib) loc).getLocation();

                player.sendBlockDamage(location, Math.clamp((float) 0.0, (float) 1.0, newDamage));

                return PlayerLib.this;
            }
        });

        rawset("SendFakeBlockChange", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue loc, LuaValue blockName) {
                String block = LuaErrorAssert.checkString(blockName, "SendFakeBlockChange", 1, null).toUpperCase();
                Location location = ((LocationLib) loc).getLocation();
                Material mat = Material.getMaterial(block);
                BlockData data = mat.createBlockData();

                player.sendBlockChange(location, data);

                return PlayerLib.this;
            }
        });

        rawset("SendFakeMultiBlockChange", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue locations, LuaValue blockNames) {
                Bukkit.getScheduler().runTaskAsynchronously(LuaCraft.getPlugin(), () -> {
                    Map<Location, BlockData> changes = new HashMap<>();
                    for (int i = 1; i <= locations.length(); i++) {
                        LuaValue loc = locations.get(LuaValue.valueOf(i));
                        if (loc.isnil()) continue;

                        Location location = ((LocationLib) loc).getLocation();
                        if (!location.getWorld().equals(player.getWorld())) {
                            throw new LuaError("All locations must be in the same world as the player!");
                        }

                        LuaValue blockName;
                        if (i <= blockNames.length()) {
                            blockName = blockNames.get(LuaValue.valueOf(i));
                        } else {
                            blockName = blockNames.get(LuaValue.valueOf(blockNames.length()));
                        }

                        Material mat = Material.getMaterial(LuaErrorAssert.checkString(blockName, "SendFakeMultiBlockChange.Table", 2, null).toUpperCase());
                        if (mat == null) {
                            throw new LuaError("Unknown material: " + LuaErrorAssert.checkString(blockName, "SendFakeMultiBlockChange.Table", 2, null));
                        }
                        BlockData data = mat.createBlockData();

                        changes.put(location, data);
                    }

                    Bukkit.getScheduler().runTask(LuaCraft.getPlugin(), () -> {
                        player.sendMultiBlockChange(changes);
                    });
                });
                return PlayerLib.this;
            }
        });

        rawset("Tool", new ItemStackLib(player.getInventory().getItemInMainHand()));

        rawset("PlaySound", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                Entity ent = ((EntityLib) args.arg(1)).getEntity();
                String sound = LuaErrorAssert.checkString(args.arg(2), "PlaySound", 2, null);
                Float volume = LuaErrorAssert.checkFloat(args.arg(3), "PlaySound", 3, null);
                Float pitch = args.arg(4).tofloat();

                player.playSound(ent, sound, volume, pitch);

                return PlayerLib.this;
            }
        });

        rawset("SetTablistName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue format) {
                Component comp = ComponentUtils.luaValueToComponent(format);

                player.playerListName(comp);

                return PlayerLib.this;
            }
        });

        rawset("SetTablistHeader", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue header) {
                Component comp = ComponentUtils.luaValueToComponent(header);

                player.sendPlayerListHeader(comp);

                return PlayerLib.this;
            }
        });

        rawset("SetTablistFooter", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue footer) {
                Component comp = ComponentUtils.luaValueToComponent(footer);

                player.sendPlayerListFooter(comp);

                return PlayerLib.this;
            }
        });

        rawset("SetTablistSortOrder", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue order) {
                player.setPlayerListOrder(LuaErrorAssert.checkInt(order, "SetTablistSortOrder", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("SetGamemode", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue gamemode) {
                player.setGameMode(GameMode.valueOf(LuaErrorAssert.checkString(gamemode, "SetGamemode", 1, player)));

                return PlayerLib.this;
            }
        });

        rawset("GetGamemode", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getGameMode().toString());
            }
        });

        rawset("SendActionBar", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue component) {
                player.sendActionBar(ComponentUtils.luaValueToComponent(component));

                return PlayerLib.this;
            }
        });

        rawset("SendTitle", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue ttle, LuaValue subTttle, LuaValue timings) {
                Component main = ComponentUtils.luaValueToComponent(ttle);
                Component sub = ComponentUtils.luaValueToComponent(subTttle);
                
                Title title;
                if (!timings.isnil()) {
                    LuaTable t = timings.checktable();

                    Title.Times times = Title.Times.times(
                        Duration.ofMillis(t.get("fadeInDuration").optint(500)),
                        Duration.ofMillis(t.get("stayDuration").optint(3000)),
                        Duration.ofMillis(t.get("fadeOutDuration").optint(500))
                    );
                    title = Title.title(main, sub, times);
                } else {
                    title = Title.title(main, sub);
                }

                player.showTitle(title);
                return PlayerLib.this;
            }
        });

        rawset("ClearTitle", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                player.clearTitle();

                return PlayerLib.this;
            }
        });

        rawset("SetCompassTarget", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue loc) {
                Location location = ((LocationLib) loc).getLocation();

                player.setCompassTarget(location);

                return PlayerLib.this;
            }
        });

        rawset("GetStatistic", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue stat, LuaValue mat) {
                Statistic statistic = Statistic.valueOf(LuaErrorAssert.checkString(stat, "GetStatistic", 1, player).toUpperCase());
                String materialOrEntityType = LuaErrorAssert.checkString(mat, "GetStatistic", 2, player).toUpperCase();
                if (mat.isnil()) {
                    return LuaValue.valueOf(player.getStatistic(statistic));
                } else {
                    try {
                        EntityType type = EntityType.valueOf(materialOrEntityType);
                        return LuaValue.valueOf(player.getStatistic(statistic, type));
                    } catch (IllegalArgumentException e) {
                        Material material = Material.valueOf(materialOrEntityType);
                        return LuaValue.valueOf(player.getStatistic(statistic, material));
                    }
                }
            }
        });

        rawset("SetStatistic", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue stat, LuaValue mat, LuaValue amt) {
                Statistic statistic = Statistic.valueOf(LuaErrorAssert.checkString(stat, "GetStatistic", 1, player).toUpperCase());
                int amount = LuaErrorAssert.checkInt(amt, "SetStatistic", 3, player);
                if (mat.isnil()) {
                    player.setStatistic(statistic, amount);
                    return PlayerLib.this;
                } else {
                    String matOrEntity = LuaErrorAssert.checkString(mat, "SetStatistic", 2, player).toUpperCase();
                    try {
                        EntityType type = EntityType.valueOf(matOrEntity);
                        player.setStatistic(statistic, type, amount);
                    } catch (IllegalArgumentException e) {
                        Material material = Material.valueOf(matOrEntity);
                        player.setStatistic(statistic, material, amount);
                    }
                    return PlayerLib.this;
                }
            }
        });

        rawset("CloseInventory", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                player.closeInventory();

                return PlayerLib.this;
            }
        });

        rawset("GetSaturation", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getSaturation());
            }
        });

        rawset("GetSaturatedRegenRate", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getSaturatedRegenRate());
            }
        });

        rawset("SetSaturation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue amt) {
                player.setSaturation(LuaErrorAssert.checkInt(amt, "SetSaturation", 1, player));

                return PlayerLib.this;
            } 
        });

        rawset("SetSaturatedRegenRate", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue rate) {
                player.setSaturatedRegenRate(LuaErrorAssert.checkInt(rate, "SetSaturatedRegenRate", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("GetEnderChest", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new InventoryLib(player.getEnderChest());
            }
        });

        rawset("OpenEnderChest", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                player.openInventory(player.getEnderChest());

                return PlayerLib.this;
            }
        });

        rawset("GetFoodLevel", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getFoodLevel());
            }
        });

        rawset("SetFoodLevel", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue level) {
                player.setFoodLevel(LuaErrorAssert.checkInt(level, "SetFoodLevel", 1, player));
            
                return PlayerLib.this;
            }
        });

        rawset("HidePlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue otherPlayer) {
                Player otherplayer = ((PlayerLib) otherPlayer).getPlayer();
                player.hidePlayer(LuaCraft.getPlugin(), otherplayer);

                return PlayerLib.this;
            }
        });

        rawset("HideEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ent) {
                Entity entity = EntityTypeUtil.unwrapEntity(ent);
                player.hideEntity(LuaCraft.getPlugin(), entity);

                return PlayerLib.this;
            }
        });

        rawset("CanSeePlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue otherPlayer) {
                Player otherplayer = ((PlayerLib) otherPlayer).getPlayer();
                player.canSee(otherplayer);

                return PlayerLib.this;
            }
        });

        rawset("Kick", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                player.kick(ComponentUtils.luaValueToComponent(message));

                return LuaValue.NIL;
            }
        });

        rawset("Ban", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue reason, LuaValue length, LuaValue source) {
                player.ban(ComponentUtils.toLegacy(ComponentUtils.luaValueToComponent(reason)), Duration.ofSeconds(LuaErrorAssert.checkInt(length, "Ban", 2, player)), LuaErrorAssert.checkString(source, "Ban", 3, player));

                return LuaValue.NIL;
            }
        });

        rawset("BanIP", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaValue reason = args.arg(1);
                LuaValue length = args.arg(2);
                LuaValue source = args.arg(3);
                LuaValue shouldKick = args.arg(4);

                player.banIp(ComponentUtils.toLegacy(ComponentUtils.luaValueToComponent(reason)), Duration.ofSeconds(LuaErrorAssert.checkInt(length, "Ban", 2, player)), LuaErrorAssert.checkString(source, "Ban", 3, player), LuaErrorAssert.checkBoolean(shouldKick, "BanIp", 4, player));
            
                return LuaValue.NIL;
            }
        });

        rawset("RunCommand", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue cmd) {
                player.performCommand(LuaErrorAssert.checkString(cmd, "RunCommand", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("SetOp", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue opStatus) {
                player.setOp(LuaErrorAssert.checkBoolean(opStatus, "SetOp", 1, player));

                return PlayerLib.this;
            }
        });

        rawset("Give", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue itemstack) {
                ItemStack item = ((ItemStackLib) itemstack).getItemStack();

                player.give(item);

                return PlayerLib.this;
            }
        });

        rawset("Inventory", new PlayerInventoryLib(player));
    }

    public static LuaFunction playerFromName() {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                String playerName = LuaErrorAssert.checkString(name, "GetPlayerFromName", 1, null);
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) return new PlayerLib(player);
                
                return LuaValue.NIL;
            }
        };
    }

    public static LuaFunction playerFromUUID() {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue uuid) {
                UUID playerUUID = UUID.fromString(LuaErrorAssert.checkString(uuid, "GetPlayerFromUUID", 1, null));
                Player player = Bukkit.getPlayer(playerUUID);

                return new PlayerLib(player);
            }
        };
    }

    public static LuaFunction getAllPlayers() {
        return new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable table = new LuaTable();
                Integer i = 1;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    table.insert(i++, new PlayerLib(player));
                }

                return table;
            }
        };
    }

    public static LuaFunction sendMessagesTo() {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue tbl, LuaValue msg) {
                for (int i = 0; i <= tbl.checktable().length(); i++) {
                    Player player = ((PlayerLib) tbl.get(i)).getPlayer();

                    player.sendMessage(ComponentUtils.luaValueToComponent(msg));
                }

                return LuaValue.NIL;
            }
        };
    }

    public Player getPlayer() {
        return player;
    }
}