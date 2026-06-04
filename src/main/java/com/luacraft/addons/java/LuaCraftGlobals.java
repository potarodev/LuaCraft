package com.luacraft.addons.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.luacraft.LuaCraft;
import org.bukkit.Bukkit;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

public class LuaCraftGlobals {
    private final Globals globals;
    private final Set<String> protectedGlobals = new HashSet<>(Set.of(
        "os", "io", "debug", "luajava",
        "Chat", "ServerEvent", "Itemstack", "Location", "Component",
        "MiniMessage", "Inventory", "Wait", "Halt", "PlayerUtil",
        "BlockUtil", "LocationUtil", "FancyNpcUtil", "Command",
        "SQL", "Color", "Vector", "Scoreboard", "Particle", "FancyNPC",
        "Chunk", "DamageSource", "BlockData", "BlockState"
    ));

    public LuaCraftGlobals(Globals globals) {
        this.globals = globals;

        if (LuaCraft.getInstance().getTestState().ENABLED) {
            try (InputStream stream = this.getClass().getResourceAsStream("/lust.lua")) {
                LuaValue value = globals.load(stream, "_lust", "t", globals).call();
                globals.set("Test", value);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Failed to load lust.lua for test mode: " + e.getMessage());
            }
        }
    }

    public void set(String key, LuaValue value) {
        if (protectedGlobals.contains(key)) {
            throw new IllegalArgumentException("Cannot override protected global from LuaCraft named: " + key);
        }

        globals.set(key, value);
    }
}