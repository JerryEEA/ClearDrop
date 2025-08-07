package com.cleardrop.commands;

import com.cleardrop.ClearDropPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DropShopCommand implements CommandExecutor, TabCompleter {
    
    private final ClearDropPlugin plugin;
    
    public DropShopCommand(ClearDropPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 檢查是否為玩家
        if (!plugin.getMessageUtil().checkPlayer(sender)) {
            return true;
        }
        
        // 檢查權限
        if (!plugin.getMessageUtil().checkPermission(sender, "cleardrop.shop")) {
            return true;
        }
        
        Player player = (Player) sender;
        
        // 如果有參數，處理子指令
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "help":
                    sendHelpMessage(player);
                    break;
                    
                case "info":
                    sendShopInfo(player);
                    break;
                    
                case "refresh":
                    plugin.getShopManager().refreshShop();
                    plugin.getMessageUtil().sendSuccessMessage(player, "商店已刷新！");
                    break;
                    
                default:
                    plugin.getMessageUtil().sendErrorMessage(player, "未知的子指令: " + subCommand);
                    sendHelpMessage(player);
                    break;
            }
        } else {
            // 直接打開商店
            openShop(player);
        }
        
        return true;
    }
    
    private void openShop(Player player) {
        try {
            plugin.getShopManager().openShop(player);
            plugin.getMessageUtil().sendInfoMessage(player, "已打開掉落物商店！");
        } catch (Exception e) {
            plugin.getMessageUtil().sendErrorMessage(player, "打開商店時發生錯誤！");
            plugin.getLogger().warning("Error opening shop for player " + player.getName() + ": " + e.getMessage());
        }
    }
    
    private void sendHelpMessage(Player player) {
        plugin.getMessageUtil().sendRawMessage(player, "&6========== 掉落物商店幫助 ==========");
        plugin.getMessageUtil().sendRawMessage(player, "&e/dropshop &7- 打開掉落物商店");
        plugin.getMessageUtil().sendRawMessage(player, "&e/dropshop help &7- 顯示幫助信息");
        plugin.getMessageUtil().sendRawMessage(player, "&e/dropshop info &7- 查看商店信息");
        plugin.getMessageUtil().sendRawMessage(player, "&e/dropshop refresh &7- 刷新商店");
        plugin.getMessageUtil().sendRawMessage(player, "&6====================================");
        plugin.getMessageUtil().sendRawMessage(player, "&7提示: 點擊物品可以購買，價格根據稀有度自動計算");
    }
    
    private void sendShopInfo(Player player) {
        int itemTypes = plugin.getShopManager().getShopItems().size();
        int totalItems = plugin.getShopManager().getTotalItemCount();
        String shopName = plugin.getConfigManager().getShopName();
        
        plugin.getMessageUtil().sendRawMessage(player, "&6========== 商店信息 ==========");
        plugin.getMessageUtil().sendRawMessage(player, "&e商店名稱: &a" + shopName);
        plugin.getMessageUtil().sendRawMessage(player, "&e物品種類: &a" + itemTypes + " 種");
        plugin.getMessageUtil().sendRawMessage(player, "&e物品總數: &a" + totalItems + " 個");
        plugin.getMessageUtil().sendRawMessage(player, "&e價格範圍:");
        plugin.getMessageUtil().sendRawMessage(player, "&7  - 普通: &f" + 
            plugin.getConfigManager().getCommonPriceMin() + "-" + 
            plugin.getConfigManager().getCommonPriceMax() + " 金幣");
        plugin.getMessageUtil().sendRawMessage(player, "&7  - 稀有: &a" + 
            plugin.getConfigManager().getUncommonPriceMin() + "-" + 
            plugin.getConfigManager().getUncommonPriceMax() + " 金幣");
        plugin.getMessageUtil().sendRawMessage(player, "&7  - 史詩: &b" + 
            plugin.getConfigManager().getRarePriceMin() + "-" + 
            plugin.getConfigManager().getRarePriceMax() + " 金幣");
        plugin.getMessageUtil().sendRawMessage(player, "&7  - 傳說: &d" + 
            plugin.getConfigManager().getEpicPriceMin() + "-" + 
            plugin.getConfigManager().getEpicPriceMax() + " 金幣");
        plugin.getMessageUtil().sendRawMessage(player, "&7  - 神話: &6" + 
            plugin.getConfigManager().getLegendaryPriceMin() + "-" + 
            plugin.getConfigManager().getLegendaryPriceMax() + " 金幣");
        plugin.getMessageUtil().sendRawMessage(player, "&6====================================");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = List.of("help", "info", "refresh");
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        }
        
        return completions;
    }
}