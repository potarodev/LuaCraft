package com.luacraft.sandbox.chat;

import org.bukkit.Bukkit;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.sandbox.util.ComponentUtils;
public class ChatLib extends LuaTable {
    public static LuaFunction Broadcast() {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                Bukkit.getServer().sendMessage(ComponentUtils.luaValueToComponent(message));

                return LuaValue.NIL;
            }
        };
    }
}