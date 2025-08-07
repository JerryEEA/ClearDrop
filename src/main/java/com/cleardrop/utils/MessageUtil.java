package com.cleardrop.utils;

import com.cleardrop.ClearDropPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {
    
    private final ClearDropPlugin plugin;
    
    public MessageUtil(ClearDropPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 將顏色代碼轉換為實際顏色
     * @param message 包含顏色代碼的消息
     * @return 轉換後的消息
     */
    public String colorize(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * 發送帶前綴的消息給玩家
     * @param sender 接收者
     * @param messageKey 消息鍵值
     */
    public void sendMessage(CommandSender sender, String messageKey) {
        String prefix = plugin.getConfigManager().getMessage("prefix");
        String message = plugin.getConfigManager().getMessage(messageKey);
        sender.sendMessage(colorize(prefix + message));
    }
    
    /**
     * 發送帶前綴和參數替換的消息給玩家
     * @param sender 接收者
     * @param messageKey 消息鍵值
     * @param replacements 替換參數（鍵值對）
     */
    public void sendMessage(CommandSender sender, String messageKey, String... replacements) {
        String prefix = plugin.getConfigManager().getMessage("prefix");
        String message = plugin.getConfigManager().getMessage(messageKey);
        
        // 替換參數
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        
        sender.sendMessage(colorize(prefix + message));
    }
    
    /**
     * 發送原始消息（不帶前綴）
     * @param sender 接收者
     * @param message 消息內容
     */
    public void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
    
    /**
     * 發送成功消息
     * @param sender 接收者
     * @param message 消息內容
     */
    public void sendSuccessMessage(CommandSender sender, String message) {
        String prefix = plugin.getConfigManager().getMessage("prefix");
        sender.sendMessage(colorize(prefix + "&a" + message));
    }
    
    /**
     * 發送錯誤消息
     * @param sender 接收者
     * @param message 消息內容
     */
    public void sendErrorMessage(CommandSender sender, String message) {
        String prefix = plugin.getConfigManager().getMessage("prefix");
        sender.sendMessage(colorize(prefix + "&c" + message));
    }
    
    /**
     * 發送警告消息
     * @param sender 接收者
     * @param message 消息內容
     */
    public void sendWarningMessage(CommandSender sender, String message) {
        String prefix = plugin.getConfigManager().getMessage("prefix");
        sender.sendMessage(colorize(prefix + "&e" + message));
    }
    
    /**
     * 發送信息消息
     * @param sender 接收者
     * @param message 消息內容
     */
    public void sendInfoMessage(CommandSender sender, String message) {
        String prefix = plugin.getConfigManager().getMessage("prefix");
        sender.sendMessage(colorize(prefix + "&b" + message));
    }
    
    /**
     * 檢查權限並發送無權限消息
     * @param sender 發送者
     * @param permission 權限節點
     * @return 是否有權限
     */
    public boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sendMessage(sender, "no-permission");
            return false;
        }
        return true;
    }
    
    /**
     * 檢查是否為玩家
     * @param sender 發送者
     * @return 是否為玩家
     */
    public boolean checkPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendErrorMessage(sender, "此指令只能由玩家執行！");
            return false;
        }
        return true;
    }
    
    /**
     * 格式化時間（秒轉換為分:秒格式）
     * @param seconds 秒數
     * @return 格式化的時間字符串
     */
    public String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    
    /**
     * 格式化數字（添加千位分隔符）
     * @param number 數字
     * @return 格式化的數字字符串
     */
    public String formatNumber(int number) {
        return String.format("%,d", number);
    }
    
    /**
     * 獲取進度條
     * @param current 當前值
     * @param max 最大值
     * @param length 進度條長度
     * @return 進度條字符串
     */
    public String getProgressBar(int current, int max, int length) {
        if (max <= 0) {
            return "&c無效的最大值";
        }
        
        double percentage = (double) current / max;
        int filled = (int) (percentage * length);
        
        StringBuilder bar = new StringBuilder("&a");
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        
        bar.append("&7");
        for (int i = filled; i < length; i++) {
            bar.append("█");
        }
        
        bar.append(" &f").append(String.format("%.1f%%", percentage * 100));
        
        return bar.toString();
    }
}