package org.luke.yakisobaGUILib.Abstract;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class GUIAbstract<E extends Enum<E>> {
    public abstract String getGUITitle();
    public abstract Enum<E> getType();
    public abstract Inventory getInventory(Player player);
    public abstract void InventoryClickListener(InventoryClickEvent event);
}
