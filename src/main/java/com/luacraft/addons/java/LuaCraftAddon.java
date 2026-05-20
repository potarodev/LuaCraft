package com.luacraft.addons.java;

public interface LuaCraftAddon {
    /**
     * Set the LuaCraft Addon name
     * <br><br>This can only be called prior to runtime, not during or after runtime
     * @param AddonName
     */
    void setName(String name);

    /**
     * Get the LuaCraft Addon name
     * @return AddonName
     */
    String getName();
    
    /**
     * Set the LuaCraft Addon Version
     * @param addonVersion
     */
    void setVersion(String addonVersion);

    /**
     * 
     * @return addonVersion
     */
    String getVersion();

    /**
     * Called when a LuaCraft Addon attempts to load, useful for setting globals
     * <br><br>This only gets called on initial loading. Addons remain static through reloads.
     * @param globals
     * @param scriptName
     */
    void onLoad(LuaCraftGlobals globals);

    /**
     * Called when a LuaCraft Addon attempts to unload, useful for cleaning up any leftover things
     * <br><br>This only gets called initial unloading. Addons remain static through reloads.
     * @param scriptName
     */
    void onUnload();
}