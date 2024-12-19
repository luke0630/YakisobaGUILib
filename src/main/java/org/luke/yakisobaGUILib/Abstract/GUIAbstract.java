package org.luke.yakisobaGUILib.Abstract;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class GUIAbstract<E extends Enum<E>> {
    public Player player = null;

    public abstract E getType();
    public abstract Inventory getInventory();
    public abstract void InventoryClickListener(InventoryClickEvent event);
    public void onStart() {

    }
}
