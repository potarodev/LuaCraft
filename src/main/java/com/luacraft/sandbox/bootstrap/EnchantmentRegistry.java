package com.luacraft.sandbox.bootstrap;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentRegistry {
    private static final List<EnchantmentDefinitions> definitions = new ArrayList<>();

    public static void define(EnchantmentDefinitions def) {
        definitions.add(def);
    }

    public static List<EnchantmentDefinitions> getDefinitions() {
        return definitions;
    }
}
