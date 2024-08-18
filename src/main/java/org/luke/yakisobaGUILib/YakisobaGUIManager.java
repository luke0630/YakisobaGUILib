package org.luke.yakisobaGUILib;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YakisobaGUIManager<E extends Enum<E>, L extends Enum<L>> implements Listener {
    private Map<Player, Enum<E>> openGUI = new HashMap<>();
    private Map<Player, Enum<L>> listGUI = new HashMap<>();

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
    }

    public void OpenGUI(Player player, Enum<E> eEnum) {
        openGUI.put(player, eEnum);
        var gui = getGUI(eEnum, player);
        player.openInventory(gui);
        openGUI.put(player, eEnum);
    }
    public void OpenListGUI(Player player, Enum<L> lEnum) {
        listGUI.put(player, lEnum);
        var gui = getGUI(lEnum, player);
        player.openInventory(gui);
        listGUI.put(player, lEnum);
    }

    Inventory getGUI(Enum<?> type, Player player) {
        for(var gui : guiList) {
            if(gui instanceof ListGUIAbstract<?> listGUIa) {
                return listGUIa.getInventory(player, playerCurrentPage);
            }
            if(gui.getType() == type) return gui.getInventory();
        }
        return null;
    }


    
    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        var clickedInventory = event.getClickedInventory();
        var topInventory = player.getOpenInventory().getTopInventory();
        if(clickedInventory != topInventory) return;

        for(var openSet : openGUI.values()) {
            for(var gui : guiList) {
                if(gui.getType() == openSet) {
                    gui.InventoryClickListener(event);
                    return;
                }
            }
        }
        for(var openSet : listGUI.values()) {
            for(var gui : guiList) {
                if(gui.getType() == openSet) {
                    if(gui instanceof ListGUIAbstract<?> listGUI) {
                        listGUI.InventoryClickListener(event, playerCurrentPage);
                        break;
                    }
                }
            }
        }
    }
}
