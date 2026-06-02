package com.luacraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.luacraft.addons.java.LuaCraftAPI;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.ComponentUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class LuaCraftCommand {
    public LuaCraftCommand(Plugin plugin, File pluginScriptsFolder, Map<String, Globals> allGlobals) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            
            registrar.register(
                Commands.literal("lua")
                    .requires(sender -> sender.getSender().hasPermission("lua.command"))
                    .executes(ctx -> {
                        ctx.getSource().getSender().sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &6Try running &4/lua help &6to see all available commands!"));
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.literal("reload")
                        .then(Commands.argument("file", StringArgumentType.greedyString())
                            .suggests((ctx, builder) -> {
                                List<String> completions = new ArrayList<>();
                                collectCompletions(pluginScriptsFolder, pluginScriptsFolder, completions);
                                completions.forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                String fileName = StringArgumentType.getString(ctx, "file");
                                CommandSender sender = ctx.getSource().getSender();
                                long startTime = System.nanoTime();
                                try {
                                    ScriptLoader.loadSingleScript(allGlobals, fileName);
                                    long durationMs = (System.nanoTime() - startTime) / 1000000;
                                    sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &6Reloaded " + fileName + " in " + durationMs + "ms"));
                                } catch (IOException | LuaError e) {
                                    sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] Lua Script Error: &c" + e.getMessage()));
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                    .then(Commands.literal("reloadall")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            long startTime = System.nanoTime();
                            try {
                                ScriptLoader.loadAllScripts(allGlobals);
                                long durationMs = (System.nanoTime() - startTime) / 1000000;
                                sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &6Reloaded all scripts in " + durationMs + "ms"));
                            } catch (IOException | LuaError e) {
                                sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] Lua Script Error: &c" + e.getMessage()));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(Commands.literal("info")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            String version = LuaCraft.getVersion();
                            String addons = LuaCraftAPI.getRegisteredAddons().stream()
                                .map(entry -> entry.name() + " v" + entry.version())
                                .collect(Collectors.joining(", "));
                            sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &6Version: &dv" + version));
                            sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &6Installed Addons: &d" + addons));
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(Commands.literal("help")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &4/lua info &6- see info about LuaCraft"));
                            sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &4/lua reload [file] &6- reload a script"));
                            sender.sendMessage(ComponentUtils.parseLegacy("[&7LuaCraft&r] &4/lua reloadall &6- reload all scripts"));
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(Commands.literal("run")
                        .then(Commands.argument("code", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                Globals replGlobals = JsePlatform.standardGlobals();
                                ScriptLoader.setupGlobals(replGlobals, "repl");

                                if (sender instanceof Player player) {
                                    replGlobals.set("sender", new PlayerLib(player));
                                }

                                String code = StringArgumentType.getString(ctx, "code");
                                try {
                                    replGlobals.load(code, "@repl").call();
                                } catch (LuaError e) {
                                    sender.sendMessage(ComponentUtils.parseLegacy("&cLua Error: " + e.getMessage()));
                                }

                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                    .build(),
                "Main LuaCraft command"
            );
        });
    }

    private void collectCompletions(File root, File folder, List<String> result) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectCompletions(root, file, result);
            } else if (!file.getName().startsWith("-") && file.getName().endsWith(".lua")) {
                String relative = root.toURI().relativize(file.toURI()).getPath();
                result.add(relative);
            }
        }
    }
}
