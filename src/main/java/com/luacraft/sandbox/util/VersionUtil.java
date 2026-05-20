package com.luacraft.sandbox.util;

public class VersionUtil {
    private static final boolean HAS_SOME_FEATURE = checkClass("org.bukkit.SomeClass");

    private static boolean checkClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasSomeFeature() {
        return HAS_SOME_FEATURE;
    }
}