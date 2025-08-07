package com.cleardrop.listeners;

import com.cleardrop.ClearDropPlugin;
import com.cleardrop.utils.ItemNameTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.List;
import java.util.logging.Logger;

public class ShopListener implements Listener {
    
    private final ClearDropPlugin plugin;
    
    public ShopListener(ClearDropPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 檢查是否為商店界面
        if (!isShopInventory(event.getView().getTitle())) {
            return;
        }
        
        // 取消事件，防止物品被拿走
        event.setCancelled(true);
        
        // 檢查是否為玩家
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        // 檢查點擊的物品是否有效
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // 額外檢查：確保物品有有效的材質和數量
        if (clickedItem.getAmount() <= 0) {
            return;
        }
        
        // 處理特殊按鈕
        if (handleSpecialButtons(player, clickedItem)) {
            return;
        }
        
        // 處理物品購買 - 根據點擊類型決定購買數量
        int availableStock = getAvailableAmount(clickedItem);
        
        // 如果沒有庫存，直接返回錯誤訊息
        if (availableStock <= 0) {
            String message = plugin.getConfigManager().getMessage("shop.insufficient-stock");
            plugin.getMessageUtil().sendErrorMessage(player, message);
            return;
        }
        
        int purchaseAmount = 1; // 默認購買1個
        
        if (event.isShiftClick() && event.isLeftClick()) {
            // Shift + 左鍵 = 購買全部
            purchaseAmount = availableStock;
        } else if (event.isRightClick()) {
            // 右鍵 = 購買64個
            purchaseAmount = Math.min(64, availableStock);
        } else if (event.isLeftClick()) {
            // 左鍵 = 購買1個
            purchaseAmount = 1;
        }
        
        handleItemPurchase(player, clickedItem, purchaseAmount);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // 檢查是否為商店界面
        if (!isShopInventory(event.getView().getTitle())) {
            return;
        }
        
        // 可以在這裡添加關閉商店時的邏輯
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            // 玩家關閉商店
        }
    }
    
    private boolean isShopInventory(String title) {
        String shopName = plugin.getConfigManager().getShopName();
        return title.contains(shopName) || title.contains("掉落物商店") || title.contains("Drop-Shop");
    }
    
    private boolean handleSpecialButtons(Player player, ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        // 刷新按鈕
        if (displayName.contains("刷新") || displayName.contains("Refresh")) {
            plugin.getShopManager().refreshShop();
            plugin.getShopManager().openShop(player);
            plugin.getMessageUtil().sendSuccessMessage(player, "商店已刷新！");
            return true;
        }
        
        // 清空按鈕（僅管理員）
        if (displayName.contains("清空") || displayName.contains("Clear")) {
            if (!player.hasPermission("cleardrop.admin")) {
                plugin.getMessageUtil().sendErrorMessage(player, "您沒有權限執行此操作！");
                return true;
            }
            
            plugin.getShopManager().clearShop();
            plugin.getShopManager().openShop(player);
            plugin.getMessageUtil().sendSuccessMessage(player, "商店已清空！");
            return true;
        }
        
        // 關閉按鈕
        if (displayName.contains("關閉") || displayName.contains("Close")) {
            player.closeInventory();
            return true;
        }
        
        // 信息按鈕
        if (displayName.contains("信息") || displayName.contains("Info")) {
            sendShopInfo(player);
            return true;
        }
        
        return false;
    }
    
    private int getAvailableAmount(ItemStack item) {
        // 從商店管理器獲取該物品的可用數量
        return plugin.getShopManager().getItemAmount(item.getType());
    }
    
    private void handleItemPurchase(Player player, ItemStack item, int amount) {
        // 驗證物品有效性
        if (item == null || item.getType() == Material.AIR || amount <= 0) {
            plugin.getMessageUtil().sendErrorMessage(player, "無效的物品或數量！");
            return;
        }
        
        // 普通物品從ShopManager獲取價格
        double unitPrice = plugin.getShopManager().getItemPrice(item.getType());
        
        if (unitPrice <= 0) {
            plugin.getMessageUtil().sendErrorMessage(player, "無法獲取物品價格！");
            return;
        }
        
        // 檢查商店中是否有足夠的物品
        int availableAmount = getAvailableAmount(item);
        // 檢查購買數量
        
        if (availableAmount < amount) {
            amount = availableAmount;
            // 調整購買數量
        }
        
        double totalPrice = unitPrice * amount;
        
        // 檢查玩家是否有足夠的金錢
        if (!hasEnoughMoney(player, totalPrice)) {
            double balance = plugin.getVaultEconomy().getBalance(player);
            String message = plugin.getConfigManager().getMessage("shop.insufficient-funds")
                    .replace("{price}", String.format("%.2f", totalPrice))
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{balance}", String.format("%.2f", balance));
            plugin.getMessageUtil().sendErrorMessage(player, message);
            return;
        }
        
        // 扣除金錢
        if (!withdrawMoney(player, totalPrice)) {
            String message = plugin.getConfigManager().getMessage("shop.withdraw-failed");
            plugin.getMessageUtil().sendErrorMessage(player, message);
            return;
        }
        
        // 使用ShopManager的新購買方法來保持完整的NBT數據
        boolean purchaseSuccess = plugin.getShopManager().purchaseItem(player, item, amount);
        
        if (!purchaseSuccess) {
            // 購買失敗，退還金錢
            plugin.getVaultEconomy().depositPlayer(player, totalPrice);
            plugin.getMessageUtil().sendErrorMessage(player, "購買失敗，已退還金錢！");
            return;
        }
        
        // 刷新商店界面
        plugin.getShopManager().openShop(player);
        
        // 發送購買成功消息
        String successMessage = plugin.getConfigManager().getMessage("shop.purchase-success")
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", getItemDisplayName(item))
                .replace("{price}", String.format("%.2f", totalPrice));
        plugin.getMessageUtil().sendSuccessMessage(player, successMessage);
        
        // 記錄購買日誌
        plugin.getLogger().info("Player " + player.getName() + " purchased " + amount + " " + 
            item.getType() + " for " + totalPrice + " coins");
    }
    

    
    private boolean hasEnoughMoney(Player player, double amount) {
        return plugin.getVaultEconomy().hasEnoughMoney(player, amount);
    }
    
    private boolean withdrawMoney(Player player, double amount) {
        return plugin.getVaultEconomy().withdrawMoney(player, amount);
    }
    
    private String getItemDisplayName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            // 檢查是否為商店添加的翻譯名稱（格式：§e + 翻譯名稱）
            if (displayName.startsWith("§e")) {
                // 移除顏色代碼，返回純文字名稱
                return displayName.substring(2);
            }
            // 移除所有顏色代碼
            return displayName.replaceAll("§[0-9a-fk-or]", "");
        }
        return ItemNameTranslator.getChineseName(item.getType());
    }
    
    private void sendShopInfo(Player player) {
        int itemTypes = plugin.getShopManager().getShopItems().size();
        int totalItems = plugin.getShopManager().getTotalItemCount();
        
        plugin.getMessageUtil().sendInfoMessage(player, "商店信息:");
        plugin.getMessageUtil().sendRawMessage(player, "&7物品種類: &e" + itemTypes);
        plugin.getMessageUtil().sendRawMessage(player, "&7物品總數: &e" + totalItems);
        plugin.getMessageUtil().sendRawMessage(player, "&7點擊物品購買，點擊按鈕執行操作");
    }
    

}