package com.luacraft.sandbox.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.PlayerLib;

public class CommandExecute extends Command {
    private final LuaFunction function;

    private LuaFunction tabCompleteFunction;

    public static LuaValue executeConsoleCommand() {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue cmd) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LuaErrorAssert.checkString(cmd, "RunConsoleCommand", 1, null));

                return LuaValue.NIL;
            }
        };
    }

    public CommandExecute(String name, LuaFunction function) {
        super(name);
        this.function = function;
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        LuaValue senderValue;
        LuaTable argsValue = new LuaTable();

        if (sender instanceof Player player) {
            senderValue = new PlayerLib(player);
        } else {
            // TODO Console sender is raw Java userdata. Replace with CommandSenderLib wrapper.
            senderValue = CoerceJavaToLua.coerce(sender);
        }

        for (int i = 0; i < args.length; i++) {
            argsValue.set(i + 1, LuaValue.valueOf(args[i]));
        }

        function.call(senderValue, argsValue);

        return true;
    }

    public void setTabCompleteFunction(LuaFunction tabCompleteFunction) {
        this.tabCompleteFunction = tabCompleteFunction;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (tabCompleteFunction == null || tabCompleteFunction.isnil()) {
            return completions;
        }

        LuaValue senderValue;
        LuaTable argsValue = new LuaTable();

        if (sender instanceof Player player) {
            senderValue = new PlayerLib(player);
        } else {
            // TEMP: Console sender is raw Java userdata. Replace with CommandSenderLib wrapper.
            senderValue = CoerceJavaToLua.coerce(sender);
        }

        for (int i = 0; i < args.length; i++) {
            argsValue.set(i + 1, LuaValue.valueOf(args[i]));
        }

        LuaValue result = tabCompleteFunction.call(senderValue, argsValue);

        if (result.istable()) {
            LuaTable resultTable = result.checktable();

            for (int i = 1; i <= resultTable.length(); i++) {
                LuaValue value = resultTable.get(i);
                if (value.isstring()) {
                    completions.add(value.tojstring());
                }
            }
        }

        return completions;
    }
}
