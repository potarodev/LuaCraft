package com.luacraft;

import org.luaj.vm2.LuaError;

public class LuaGuard {
    private static final ThreadLocal<Integer> instructions = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> limit = ThreadLocal.withInitial(() -> 10_000);
    private static String fileName = "";

    public static void cleanup() {
        instructions.remove();
    }

    public static void prepare(int max, String filename) {
        instructions.set(0);
        limit.set(max);
        fileName = filename;
    }

    public static void check() {
        int count = instructions.get() + 1;
        if (count > limit.get()) {
            throw new LuaError("Script '" + fileName + "': instruction limit exceeded");
        }
        instructions.set(count);
    }
}