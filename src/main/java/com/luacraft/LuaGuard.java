package com.luacraft;

import org.luaj.vm2.LuaError;

public class LuaGuard {
    private static final ThreadLocal<Long> windowStart = ThreadLocal.withInitial(() -> 0L);
    private static final ThreadLocal<Integer> instructions = ThreadLocal.withInitial(() -> 0);
    private static String fileName = "";

    public static void cleanup() {
        instructions.remove();
    }

    public static void prepare(String filename) {
        instructions.set(0);
        fileName = filename;
    }

    public static void check() {
        long now = System.currentTimeMillis();
        long start = windowStart.get();

        if (now - start > 50) {
            windowStart.set(now);
            instructions.set(0);
        }

        int count = instructions.get() + 1;
        if (count > 1000) {
            throw new LuaError("Script '" + fileName + "': infinite loop detected");
        }
        instructions.set(count);
    }
}