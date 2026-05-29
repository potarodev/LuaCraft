package com.luacraft.sandbox.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.ThreeArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.chat.ConsoleLib;
import com.luacraft.sandbox.entity.PlayerLib;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public class CommandLib extends LuaTable {
    
    public CommandLib(String file) {
        rawset("New", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue cmdName, LuaValue config, LuaValue callback) {
                String name;
                List<String> aliases = new ArrayList<>();

                if (cmdName.isstring()) {
                    name = LuaErrorAssert.checkString(cmdName, "New", 1, null);
                } else if (cmdName.istable()) {
                    LuaTable nameTable = LuaErrorAssert.checkTable(cmdName, "New", 1, null);
                    name = LuaErrorAssert.checkString(nameTable.get(1), "New", 1, null);
                    for (int i = 2; i <= nameTable.length(); i++) {
                        aliases.add(LuaErrorAssert.checkString(nameTable.get(i), "New", 1, null));
                    }
                } else {
                    name = "LuaCraftCommand" + ThreadLocalRandom.current().nextInt(1, 99);
                }

                CommandAPICommand command = new CommandAPICommand(name);
                command.withAliases(aliases.toArray(new String[0]));
                List<Argument<?>> arguments = new ArrayList<>();

                if (!config.isnil()) {
                    LuaTable configTable = LuaErrorAssert.checkTable(config, "New", 2, null);
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

                        Argument<?> argument = switch (argumentType) {
                            case "PLAYER" -> new EntitySelectorArgument.OnePlayer(name);
                            case "INTEGER", "NUMBER" -> new IntegerArgument(argumentName);
                            case "STRING", "TEXT" -> new StringArgument(argumentName);
                            case "GREEDY", "GREEDYSTRING" -> new GreedyStringArgument(argumentName);
                            case "BOOLEAN", "BOOL" -> new BooleanArgument(argumentName);
                            default -> new StringArgument(argumentName);
                        };

                        if (argumentSuggestions != null) {
                            LuaTable suggestions = argumentSuggestions;
                            argument.replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info -> {
                                List<IStringTooltip> tooltips = new ArrayList<>();
                                LuaValue sKey = LuaValue.NIL;
                                while (true) {
                                    Varargs sEntry = suggestions.next(sKey);
                                    sKey = sEntry.arg1();
                                    if (sKey.isnil()) break;
                                    tooltips.add(StringTooltip.ofString(sKey.tojstring(), sEntry.arg(2).tojstring()));
                                }
                                return tooltips.toArray(new IStringTooltip[0]);
                            }));
                        }

                        arguments.add(argument);

                        if (argumentCallback != null && !argumentCallback.isnil()) {
                            LuaValue cb = argumentCallback;
                            List<Argument<?>> argsCopy = new ArrayList<>(arguments);
                            command.withArguments(argsCopy)
                                .executes((sender, args) -> {
                                    LuaValue luaSender = sender instanceof Player p ? new PlayerLib(p) : new ConsoleLib((ConsoleCommandSender) sender);
                                    LuaTable luaArgs = new LuaTable();
                                    for (Object val : args.args()) {
                                        if (val instanceof String s) luaArgs.insert(luaArgs.length() + 1, LuaValue.valueOf(s));
                                        if (val instanceof Boolean b) luaArgs.insert(luaArgs.length() + 1, LuaValue.valueOf(b));
                                        if (val instanceof Integer i) luaArgs.insert(luaArgs.length() + 1, LuaValue.valueOf(i));
                                    }
                                    cb.call(luaSender, luaArgs);
                                });
                        }
                    }
                }

                if (!callback.isnil()) {
                    LuaValue functionCallback = LuaErrorAssert.checkFunction(callback, "New", 3, null);
                    if (functionCallback != null) {
                        LuaValue cb = functionCallback;
                        command.executes((sender, args) -> {
                            LuaValue luaSender = sender instanceof Player p ? new PlayerLib(p) : new ConsoleLib((ConsoleCommandSender) sender);
                            cb.call(luaSender);
                        });
                    }
                }

                CommandAPI.unregister(name, true);
                command.register();

                return LuaValue.NIL;
            }
        });
    }
}
