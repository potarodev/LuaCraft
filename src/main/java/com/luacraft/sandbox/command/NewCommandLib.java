package com.luacraft.sandbox.command;

import com.luacraft.LuaCraft;
import com.luacraft.LuaErrorAssert;
import org.bukkit.command.Command;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class NewCommandLib extends LuaTable {
    public NewCommandLib(String file) {
        rawset("Register", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 2) {
                    LuaErrorAssert.throwError("Command.Register expected at least 2 arguments, got " + args.narg(), null);
                }

                LuaErrorAssert.checkString(args.arg(1), "Register", 1, null);
                LuaErrorAssert.checkFunction(args.arg(args.narg()), "Register", 1, null);

                String[] literals = new String[args.narg() - 1];
                for (int i = 1; i < args.narg(); i++) {
                    if (args.isstring(i)) {
                        literals[i-1] = args.arg(i).tojstring();
                        continue;
                    }
                    if (args.istable(i)) {
                        LuaTable table = args.checktable(i);
                        try {
                            CommandArgument arg = (CommandArgument) table;
                            literals[i-1] = arg.toString();
                            continue;
                        } catch (ClassCastException e) {
                            LuaErrorAssert.throwError("Command argument " + i + " while registering command '" + String.join(" ", literals) + "' was a table, but not a valid argument", null);
                        }
                    }
                    LuaErrorAssert.throwError("Command argument " + i + " while registering command '" + String.join(" ", literals) + "' was a table, but not a valid argument", null);
                }

                LuaCraft.getInstance().getLogger().info("Registering command literals " + String.join(" ", literals));

                return LuaValue.NIL;
            }
        });
    }
}
