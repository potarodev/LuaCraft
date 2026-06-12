package com.luacraft.sandbox.command.args;

import com.luacraft.sandbox.command.CommandArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class StringArgument extends CommandArgument {
    public StringArgument(String name, LuaTable options) {

    }

    @Override
    protected ArgumentType buildArgument() {
//        TODO: options like type=greedy, single_word, etc
        return StringArgumentType.string();
    }

}
