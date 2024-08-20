package org.luke.yakisobaGUILib;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YakisobaGUIManager<E extends Enum<E>, L extends Enum<L>> implements Listener {
    private Map<Player, Enum<?>> openGUI = new HashMap<>();

    private List<GUIAbstract<?>> guiList = new ArrayList<>();
    private Map<Player, Integer> playerCurrentPage = new HashMap<>();

    public Map<Player, Integer> getPlayerCurrentPage() {
        return playerCurrentPage;
    }

    public YakisobaGUIManager() {
    }

    public void Initialization(JavaPlugin plugin, List<GUIAbstract<?>> guiList) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
        this.guiList = guiList;
        for(var gui : guiList) {
            if(gui instanceof ListGUIAbstract<?> listGUI) {
                listGUI.setOpenGUI(openGUI);
            }
        }
    }

    public void OpenGUI(Player player, Enum<E> eEnum) {
        openGUI.put(player, eEnum);
        var gui = getGUI(eEnum, player);
        player.openInventory(gui);
        openGUI.put(player, eEnum);
    }
    public void OpenListGUI(Player player, Enum<L> lEnum) {
        openGUI.put(player, lEnum);
        var gui = getGUI(lEnum, player);
        player.openInventory(gui);
        openGUI.put(player, lEnum);
    }

    Inventory getGUI(Enum<?> type, Player player) {
        for(var gui : guiList) {
            if(gui instanceof ListGUIAbstract<?> listGUIa) {
                return listGUIa.getInventoryList(player, playerCurrentPage);
            }
            if(gui.getType() == type) return gui.getInventory(player);
        }
        return null;
    }


    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        openGUI.remove(player);
    }


    
    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        var clickedInventory = event.getClickedInventory();
        var topInventory = player.getOpenInventory().getTopInventory();

        if(!openGUI.containsKey(player)) return;
        event.setCancelled(true);
        if(clickedInventory != topInventory) return;
        event.setCancelled(true);

        for(var openSet : openGUI.values()) {
            for(var gui : guiList) {
                if(gui.getType() == openSet) {
                    if(gui instanceof ListGUIAbstract<?> listGUI) {
                        listGUI.InventoryClickListener(event, playerCurrentPage);
                        break;
                    }
                    gui.InventoryClickListener(event);
                    break;
                }
            }
        }

    }
}
