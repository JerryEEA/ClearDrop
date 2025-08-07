package com.cleardrop.commands;

import com.cleardrop.ClearDropPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClearDropCommand implements CommandExecutor, TabCompleter {
    
    private final ClearDropPlugin plugin;
    
    public ClearDropCommand(ClearDropPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelpMessage(sender);
                break;
                
            case "clear":
                handleClearCommand(sender, args);
                break;
                
            case "timer":
                handleTimerCommand(sender, args);
                break;
                
            case "shop":
                handleShopCommand(sender, args);
                break;
                
            case "world":
                handleWorldCommand(sender, args);
                break;
                
            case "reload":
                handleReloadCommand(sender);
                break;
                
            case "status":
                handleStatusCommand(sender);
                break;
                
            case "info":
                handleInfoCommand(sender);
                break;
                
            default:
                plugin.getMessageUtil().sendErrorMessage(sender, "未知的子指令: " + subCommand);
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        plugin.getMessageUtil().sendRawMessage(sender, "&6========== ClearDrop 幫助 ==========");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop help &7- 顯示幫助信息");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop clear &7- 立即清理掉落物");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop timer <start|stop|toggle|set> [時間] &7- 計時器管理");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop shop <clear> &7- 商店管理");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop world <add|remove|list> [世界名] &7- 世界管理");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop reload &7- 重載插件");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop status &7- 查看插件狀態");
        plugin.getMessageUtil().sendRawMessage(sender, "&e/cleardrop info &7- 查看插件信息");
        plugin.getMessageUtil().sendRawMessage(sender, "&6====================================");
    }
    
    private void handleClearCommand(CommandSender sender, String[] args) {
        if (!plugin.getMessageUtil().checkPermission(sender, "cleardrop.clear")) {
            return;
        }
        
        int cleared = plugin.getClearManager().forceClear();
        plugin.getMessageUtil().sendSuccessMessage(sender, 
            "已強制清理 " + cleared + " 個掉落物！");
    }
    
    private void handleTimerCommand(CommandSender sender, String[] args) {
        if (!plugin.getMessageUtil().checkPermission(sender, "cleardrop.admin")) {
            return;
        }
        
        if (args.length < 2) {
            plugin.getMessageUtil().sendErrorMessage(sender, "用法: /cleardrop timer <start|stop|toggle|set> [時間]");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "start":
                plugin.getClearManager().startTimer();
                plugin.getMessageUtil().sendSuccessMessage(sender, "清理計時器已啟動！");
                break;
                
            case "stop":
                plugin.getClearManager().stopTimer();
                plugin.getMessageUtil().sendSuccessMessage(sender, "清理計時器已停止！");
                break;
                
            case "toggle":
                plugin.getClearManager().toggleTimer();
                boolean isRunning = plugin.getClearManager().isRunning();
                plugin.getMessageUtil().sendSuccessMessage(sender, 
                    "清理計時器已" + (isRunning ? "啟動" : "停止") + "！");
                break;
                
            case "set":
                if (args.length < 3) {
                    plugin.getMessageUtil().sendErrorMessage(sender, "用法: /cleardrop timer set <時間(秒)>");
                    return;
                }
                
                try {
                    int time = Integer.parseInt(args[2]);
                    if (time < 30) {
                        plugin.getMessageUtil().sendErrorMessage(sender, "時間不能少於30秒！");
                        return;
                    }
                    
                    plugin.getConfigManager().setClearInterval(time);
                    plugin.getClearManager().resetTimer();
                    plugin.getMessageUtil().sendSuccessMessage(sender, 
                        "清理間隔已設置為 " + time + " 秒！");
                } catch (NumberFormatException e) {
                    plugin.getMessageUtil().sendErrorMessage(sender, "無效的時間格式！");
                }
                break;
                
            default:
                plugin.getMessageUtil().sendErrorMessage(sender, "未知的計時器操作: " + action);
                break;
        }
    }
    
    private void handleShopCommand(CommandSender sender, String[] args) {
        if (!plugin.getMessageUtil().checkPermission(sender, "cleardrop.admin")) {
            return;
        }
        
        if (args.length < 2) {
            plugin.getMessageUtil().sendErrorMessage(sender, "用法: /cleardrop shop <clear>");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        if (action.equals("clear")) {
            plugin.getShopManager().clearShop();
            plugin.getMessageUtil().sendSuccessMessage(sender, "商店已清空！");
        } else {
            plugin.getMessageUtil().sendErrorMessage(sender, "未知的商店操作: " + action);
        }
    }
    
    private void handleWorldCommand(CommandSender sender, String[] args) {
        if (!plugin.getMessageUtil().checkPermission(sender, "cleardrop.admin")) {
            return;
        }
        
        if (args.length < 2) {
            plugin.getMessageUtil().sendErrorMessage(sender, "用法: /cleardrop world <add|remove|list> [世界名]");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "add":
                if (args.length < 3) {
                    plugin.getMessageUtil().sendErrorMessage(sender, "用法: /cleardrop world add <世界名>");
                    return;
                }
                
                String addWorld = args[2];
                World world = Bukkit.getWorld(addWorld);
                if (world == null) {
                    plugin.getMessageUtil().sendErrorMessage(sender, "世界 '" + addWorld + "' 不存在！");
                    return;
                }
                
                plugin.getConfigManager().addClearWorld(addWorld);
                plugin.getMessageUtil().sendSuccessMessage(sender, 
                    "已將世界 '" + addWorld + "' 添加到清理列表！");
                break;
                
            case "remove":
                if (args.length < 3) {
                    plugin.getMessageUtil().sendErrorMessage(sender, "用法: /cleardrop world remove <世界名>");
                    return;
                }
                
                String removeWorld = args[2];
                plugin.getConfigManager().removeClearWorld(removeWorld);
                plugin.getMessageUtil().sendSuccessMessage(sender, 
                    "已將世界 '" + removeWorld + "' 從清理列表移除！");
                break;
                
            case "list":
                List<String> worlds = plugin.getConfigManager().getClearWorlds();
                plugin.getMessageUtil().sendInfoMessage(sender, "清理世界列表:");
                for (String worldName : worlds) {
                    plugin.getMessageUtil().sendRawMessage(sender, "&7- &e" + worldName);
                }
                break;
                
            default:
                plugin.getMessageUtil().sendErrorMessage(sender, "未知的世界操作: " + action);
                break;
        }
    }
    
    private void handleReloadCommand(CommandSender sender) {
        if (!plugin.getMessageUtil().checkPermission(sender, "cleardrop.reload")) {
            return;
        }
        
        plugin.reloadPlugin();
        plugin.getMessageUtil().sendMessage(sender, "reload-success");
    }
    
    private void handleStatusCommand(CommandSender sender) {
        if (!plugin.getMessageUtil().checkPermission(sender, "cleardrop.admin")) {
            return;
        }
        
        plugin.getMessageUtil().sendRawMessage(sender, "&6========== ClearDrop 狀態 ==========");
        plugin.getMessageUtil().sendRawMessage(sender, "&e計時器狀態: " + 
            (plugin.getClearManager().isRunning() ? "&a運行中" : "&c已停止"));
        plugin.getMessageUtil().sendRawMessage(sender, "&e剩餘時間: &a" + 
            plugin.getClearManager().getFormattedTime());
        plugin.getMessageUtil().sendRawMessage(sender, "&e清理間隔: &a" + 
            plugin.getConfigManager().getClearInterval() + " 秒");
        plugin.getMessageUtil().sendRawMessage(sender, "&e當前掉落物數量: &a" + 
            plugin.getClearManager().getItemCount());
        plugin.getMessageUtil().sendRawMessage(sender, "&e商店物品種類: &a" + 
            plugin.getShopManager().getShopItems().size());
        plugin.getMessageUtil().sendRawMessage(sender, "&e清理世界數量: &a" + 
            plugin.getConfigManager().getClearWorlds().size());
        plugin.getMessageUtil().sendRawMessage(sender, "&6====================================");
    }
    
    private void handleInfoCommand(CommandSender sender) {
        plugin.getMessageUtil().sendRawMessage(sender, "&6========== ClearDrop 信息 ==========");
        plugin.getMessageUtil().sendRawMessage(sender, "&e插件名稱: &aClearDrop");
        plugin.getMessageUtil().sendRawMessage(sender, "&e版本: &a1.0.0");
        plugin.getMessageUtil().sendRawMessage(sender, "&e支援版本: &a1.21-1.21.8");
        plugin.getMessageUtil().sendRawMessage(sender, "&e作者: &aJerryEEA");
        plugin.getMessageUtil().sendRawMessage(sender, "&e描述: &7掉落物清理插件 - 自動清理掉落物並提供GUI商店功能");
        plugin.getMessageUtil().sendRawMessage(sender, "&6====================================");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "clear", "timer", "shop", "world", "reload", "status", "info");
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "timer":
                    List<String> timerActions = Arrays.asList("start", "stop", "toggle", "set");
                    for (String action : timerActions) {
                        if (action.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(action);
                        }
                    }
                    break;
                    
                case "shop":
                    List<String> shopActions = Arrays.asList("clear");
                    for (String action : shopActions) {
                        if (action.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(action);
                        }
                    }
                    break;
                    
                case "world":
                    List<String> worldActions = Arrays.asList("add", "remove", "list");
                    for (String action : worldActions) {
                        if (action.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(action);
                        }
                    }
                    break;
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            String action = args[1].toLowerCase();
            
            if (subCommand.equals("world") && (action.equals("add") || action.equals("remove"))) {
                for (World world : Bukkit.getWorlds()) {
                    String worldName = world.getName();
                    if (worldName.toLowerCase().startsWith(args[2].toLowerCase())) {
                        completions.add(worldName);
                    }
                }
            }
        }
        
        return completions;
    }
}