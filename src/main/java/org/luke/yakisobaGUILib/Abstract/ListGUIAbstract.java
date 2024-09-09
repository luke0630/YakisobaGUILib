package org.luke.yakisobaGUILib.Abstract;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.luke.yakisobaGUILib.YakisobaGUIManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.luke.takoyakiLibrary.TakoUtility.*;
import static org.luke.yakisobaGUILib.CustomRunnable.*;

public abstract class ListGUIAbstract<L extends Enum<L>> extends GUIAbstract<L> {
    static final Integer GUI_SIZE = 9 * 6;
    static final Integer CLICK_CENTER = 9 * 5 + 4;
    static final Integer CLICK_BACK = 9 * 5;
    static final Integer START_BAR_INDEX = 9 * 5;
    static final Integer GUI_ITEM_SIZE = 9 * 5;

    public Map<Integer, ItemStack> controllerItems = new HashMap<>();

    public Player player = null;

    public void setOpenGUI(Map<Player, Enum<?>> openGUI) {
        this.openGUI = openGUI;
    }

    private Map<Player, Enum<?>> openGUI;

    /////****ListGUIAbstractを継承する際にOverrideしなくていいものをここでしておくことで使えないようにする****////

    @Override
    public Inventory getInventory(Player player) {
        return null;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent event) {

    }
    /////****************************************************************************************////

    public abstract String getGUITitle();

    public abstract List<ItemStack> getItemList();

    public abstract ItemStack setCenterItemStack();

    public abstract InventoryRunnable whenClickContent();

    public abstract InventoryRunnable whenClickCenter();

    public abstract InventoryRunnable whenClickBack();
    public void customInventoryClickEvent(InventoryClickEvent event) {

    }

    public void InventoryClickListener(InventoryClickEvent event, Map<Player, Integer> pageMap) {
        customInventoryClickEvent(event);
        // 共通のクリックイベント処理
        Player player = (Player) event.getWhoClicked();

        var slot = event.getSlot();
        event.setCancelled(true);
        InventoryRunnable inventoryRunnable = null;

        //------------下のバーの動作・コンテンツのクリック------------
        if(CLICK_CENTER == slot) {
            inventoryRunnable = whenClickCenter();
        } else if(CLICK_BACK == slot) {
            inventoryRunnable = whenClickBack();
        } else if(slot < 5*9) {
            if(event.getCurrentItem() != null) {
                inventoryRunnable = whenClickContent();
            }
        }
        if(inventoryRunnable != null) {
            inventoryRunnable.run(event);
        }
        //------------下のバーの動作・コンテンツのクリック------------

        //------------下のバーの戻る次へボタン-----------
        var currentOpenPage = pageMap.get(player);
        if(currentOpenPage == null) return;
        var maxPage = getMaxPage( getItemList().size() );
        if(currentOpenPage > 0 && slot == START_BAR_INDEX + 7) {
            //戻る
            pageMap.replace(player, pageMap.get(player)-1);
            player.openInventory(getInventoryList(player, pageMap));
            openGUI.put(player, getType());
        } else if(currentOpenPage < maxPage && slot == START_BAR_INDEX+8) {
            //次へ
            pageMap.replace(player, pageMap.get(player)+1);
            player.openInventory(getInventoryList(player, pageMap));
            openGUI.put(player, getType());
        }
        //------------下のバーの戻る次へボタン-----------
    }


    //DO NOT ABSTRACT
    public Inventory getInventoryList(Player player, Map<Player, Integer> pageMap) {

        this.player = player;

        var items = getItemList();
        Inventory inventory = getInitInventory(GUI_SIZE, toColor(getGUITitle()));

        //----------下の操作バー----------
        for (int i = START_BAR_INDEX; i < GUI_SIZE; i++) {
            if (CLICK_CENTER == i && whenClickCenter() != null) {
                if(setCenterItemStack() != null) {
                    inventory.setItem(i, setCenterItemStack());
                } else {
                    inventory.setItem(i, getItem(Material.REDSTONE_BLOCK, "&cインタラクションする"));
                }
                continue;
            }
            if (CLICK_BACK == i && whenClickBack() != null) {
                inventory.setItem(i, getItem(Material.FEATHER, "&c&l戻る"));
                continue;
            }
            inventory.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }
        if (items == null) return inventory;

        if (items.size() > START_BAR_INDEX) {
            //ページが必要な場合
            int maxPage = getMaxPage(items.size());
            if (!pageMap.containsKey(player) || maxPage < pageMap.get(player)) {
                //1--Mapに存在しない場合
                //2--現在開いているページや次のページや前のページが削除されて、戻るボタンや次のボタンを押されたときにNULLが出るため0ページ目に戻ることでエラーを回避する
                pageMap.put(player, 0);
            }

            int openPage = pageMap.get(player);
            int minIndex = openPage * 45;

            inventory = Bukkit.createInventory(null, GUI_SIZE, toColor(getGUITitle() + " &8&lページ" + (openPage + 1) + "/" + (maxPage + 1)));

            //戻るボタンの表示
            if (openPage > 0) {
                inventory.setItem(START_BAR_INDEX + 7, getItem(Material.FEATHER, "&c&l戻る"));
            }
            //次へボタンの表示
            if (openPage < maxPage) {
                inventory.setItem(START_BAR_INDEX + 8, getItem(Material.ARROW, "&b&l次のページ"));
            }
            for (int i = 0; i < START_BAR_INDEX; i++) {
                try {
                    inventory.setItem(i, items.get(minIndex + i));
                } catch (Exception e) {
                    break;
                }
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                inventory.setItem(i, items.get(i));
            }
        }

        //カスタムバー//
        for(var mapSet : controllerItems.entrySet()) {
            inventory.setItem(mapSet.getKey() + START_BAR_INDEX, mapSet.getValue());
        }

        return inventory;
    }

    private Integer getMaxPage(Integer size) {
        var result = (int) size / GUI_ITEM_SIZE;
        if (size % GUI_ITEM_SIZE == 0) result -= 1; //あまりがない時、何もないページができてしまうためそれを消す
        return result;
    }
}
