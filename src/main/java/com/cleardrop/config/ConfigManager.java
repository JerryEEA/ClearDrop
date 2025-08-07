package com.cleardrop.config;

import com.cleardrop.ClearDropPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    
    private final ClearDropPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration shopConfig;
    private File configFile;
    private File shopConfigFile;
    
    public ConfigManager(ClearDropPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.shopConfigFile = new File(plugin.getDataFolder(), "shop.yml");
    }
    
    public void loadConfig() {
        // 創建插件數據文件夾
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // 加載主配置文件
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // 加載商店配置文件
        if (!shopConfigFile.exists()) {
            createDefaultShopConfig();
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopConfigFile);
        
        // 設置默認值
        setDefaults();
    }
    
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        shopConfig = YamlConfiguration.loadConfiguration(shopConfigFile);
    }
    
    private void setDefaults() {
        // 清理設定
        config.addDefault("clear.enabled", true);
        config.addDefault("clear.interval", 300); // 5分鐘
        config.addDefault("clear.warning-time", 30); // 30秒前警告
        config.addDefault("clear.worlds", Arrays.asList("world"));
        
        // 商店設定
        config.addDefault("shop.name", "Drop-Shop");
        config.addDefault("shop.enabled", true);
        config.addDefault("shop.auto-clear-enabled", true);
        
        // 價格範圍設定
        config.addDefault("shop.price-ranges.common.min", 1);
        config.addDefault("shop.price-ranges.common.max", 10);
        config.addDefault("shop.price-ranges.uncommon.min", 11);
        config.addDefault("shop.price-ranges.uncommon.max", 30);
        config.addDefault("shop.price-ranges.rare.min", 31);
        config.addDefault("shop.price-ranges.rare.max", 80);
        config.addDefault("shop.price-ranges.epic.min", 81);
        config.addDefault("shop.price-ranges.epic.max", 150);
        config.addDefault("shop.price-ranges.legendary.min", 151);
        config.addDefault("shop.price-ranges.legendary.max", 300);
        
        // 消息設定
        config.addDefault("messages.prefix", "&6[ClearDrop] &r");
        config.addDefault("messages.clear.warning", "&e警告: 掉落物將在 &c{time} &e秒後被清理！");
        config.addDefault("messages.clear-complete", "&a已清理 &e{count} &a個掉落物！");
        config.addDefault("messages.clear.cleared", "&a已清理 &e{count} &a個掉落物！");
        config.addDefault("messages.no-permission", "&c你沒有權限執行此指令！");
        config.addDefault("messages.reload-success", "&a插件重載成功！");
        config.addDefault("messages.shop-opened", "&a已打開掉落物商店！");
        config.addDefault("messages.shop.opened", "&a已打開掉落物商店！");
        config.addDefault("messages.shop.insufficient-funds", "&c您的金錢不足！需要 &e{price} &c金幣購買 &e{amount} &c個物品。當前餘額: &e{balance}");
        config.addDefault("messages.shop.insufficient-stock", "&c商店中沒有足夠的物品！");
        config.addDefault("messages.shop.withdraw-failed", "&c扣除金錢失敗！");
        config.addDefault("messages.shop.purchase-success", "&a成功購買 &e{amount} &a個 &e{item} &a，花費 &e{price} &a金幣！");
        
        // 添加不帶點號的別名，確保兼容性
        config.addDefault("messages.shop-insufficient-funds", "&c您的金錢不足！需要 &e{price} &c金幣購買 &e{amount} &c個物品。當前餘額: &e{balance}");
        config.addDefault("messages.shop-insufficient-stock", "&c商店中沒有足夠的物品！");
        config.addDefault("messages.shop-withdraw-failed", "&c扣除金錢失敗！");
        config.addDefault("messages.shop-purchase-success", "&a成功購買 &e{amount} &a個 &e{item} &a，花費 &e{price} &a金幣！");
        
        config.options().copyDefaults(true);
        saveConfig();
    }
    
    private void createDefaultShopConfig() {
        shopConfig = new YamlConfiguration();
        shopConfig.set("shop-items", new HashMap<String, Object>());
        saveShopConfig();
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("無法保存配置文件: " + e.getMessage());
        }
    }
    
    public void saveShopConfig() {
        try {
            shopConfig.save(shopConfigFile);
        } catch (IOException e) {
            plugin.getLogger().severe("無法保存商店配置文件: " + e.getMessage());
        }
    }
    
    // Getter 方法
    public boolean isClearEnabled() {
        return config.getBoolean("clear.enabled", true);
    }
    
    public int getClearInterval() {
        return config.getInt("clear.interval", 300);
    }
    
    public int getWarningTime() {
        return config.getInt("clear.warning-time", 30);
    }
    
    public List<String> getClearWorlds() {
        return config.getStringList("clear.worlds");
    }
    
    public String getShopName() {
        return config.getString("shop.name", "Drop-Shop");
    }
    
    public boolean isShopEnabled() {
        return config.getBoolean("shop.enabled", true);
    }
    
    public boolean isAutoClearEnabled() {
        return config.getBoolean("shop.auto-clear-enabled", true);
    }
    
    public String getMessage(String key) {
        return config.getString("messages." + key, "&c消息未找到: " + key);
    }
    
    public int getItemPrice(Material material) {
        String rarity = getItemRarity(material);
        int min = config.getInt("shop.price-ranges." + rarity + ".min", 10);
        int max = config.getInt("shop.price-ranges." + rarity + ".max", 50);
        return new Random().nextInt(max - min + 1) + min;
    }
    
    // 價格範圍 getter 方法
    public int getCommonPriceMin() {
        return config.getInt("shop.price-ranges.common.min", 10);
    }
    
    public int getCommonPriceMax() {
        return config.getInt("shop.price-ranges.common.max", 50);
    }
    
    public int getUncommonPriceMin() {
        return config.getInt("shop.price-ranges.uncommon.min", 50);
    }
    
    public int getUncommonPriceMax() {
        return config.getInt("shop.price-ranges.uncommon.max", 150);
    }
    
    public int getRarePriceMin() {
        return config.getInt("shop.price-ranges.rare.min", 150);
    }
    
    public int getRarePriceMax() {
        return config.getInt("shop.price-ranges.rare.max", 400);
    }
    
    public int getEpicPriceMin() {
        return config.getInt("shop.price-ranges.epic.min", 400);
    }
    
    public int getEpicPriceMax() {
        return config.getInt("shop.price-ranges.epic.max", 800);
    }
    
    public int getLegendaryPriceMin() {
        return config.getInt("shop.price-ranges.legendary.min", 800);
    }
    
    public int getLegendaryPriceMax() {
        return config.getInt("shop.price-ranges.legendary.max", 1000);
    }
    
    private String getItemRarity(Material material) {
        // 根據物品類型判斷稀有度
        if (isLegendaryItem(material)) return "legendary";
        if (isEpicItem(material)) return "epic";
        if (isRareItem(material)) return "rare";
        if (isUncommonItem(material)) return "uncommon";
        return "common";
    }
    
    private boolean isLegendaryItem(Material material) {
        return material == Material.NETHERITE_INGOT || 
               material == Material.NETHERITE_SCRAP ||
               material == Material.DRAGON_EGG ||
               material == Material.ELYTRA ||
               material == Material.NETHER_STAR;
    }
    
    private boolean isEpicItem(Material material) {
        return material == Material.DIAMOND || 
               material == Material.EMERALD ||
               material == Material.ANCIENT_DEBRIS ||
               material.name().contains("DIAMOND_") ||
               material.name().contains("NETHERITE_");
    }
    
    private boolean isRareItem(Material material) {
        return material == Material.GOLD_INGOT ||
               material == Material.IRON_INGOT ||
               material.name().contains("GOLD_") ||
               material.name().contains("IRON_") ||
               material == Material.ENDER_PEARL;
    }
    
    private boolean isUncommonItem(Material material) {
        return material == Material.REDSTONE ||
               material == Material.LAPIS_LAZULI ||
               material == Material.COAL ||
               material.name().contains("_ORE");
    }
    
    // Setter 方法
    public void setClearEnabled(boolean enabled) {
        config.set("clear.enabled", enabled);
        saveConfig();
    }
    
    public void setClearInterval(int interval) {
        config.set("clear.interval", interval);
        saveConfig();
    }
    
    public void addClearWorld(String world) {
        List<String> worlds = getClearWorlds();
        if (!worlds.contains(world)) {
            worlds.add(world);
            config.set("clear.worlds", worlds);
            saveConfig();
        }
    }
    
    public void removeClearWorld(String world) {
        List<String> worlds = getClearWorlds();
        if (worlds.contains(world)) {
            worlds.remove(world);
            config.set("clear.worlds", worlds);
            saveConfig();
        }
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public FileConfiguration getShopConfig() {
        return shopConfig;
    }
    
    // 調試設置 getter 方法
    public boolean isDebugEnabled() {
        return config.getBoolean("debug.enabled", false);
    }
    
    public boolean isLogClearDetailsEnabled() {
        return config.getBoolean("debug.log-clear-details", false);
    }
    
    public boolean isLogShopOperationsEnabled() {
        return config.getBoolean("debug.log-shop-operations", false);
    }
}