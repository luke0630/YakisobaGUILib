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

import static org.luke.takoyakiLibrary.TakoUtility.toColor;

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
        getAndOpenGUI(eEnum, player);
    }
    public void OpenListGUI(Player player, Enum<L> lEnum) {
        getAndOpenGUI(lEnum, player);
    }

    public void  getAndOpenGUI(Enum<?> type, Player player) {
        Inventory inventory = null;
        for(var gui : guiList) {
            if(gui.getType() == type) {
                if(gui instanceof ListGUIAbstract<?> listGUIa) {
                    inventory = listGUIa.getInventoryList(player, playerCurrentPage);
                } else {
                    inventory = gui.getInventory(player);
                }
            }
        }
        if(inventory != null) {
            openGUI.put(player, type);
            player.openInventory(inventory);
            openGUI.put(player, type);
        } else {
            player.sendMessage(toColor("&c&lそのGUIは登録されていません。 GUI NAME:  " + type.toString() ));
            player.closeInventory();
        }
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
