package com.luacraft;

import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.Globals;
import com.google.common.reflect.ClassPath;

@SuppressWarnings("null")
public class RegisterListeners {
    public static void registerListeners(Plugin plugin, Map<String, Globals> allGlobals) throws IOException {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        String packageName = "com.luacraft.sandbox.events";
        int total = 0;
        
        for (ClassPath.ClassInfo classInfo : ClassPath.from(classLoader).getTopLevelClassesRecursive(packageName)) {
            try {
                Class<?> clazz = classInfo.load();

                if (Listener.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                    clazz.getDeclaredMethods(); 

                    Listener listener = (Listener) clazz.getConstructor(Map.class).newInstance(allGlobals);
                    
                    Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
                    total++;
                }
            } catch (LinkageError e) {
                plugin.getLogger().warning("Skipping " + classInfo.getSimpleName() + " due to missing dependency. You can safely ignore this if you do not use this event.");
            } catch (Throwable t) {
                plugin.getLogger().severe(t.getMessage());
            }
        }
        plugin.getLogger().info("[LuaCraft] Registered all " + total + " possible events");
    }
}