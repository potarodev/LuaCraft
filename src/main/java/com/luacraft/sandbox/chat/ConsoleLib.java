package com.luacraft.sandbox.chat;

import org.bukkit.command.ConsoleCommandSender;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;

public class ConsoleLib extends LuaTable {
    public ConsoleLib(ConsoleCommandSender console) {
        rawset("SendMessage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue msg) {
                console.sendMessage(LuaErrorAssert.checkString(msg, "SendMessage", 1, null));

                return ConsoleLib.this;
            }
        });
    }
}
