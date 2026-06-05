package com.luacraft.test;

import org.bukkit.Bukkit;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

public class TestGlobals {
    public static void loadAll(Globals globals) {
        globals.set("io", new TestIoGlobals());
        globals.set("os", new TestOsGlobals());
        globals.load(new DebugLib());

        try (InputStream stream = TestGlobals.class.getResourceAsStream("/lester.lua")) {
            LuaValue value = globals.load(stream, "testlib", "t", globals).call();
            globals.set("Test", value);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to load lust.lua for test mode: " + e.getMessage());
        }
    }

    private static class TestIoGlobals extends LuaTable {
        public TestIoGlobals() {
            rawset("write", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    for (int i = 1, n = args.narg(); i <= n; i++)
                        System.out.print(args.checkstring(i));
                    return args;
                }
            });
            rawset("flush", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    System.out.flush();
                    return args;
                }
            });
        }
    }

    private static final long t0 = System.currentTimeMillis();

    private static class TestOsGlobals extends LuaTable {
        public TestOsGlobals() {
            rawset("getenv", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue envKey) {
                    String env = System.getProperty(envKey.checkjstring());
                    return env != null ? LuaString.valueOf(env) : NIL;
                }
            });
            rawset("clock", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaDouble.valueOf((System.currentTimeMillis() - t0) / 1000.0);
                }
            });
        }
    }
}
