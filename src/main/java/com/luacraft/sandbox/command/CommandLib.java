package com.luacraft.sandbox.command;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.ThreeArgFunction;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.chat.ConsoleLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

public class CommandLib extends LuaTable {
    //public final static Map<String, Map<String, Command>> bukkitCommands = new HashMap<>();
    //private static CommandMap commandMap;
    //private final String fileName;

    public static void refreshAllPlayerCommands() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.updateCommands();
        }
    }
    
    public CommandLib(String file) {
        //this.fileName = file;

        rawset("New", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue cmdName, LuaValue config, LuaValue callback) {
                String name = LuaErrorAssert.checkString(cmdName, "New", 1, null);

                LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name);
                RequiredArgumentBuilder<CommandSourceStack, ?> current = null;

                if (!config.isnil()) {
                    LuaTable configTable = LuaErrorAssert.checkTable(config, "New", 2, null);
                    //LuaTable argumentConfigs = LuaErrorAssert.checkTable(configTable.get(i), "New", 2, null);
                    LuaValue key = LuaValue.NIL;

                    while (true) {
                        Varargs entry = configTable.next(key);
                        key = entry.arg1();
                        if (key.isnil()) break;

                        String argumentName = LuaErrorAssert.checkString(key, "New", 2, null);
                        LuaTable argumentConfig = LuaErrorAssert.checkTable(entry.arg(2), "New", 2, null);
                        String argumentType = LuaErrorAssert.checkString(argumentConfig.get(1), "New", 2, null);
                        LuaTable argumentSuggestions = LuaErrorAssert.checkTable(argumentConfig.get(2), "New", 3, null);
                        LuaValue argumentCallback = argumentSuggestions != null ? argumentConfig.get(3) : argumentConfig.get(2);

                        RequiredArgumentBuilder<CommandSourceStack, ?> argument = switch (argumentType) {
                            case "PLAYER" -> Commands.argument(argumentName, ArgumentTypes.player());
                            case "INTENGER", "NUMBER" -> Commands.argument(argumentName, IntegerArgumentType.integer());
                            case "STRING", "TEXT" -> Commands.argument(argumentName, StringArgumentType.string());
                            case "GREEDY", "GREEDYSTRING" -> Commands.argument(argumentName, StringArgumentType.greedyString());
                            case "WORD" -> Commands.argument(argumentName, StringArgumentType.word());
                            case "BOOLEAN", "BOOL" -> Commands.argument(argumentName, BoolArgumentType.bool());
                            default -> Commands.argument(argumentName, StringArgumentType.word());
                        };

                        if (argumentCallback != null) {
                            if (argumentSuggestions != null) {
                                argument.suggests((ctx, builder) -> {
                                    LuaValue sKey = LuaValue.NIL;
                                    while (true) {
                                        Varargs sEntry = argumentSuggestions.next(sKey);
                                        sKey = sEntry.arg1();
                                        if (sKey.isnil()) break;

                                        LuaValue sValue = sEntry.arg(2);

                                        String suggestion = LuaErrorAssert.checkString(sKey, "New", 2, null);
                                        String tooltip = LuaErrorAssert.checkString(sValue, "New", 2, null);

                                        builder.suggest(suggestion, new LiteralMessage(tooltip));
                                    }
                                    return builder.buildFuture();
                                });
                            }

                            argument.executes(ctx -> {
                                LuaValue sender = LuaValue.NIL;
                                if (ctx.getSource().getSender() instanceof Player player) {
                                        sender = new PlayerLib(player);
                                    } else if (ctx.getSource().getSender() instanceof ConsoleCommandSender console) {
                                        sender = new ConsoleLib(console);
                                }

                                argumentCallback.call(sender);

                                return Command.SINGLE_SUCCESS;
                            });

                            if (current == null) {
                                root.then(argument);
                            } else {
                                current.then(argument);
                            }
                            current = argument;
                        }
                    }
                }
                if (!callback.isnil()) {
                    LuaValue functionCallback = LuaErrorAssert.checkFunction(callback, "New", 3, null);
                    if (functionCallback != null) {
                        root.executes(ctx -> {
                            LuaValue sender = LuaValue.NIL;
                            if (ctx.getSource().getSender() instanceof Player player) {
                                sender = new PlayerLib(player);
                            } else if (ctx.getSource().getSender() instanceof ConsoleCommandSender console) {
                                sender = new ConsoleLib(console);
                            }

                            functionCallback.call(sender);

                            return Command.SINGLE_SUCCESS;
                        });
                    }
                }

                LuaCraft.dispatcher.register(root);

                return LuaValue.NIL;
            }
        });
    }

    
    //public static void commandUnRegister(String fileName) {
    //    Map<String, Command> commandsForFile = bukkitCommands.get(fileName);
    //    if (commandsForFile == null) return;
    //    
    //    Map<String, Command> knownCommands = commandMap.getKnownCommands();
//
    //    for (Map.Entry<String, Command> entry : commandsForFile.entrySet()) {
    //        String cmdName = entry.getKey();
    //        Command cmd = entry.getValue();
    //        
    //        cmd.unregister(commandMap);
    //        knownCommands.remove(cmdName);
    //        knownCommands.remove("LuaCraft:" + cmdName);
    //    }
//
    //    commandsForFile.clear();
    //    bukkitCommands.remove(fileName);
    //}
}
