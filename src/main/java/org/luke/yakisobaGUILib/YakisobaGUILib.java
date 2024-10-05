package org.luke.yakisobaGUILib;

import org.bukkit.plugin.java.JavaPlugin;

public final class YakisobaGUILib extends JavaPlugin {

    public static YakisobaGUILib getInstance() {
        return instance;
    }

    private static YakisobaGUILib instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
