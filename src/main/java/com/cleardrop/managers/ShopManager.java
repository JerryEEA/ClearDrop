package com.cleardrop.managers;

import com.cleardrop.ClearDropPlugin;
import com.cleardrop.utils.ItemNameTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class ShopManager {
    
    private final ClearDropPlugin plugin;
    private final Map<String, ItemStack> shopItems; // 改為儲存完整ItemStack
    private final Map<String, Integer> itemPrices; // 使用物品ID作為key
    private final List<Material> bannedItems; // 禁止物品列表
    
    public ShopManager(ClearDropPlugin plugin) {
        this.plugin = plugin;
        this.shopItems = new HashMap<>();
        this.itemPrices = new HashMap<>();
        this.bannedItems = Arrays.asList(
            Material.WRITTEN_BOOK, // 禁止成書（可能包含不當內容）
            Material.COMMAND_BLOCK,
            Material.COMMAND_BLOCK_MINECART,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.STRUCTURE_BLOCK,
            Material.JIGSAW,
            Material.BARRIER,
            Material.BEDROCK
        );
        loadShopData();
    }
    
    public void addItemsToShop(List<Item> items) {
        int bannedItemsRemoved = 0;
        int itemsAdded = 0;
        
        for (Item item : items) {
            ItemStack itemStack = item.getItemStack();
            Material material = itemStack.getType();
            int amount = itemStack.getAmount();
            
            // 檢查是否為禁止物品
            if (bannedItems.contains(material)) {
                bannedItemsRemoved++;
                plugin.getLogger().warning("已移除禁止物品: " + material.name() + " x" + amount);
                continue;
            }
            
            // 檢查物品是否有特殊 NBT 數據（現在允許所有物品）
            if (hasSpecialNBT(itemStack)) {
                // 無效物品跳過
                continue;
            }
            
            // 生成物品唯一ID
            String itemId = generateItemId(itemStack);
            
            // 檢查是否已存在相同的物品
            if (shopItems.containsKey(itemId)) {
                // 合併相同物品的數量
                ItemStack existingItem = shopItems.get(itemId);
                existingItem.setAmount(existingItem.getAmount() + amount);
            } else {
                // 添加新物品到商店
                ItemStack shopItem = itemStack.clone();
                shopItems.put(itemId, shopItem);
                itemPrices.put(itemId, plugin.getConfigManager().getItemPrice(material));
            }
            
            itemsAdded += amount;
        }
        
        if (bannedItemsRemoved > 0) {
            plugin.getLogger().info("清理過程中移除了 " + bannedItemsRemoved + " 個禁止物品");
        }
        
        if (itemsAdded > 0) {
            plugin.getLogger().info("成功添加了 " + itemsAdded + " 個物品到商店");
        }
        
        // 自動保存數據
        saveShopData();
    }
    
    // 生成物品唯一ID，基於物品類型和NBT數據
    private String generateItemId(ItemStack item) {
        StringBuilder id = new StringBuilder();
        id.append(item.getType().name());
        
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            
            // 添加附魔信息
            if (meta.hasEnchants()) {
                id.append("_enchants:");
                meta.getEnchants().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey((e1, e2) -> e1.getKey().getKey().compareTo(e2.getKey().getKey())))
                    .forEach(entry -> id.append(entry.getKey().getKey().getKey())
                        .append(":").append(entry.getValue()).append(";"));
            }
            
            // 添加自定義名稱
            if (meta.hasDisplayName()) {
                id.append("_name:").append(meta.getDisplayName().replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", ""));
            }
            
            // 添加lore
            if (meta.hasLore()) {
                id.append("_lore:");
                for (String line : meta.getLore()) {
                    id.append(line.replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "")).append(";");
                }
            }
            
            // 添加耐久度
            if (item.getType().getMaxDurability() > 0) {
                id.append("_durability:").append(item.getDurability());
            }
        }
        
        return id.toString();
    }
    
    private boolean hasSpecialNBT(ItemStack item) {
        // 現在允許所有物品進入商店，包括：
        // - 附魔物品和附魔書
        // - 自定義名稱物品
        // - 帶lore的物品
        // - 耐久度損耗的工具
        // 只過濾真正危險或無效的物品
        
        // 檢查是否為空氣或無效物品
        if (item == null || item.getType().isAir()) {
            return true;
        }
        
        // 允許所有其他物品進入商店
        return false;
    }
    

    
    public void openShop(Player player) {
        if (!plugin.getConfigManager().isShopEnabled()) {
            player.sendMessage(plugin.getMessageUtil().colorize(
                plugin.getConfigManager().getMessage("prefix") + "&c商店功能已停用！"));
            return;
        }
        
        String shopName = plugin.getConfigManager().getShopName();
        Inventory shopInventory = Bukkit.createInventory(null, 54, shopName);
        
        // 添加商店物品
        int slot = 0;
        
        // 添加商店物品
        for (Map.Entry<String, ItemStack> entry : shopItems.entrySet()) {
            if (slot >= 45) break; // 保留最後一行給控制按鈕
            
            String itemId = entry.getKey();
            ItemStack originalItem = entry.getValue();
            int amount = originalItem.getAmount();
            int price = itemPrices.getOrDefault(itemId, 10);
            
            // 創建顯示物品，保留原始NBT數據
            ItemStack displayItem = originalItem.clone();
            displayItem.setAmount(Math.min(amount, 64));
            
            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                // 保留原始名稱，如果沒有則使用翻譯名稱
                if (!meta.hasDisplayName()) {
                    meta.setDisplayName("§e" + getItemDisplayName(originalItem.getType()));
                }
                
                // 添加商店信息到lore
                List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add("");
                lore.add("§7庫存: §a" + amount);
                lore.add("§7價格: §6" + price + " 金幣");
                lore.add("");
                lore.add("§e左鍵購買 1 個");
                lore.add("§e右鍵購買 64 個");
                lore.add("§eShift+左鍵購買全部");
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }
            
            shopInventory.setItem(slot++, displayItem);
        }
        

        
        // 添加控制按鈕
        addControlButtons(shopInventory);
        
        player.openInventory(shopInventory);
        
        String message = plugin.getConfigManager().getMessage("shop.opened");
        player.sendMessage(plugin.getMessageUtil().colorize(
            plugin.getConfigManager().getMessage("prefix") + message));
    }
    
    private void addControlButtons(Inventory inventory) {
        // 刷新按鈕
        ItemStack refreshButton = new ItemStack(Material.EMERALD);
        ItemMeta refreshMeta = refreshButton.getItemMeta();
        if (refreshMeta != null) {
            refreshMeta.setDisplayName("§a刷新商店");
            refreshMeta.setLore(Arrays.asList("§7點擊刷新商店內容"));
            refreshButton.setItemMeta(refreshMeta);
        }
        inventory.setItem(49, refreshButton);
        
        // 清空商店按鈕（僅管理員可見）
        ItemStack clearButton = new ItemStack(Material.BARRIER);
        ItemMeta clearMeta = clearButton.getItemMeta();
        if (clearMeta != null) {
            clearMeta.setDisplayName("§c清空商店");
            clearMeta.setLore(Arrays.asList(
                "§7清空商店所有物品",
                "§c僅管理員可用"
            ));
            clearButton.setItemMeta(clearMeta);
        }
        inventory.setItem(53, clearButton);
        
        // 信息按鈕
        ItemStack infoButton = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoButton.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§b商店信息");
            List<String> infoLore = new ArrayList<>();
            infoLore.add("§7總物品種類: §e" + shopItems.size());
            infoLore.add("§7總物品數量: §e" + getTotalItemCount());
            infoLore.add("");
            infoLore.add("§7這裡的物品來自於");
            infoLore.add("§7被清理的掉落物");
            infoMeta.setLore(infoLore);
            infoButton.setItemMeta(infoMeta);
        }
        inventory.setItem(45, infoButton);
    }
    
    // 新的購買方法，通過ItemStack購買
    public boolean purchaseItem(Player player, ItemStack displayItem, int amount) {
        // 從顯示物品中提取原始物品ID
        String itemId = findItemIdByDisplayItem(displayItem);
        
        // 調試日誌 - 根據配置決定是否輸出
        if (plugin.getConfigManager().isLogShopOperationsEnabled()) {
            plugin.getLogger().info("[DEBUG] 尋找物品ID: " + itemId);
            plugin.getLogger().info("[DEBUG] 商店物品數量: " + shopItems.size());
            plugin.getLogger().info("[DEBUG] 商店包含此ID: " + shopItems.containsKey(itemId));
        }
        
        if (itemId == null || !shopItems.containsKey(itemId)) {
            if (plugin.getConfigManager().isLogShopOperationsEnabled()) {
                plugin.getLogger().warning("[DEBUG] 購買失敗 - 找不到物品ID: " + itemId);
            }
            return false;
        }
        
        ItemStack originalItem = shopItems.get(itemId);
        int available = originalItem.getAmount();
        int price = itemPrices.getOrDefault(itemId, 10);
        int totalPrice = price * amount;
        
        // 檢查庫存
        if (available < amount) {
            player.sendMessage(plugin.getMessageUtil().colorize(
                plugin.getConfigManager().getMessage("prefix") + "§c庫存不足！可用: " + available));
            return false;
        }
        
        // 經濟檢查已在ShopListener中處理
        
        // 給予物品（保留完整NBT數據）
        ItemStack purchasedItem = originalItem.clone();
        purchasedItem.setAmount(amount);
        
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), purchasedItem);
            player.sendMessage(plugin.getMessageUtil().colorize(
                plugin.getConfigManager().getMessage("prefix") + "§e背包已滿，物品已掉落在地上！"));
        } else {
            player.getInventory().addItem(purchasedItem);
        }
        
        // 扣除庫存
        originalItem.setAmount(available - amount);
        if (originalItem.getAmount() <= 0) {
            shopItems.remove(itemId);
            itemPrices.remove(itemId);
        }
        
        // 保存數據
        saveShopData();
        
        return true;
    }
    
    // 舊的購買方法，保持兼容性
    public boolean purchaseItem(Player player, Material material, int amount) {
        // 尋找第一個匹配材料的物品
        for (Map.Entry<String, ItemStack> entry : shopItems.entrySet()) {
            if (entry.getValue().getType() == material) {
                return purchaseItem(player, entry.getValue(), amount);
            }
        }
        return false;
    }
    
    // 根據顯示物品找到對應的物品ID
    private String findItemIdByDisplayItem(ItemStack displayItem) {
        // 移除商店添加的lore信息，恢復原始物品
        ItemStack cleanItem = displayItem.clone();
        ItemMeta meta = cleanItem.getItemMeta();
        
        if (meta != null) {
            // 檢查是否為商店添加的翻譯名稱
            if (meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                String expectedTranslatedName = "§e" + getItemDisplayName(displayItem.getType());
                // 如果是商店添加的翻譯名稱，則移除它
                if (displayName.equals(expectedTranslatedName)) {
                    meta.setDisplayName(null);
                }
            }
            
            // 移除商店添加的lore
            if (meta.hasLore()) {
                List<String> lore = new ArrayList<>(meta.getLore());
                
                // 找到商店添加的lore開始位置（通常是空行後的內容）
                int shopLoreStart = -1;
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (line.isEmpty() && i + 1 < lore.size()) {
                        String nextLine = lore.get(i + 1);
                        if (nextLine.contains("庫存:") || nextLine.contains("價格:")) {
                            shopLoreStart = i;
                            break;
                        }
                    }
                }
                
                // 如果找到商店lore，則移除它們
                if (shopLoreStart >= 0) {
                    lore = lore.subList(0, shopLoreStart);
                } else {
                    // 如果沒有找到空行分隔符，則從末尾移除商店相關的lore
                    for (int i = lore.size() - 1; i >= 0; i--) {
                        String line = lore.get(i);
                        if (line.contains("左鍵購買") || line.contains("右鍵購買") || 
                            line.contains("庫存:") || line.contains("價格:")) {
                            lore.remove(i);
                        } else {
                            break;
                        }
                    }
                }
                
                if (lore.isEmpty()) {
                    meta.setLore(null);
                } else {
                    meta.setLore(lore);
                }
            }
            
            cleanItem.setItemMeta(meta);
        }
        
        // 生成清理後物品的ID並查找匹配
        String searchId = generateItemId(cleanItem);
        
        // 調試日誌 - 根據配置決定是否輸出
        if (plugin.getConfigManager().isLogShopOperationsEnabled()) {
            plugin.getLogger().info("[DEBUG] 生成的搜索ID: " + searchId);
            plugin.getLogger().info("[DEBUG] 可用的商店物品ID: " + shopItems.keySet());
        }
        
        return shopItems.containsKey(searchId) ? searchId : null;
    }
    
    public void clearShop() {
        shopItems.clear();
        itemPrices.clear();
        saveShopData();
    }
    
    public void loadShopData() {
        ConfigurationSection shopSection = plugin.getConfigManager().getShopConfig()
                .getConfigurationSection("shop-items");
        
        if (shopSection != null) {
            for (String itemId : shopSection.getKeys(false)) {
                try {
                    // 優先嘗試讀取Base64序列化的ItemStack
                    String base64Data = shopSection.getString(itemId + ".item-data");
                    ItemStack item = null;
                    
                    if (base64Data != null && !base64Data.isEmpty()) {
                        // 使用Base64反序列化
                        item = itemStackFromBase64(base64Data);
                    } else {
                        // 回退到標準Bukkit序列化（向後兼容）
                        item = shopSection.getItemStack(itemId + ".item");
                    }
                    
                    int price = shopSection.getInt(itemId + ".price", 10);
                    
                    if (item != null && item.getAmount() > 0) {
                        // 檢查是否為禁止物品
                        if (bannedItems.contains(item.getType())) {
                            plugin.getLogger().info("從配置文件中移除禁止物品: " + item.getType().name());
                            continue;
                        }
                        
                        shopItems.put(itemId, item);
                        itemPrices.put(itemId, price);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("載入物品時發生錯誤: " + itemId + " - " + e.getMessage());
                }
            }
        }
    }
    
    public void saveShopData() {
        plugin.getConfigManager().getShopConfig().set("shop-items", null);
        
        for (Map.Entry<String, ItemStack> entry : shopItems.entrySet()) {
            String itemId = entry.getKey();
            ItemStack item = entry.getValue();
            int price = itemPrices.getOrDefault(itemId, 10);
            
            String path = "shop-items." + itemId;
            
            try {
                // 使用Base64序列化保存ItemStack，確保NBT數據完整保留
                String base64Data = itemStackToBase64(item);
                plugin.getConfigManager().getShopConfig().set(path + ".item-data", base64Data);
                plugin.getConfigManager().getShopConfig().set(path + ".price", price);
                
                // 同時保存標準格式作為備份（可選）
                plugin.getConfigManager().getShopConfig().set(path + ".item", item);
            } catch (Exception e) {
                plugin.getLogger().warning("保存物品時發生錯誤: " + itemId + " - " + e.getMessage());
                // 如果Base64序列化失敗，回退到標準序列化
                plugin.getConfigManager().getShopConfig().set(path + ".item", item);
                plugin.getConfigManager().getShopConfig().set(path + ".price", price);
            }
        }
        
        plugin.getConfigManager().saveShopConfig();
    }
    
    /**
     * 將ItemStack序列化為Base64字符串，使用NBT格式完整保留所有數據
     * 使用BukkitObjectOutputStream確保完整的NBT數據保存
     */
    private String itemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeObject(item);
            dataOutput.close();
            
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("無法序列化ItemStack: " + e.getMessage(), e);
        }
    }
    
    /**
     * 從Base64字符串反序列化ItemStack，使用NBT格式完整恢復所有數據
     */
    private ItemStack itemStackFromBase64(String data) throws IOException {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            
            return item;
        } catch (ClassNotFoundException e) {
            throw new IOException("無法反序列化ItemStack: " + e.getMessage(), e);
        }
    }
    
    private String getItemDisplayName(Material material) {
        return ItemNameTranslator.getChineseName(material);
    }
    
    public int getTotalItemCount() {
        return shopItems.values().stream().mapToInt(ItemStack::getAmount).sum();
    }
    
    public void refreshShop() {
        // 刷新商店數據
        loadShopData();
    }
    
    public void removeItemFromShop(Material material, int amount) {
        // 尋找匹配材料的物品並移除
        for (Map.Entry<String, ItemStack> entry : shopItems.entrySet()) {
            ItemStack item = entry.getValue();
            if (item.getType() == material) {
                String itemId = entry.getKey();
                int currentAmount = item.getAmount();
                int newAmount = Math.max(0, currentAmount - amount);
                
                if (newAmount <= 0) {
                    shopItems.remove(itemId);
                    itemPrices.remove(itemId);
                } else {
                    item.setAmount(newAmount);
                }
                
                saveShopData();
                break;
            }
        }
    }
    
    // 新的Getter方法
    public Map<String, ItemStack> getShopItemsNew() {
        return new HashMap<>(shopItems);
    }
    
    public Map<String, Integer> getItemPricesNew() {
        return new HashMap<>(itemPrices);
    }
    
    // 舊的Getter方法，保持兼容性
    public Map<Material, Integer> getShopItems() {
        Map<Material, Integer> result = new HashMap<>();
        for (ItemStack item : shopItems.values()) {
            Material material = item.getType();
            result.put(material, result.getOrDefault(material, 0) + item.getAmount());
        }
        return result;
    }
    
    public Map<Material, Integer> getItemPrices() {
        Map<Material, Integer> result = new HashMap<>();
        for (Map.Entry<String, ItemStack> entry : shopItems.entrySet()) {
            Material material = entry.getValue().getType();
            String itemId = entry.getKey();
            if (!result.containsKey(material)) {
                result.put(material, itemPrices.getOrDefault(itemId, 10));
            }
        }
        return result;
    }
    
    public boolean hasItem(Material material) {
        return shopItems.values().stream().anyMatch(item -> 
            item.getType() == material && item.getAmount() > 0);
    }
    
    public int getItemAmount(Material material) {
        return shopItems.values().stream()
            .filter(item -> item.getType() == material)
            .mapToInt(ItemStack::getAmount)
            .sum();
    }
    
    public int getItemPrice(Material material) {
        for (Map.Entry<String, ItemStack> entry : shopItems.entrySet()) {
            if (entry.getValue().getType() == material) {
                return itemPrices.getOrDefault(entry.getKey(), 10);
            }
        }
        return 10;
    }
    
    // 別名方法，為了兼容性
    public void removeItem(Material material, int amount) {
        removeItemFromShop(material, amount);
    }
    

}