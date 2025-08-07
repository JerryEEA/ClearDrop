package com.cleardrop;

import com.cleardrop.commands.ClearDropCommand;
import com.cleardrop.commands.DropShopCommand;
import com.cleardrop.config.ConfigManager;
import com.cleardrop.economy.VaultEconomy;
import com.cleardrop.listeners.ShopListener;
import com.cleardrop.managers.ClearManager;
import com.cleardrop.managers.ShopManager;
import com.cleardrop.utils.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearDropPlugin extends JavaPlugin {
    
    private static ClearDropPlugin instance;
    private ConfigManager configManager;
    private ClearManager clearManager;
    private ShopManager shopManager;
    private MessageUtil messageUtil;
    private VaultEconomy vaultEconomy;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化配置管理器
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // 初始化消息工具
        messageUtil = new MessageUtil(this);
        
        // 初始化經濟系統
        vaultEconomy = new VaultEconomy();
        
        // 初始化管理器
        clearManager = new ClearManager(this);
        shopManager = new ShopManager(this);
        
        // 註冊指令
        registerCommands();
        
        // 註冊事件監聽器
        registerListeners();
        
        // 啟動清理計時器
        clearManager.startTimer();
        
        getLogger().info("ClearDrop 插件已啟用！");
        getLogger().info("支援版本: 1.21-1.21.8");
    }
    
    @Override
    public void onDisable() {
        if (clearManager != null) {
            clearManager.stopTimer();
        }
        
        if (shopManager != null) {
            shopManager.saveShopData();
        }
        
        getLogger().info("ClearDrop 插件已停用！");
    }
    
    private void registerCommands() {
        getCommand("cleardrop").setExecutor(new ClearDropCommand(this));
        getCommand("dropshop").setExecutor(new DropShopCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
    }
    
    public void reloadPlugin() {
        // 停止計時器
        if (clearManager != null) {
            clearManager.stopTimer();
        }
        
        // 保存商店數據
        if (shopManager != null) {
            shopManager.saveShopData();
        }
        
        // 重新加載配置
        configManager.reloadConfig();
        
        // 重新初始化管理器
        clearManager = new ClearManager(this);
        shopManager = new ShopManager(this);
        
        // 重新啟動計時器
        clearManager.startTimer();
    }
    
    // Getter 方法
    public static ClearDropPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ClearManager getClearManager() {
        return clearManager;
    }
    
    public ShopManager getShopManager() {
        return shopManager;
    }
    
    public MessageUtil getMessageUtil() {
        return messageUtil;
    }
    
    public VaultEconomy getVaultEconomy() {
        return vaultEconomy;
    }
}