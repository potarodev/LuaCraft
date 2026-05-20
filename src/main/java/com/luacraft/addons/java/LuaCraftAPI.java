package com.luacraft.addons.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LuaCraftAPI {
    public record AddonEntry(String name, String version, LuaCraftAddon addon) {};
    private static final List<AddonEntry> registeredAddons = new ArrayList<>();

    public static void registerAddon(LuaCraftAddon addon) {
        String addonName = addon.getName();
        String addonVersion = addon.getVersion();

        registeredAddons.add(new AddonEntry(addonName, addonVersion, addon));
    };

    public static void unregisterAddon(LuaCraftAddon addon) {
        String addonName = addon.getName();
        String addonVersion = addon.getVersion();

        registeredAddons.remove(new AddonEntry(addonName, addonVersion, addon));
    }

    public static List<AddonEntry> getRegisteredAddons() {
        return Collections.unmodifiableList(registeredAddons);
    }
}