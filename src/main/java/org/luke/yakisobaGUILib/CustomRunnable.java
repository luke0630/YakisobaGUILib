package org.luke.yakisobaGUILib;

import org.bukkit.event.inventory.InventoryClickEvent;

public class CustomRunnable {
    @FunctionalInterface
    public interface InventoryRunnable {
        void run(InventoryClickEvent event);
    }
}
