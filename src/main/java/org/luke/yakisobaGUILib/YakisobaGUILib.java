package org.luke.yakisobaGUILib;

import lombok.Getter;
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
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static org.luke.takoyakiLibrary.TakoUtility.toColor;

public final class YakisobaGUILib<E extends Enum<E>, L extends Enum<L>> implements Listener {
    @Getter
    private static YakisobaGUILib<?, ?> instance;

    @Getter
    private Map<Player, GUIAbstract<?>> openGUI = new WeakHashMap<>();
    private List<GUIAbstract<?>> guiList = new ArrayList<>();
    @Getter
    private Map<Player, Integer> playerCurrentPage = new WeakHashMap<>();

    public YakisobaGUILib(JavaPlugin plugin, List<GUIAbstract<?>> guiList) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
        this.guiList = guiList;
    }

    public void OpenGUI(Player player, E eEnum) {
        getAndOpenGUI(eEnum, player);
    }
    public void OpenListGUI(Player player, L lEnum) {
        getAndOpenGUI(lEnum, player);
    }

    public void  getAndOpenGUI(Enum<?> type, Player player) {
        Inventory inventory = null;
        GUIAbstract<? extends Enum<?>> newInstance = null;
        for(var gui : guiList) {
            if(gui.getType() == type) {
                //クラスを抽出
                Class<?> clazz = gui.getClass();

                // 新しいインスタンスを作成
                try {
                    newInstance = (GUIAbstract<?>) clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(newInstance instanceof ListGUIAbstract<?> listGUIa) {
                    inventory = listGUIa.getInventoryList(player, playerCurrentPage);
                } else {
                    inventory = newInstance.getInventory(player);
                }
                break;
            }
        }
        if(inventory != null) {
            openGUI.put(player, newInstance);
            player.openInventory(inventory);
            openGUI.put(player, newInstance);
        } else {
            try{
                player.sendMessage(toColor("&c&lそのGUIは登録されていません。 GUI NAME:  " + type.toString() ));
                player.closeInventory();
            } catch (Exception E) {
                player.sendMessage(toColor("&c&lそのGUIは開けませんでした。"));
            }
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

        if(openGUI.get(player) instanceof ListGUIAbstract<?> listGUI) {
            listGUI.InventoryClickListener(event, playerCurrentPage);
        } else {
            openGUI.get(player).InventoryClickListener(event);
        }
    }
}