package org.luke.yakisobaGUILib;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import java.util.List;

public final class YakisobaGUILib {
    @Getter
    private static YakisobaGUILib instance;
    @Getter
    private JavaPlugin plugin;
    @Getter
    private final YakisobaGUIManager<?, ?> guiManager;

    public YakisobaGUILib(JavaPlugin javaPlugin, List<GUIAbstract<?>> guiList) {
        this.plugin = javaPlugin;
        instance = this;
        guiManager = new YakisobaGUIManager<>();
        guiManager.Initialization(guiList);
    }
}