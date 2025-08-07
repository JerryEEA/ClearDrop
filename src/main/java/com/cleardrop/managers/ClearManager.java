package com.cleardrop.managers;

import com.cleardrop.ClearDropPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClearManager {
    
    private final ClearDropPlugin plugin;
    private BukkitTask clearTask;
    private BukkitTask warningTask;
    private int currentTime;
    private boolean isRunning;
    
    public ClearManager(ClearDropPlugin plugin) {
        this.plugin = plugin;
        this.isRunning = false;
    }
    
    public void startTimer() {
        if (!plugin.getConfigManager().isClearEnabled()) {
            return;
        }
        
        if (isRunning) {
            stopTimer();
        }
        
        currentTime = plugin.getConfigManager().getClearInterval();
        isRunning = true;
        
        // 主計時器
        clearTask = new BukkitRunnable() {
            @Override
            public void run() {
                currentTime--;
                
                // 警告時間檢查
                int warningTime = plugin.getConfigManager().getWarningTime();
                if (currentTime == warningTime) {
                    sendWarningMessage(warningTime);
                }
                
                // 清理時間到達
                if (currentTime <= 0) {
                    clearDroppedItems();
                    currentTime = plugin.getConfigManager().getClearInterval();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // 每秒執行一次
        
        plugin.getLogger().info("掉落物清理計時器已啟動，間隔: " + plugin.getConfigManager().getClearInterval() + " 秒");
    }
    
    public void stopTimer() {
        if (clearTask != null && !clearTask.isCancelled()) {
            clearTask.cancel();
        }
        
        if (warningTask != null && !warningTask.isCancelled()) {
            warningTask.cancel();
        }
        
        isRunning = false;
        plugin.getLogger().info("掉落物清理計時器已停止");
    }
    
    public void clearDroppedItems() {
        List<String> worlds = plugin.getConfigManager().getClearWorlds();
        int totalCleared = 0;
        List<Item> clearedItems = new ArrayList<>();
        
        for (String worldName : worlds) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("世界 '" + worldName + "' 不存在，跳過清理");
                continue;
            }
            
            Collection<Item> itemCollection = world.getEntitiesByClass(Item.class);
            List<Item> items = new ArrayList<>(itemCollection);
            for (Item item : items) {
                // 檢查物品是否可以被清理（排除玩家剛丟棄的物品）
                if (canClearItem(item)) {
                    clearedItems.add(item);
                    totalCleared++;
                }
            }
        }
        
        // 將清理的物品添加到商店
        if (plugin.getConfigManager().isShopEnabled()) {
            plugin.getShopManager().addItemsToShop(clearedItems);
        }
        
        // 移除物品
        for (Item item : clearedItems) {
            item.remove();
        }
        
        // 發送清理完成消息
        String message = plugin.getConfigManager().getMessage("clear.cleared")
                .replace("{count}", String.valueOf(totalCleared));
        broadcastMessage(message);
        
        plugin.getLogger().info("已清理 " + totalCleared + " 個掉落物");
    }
    
    public int forceClear() {
        List<String> worlds = plugin.getConfigManager().getClearWorlds();
        int totalCleared = 0;
        List<Item> clearedItems = new ArrayList<>();
        
        for (String worldName : worlds) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }
            
            Collection<Item> itemCollection = world.getEntitiesByClass(Item.class);
            List<Item> items = new ArrayList<>(itemCollection);
            for (Item item : items) {
                clearedItems.add(item);
                totalCleared++;
            }
        }
        
        // 將清理的物品添加到商店
        if (plugin.getConfigManager().isShopEnabled()) {
            plugin.getShopManager().addItemsToShop(clearedItems);
        }
        
        // 移除物品
        for (Item item : clearedItems) {
            item.remove();
        }
        
        return totalCleared;
    }
    
    private boolean canClearItem(Item item) {
        // 檢查物品是否存在超過5秒（避免清理剛丟棄的物品）
        return item.getTicksLived() > 100; // 5秒 = 100 ticks
    }
    
    private void sendWarningMessage(int seconds) {
        String message = plugin.getConfigManager().getMessage("clear.warning")
                .replace("{time}", String.valueOf(seconds));
        broadcastMessage(message);
    }
    
    private void broadcastMessage(String message) {
        String prefix = plugin.getConfigManager().getMessage("prefix");
        String fullMessage = plugin.getMessageUtil().colorize(prefix + message);
        
        Bukkit.getOnlinePlayers().forEach(player -> {
            // 只向在清理世界中的玩家發送消息
            if (plugin.getConfigManager().getClearWorlds().contains(player.getWorld().getName())) {
                player.sendMessage(fullMessage);
            }
        });
    }
    
    public void resetTimer() {
        currentTime = plugin.getConfigManager().getClearInterval();
    }
    
    public void toggleTimer() {
        if (isRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }
    
    // Getter 方法
    public boolean isRunning() {
        return isRunning;
    }
    
    public int getCurrentTime() {
        return currentTime;
    }
    
    public String getFormattedTime() {
        int minutes = currentTime / 60;
        int seconds = currentTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public int getItemCount() {
        List<String> worlds = plugin.getConfigManager().getClearWorlds();
        int count = 0;
        
        for (String worldName : worlds) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                count += world.getEntitiesByClass(Item.class).size();
            }
        }
        
        return count;
    }
}