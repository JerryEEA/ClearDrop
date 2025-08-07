package com.cleardrop.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultEconomy {
    
    private Object economy = null;
    private boolean vaultEnabled = false;
    
    public VaultEconomy() {
        setupEconomy();
    }
    
    private void setupEconomy() {
        try {
            // 檢查 Vault
            if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
                // 使用反射來避免編譯時依賴
                Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
                Object rsp = Bukkit.getServer().getServicesManager().getRegistration(economyClass);
                if (rsp != null) {
                    economy = rsp.getClass().getMethod("getProvider").invoke(rsp);
                    if (economy != null) {
                        vaultEnabled = true;
                        return;
                    }
                }
            }
            
            // 檢查 Essentials
            if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
                vaultEnabled = true;
                economy = "essentials";
                return;
            }
            
            // 檢查 CMI
            if (Bukkit.getServer().getPluginManager().getPlugin("CMI") != null) {
                vaultEnabled = true;
                economy = "cmi";
                return;
            }
            
            vaultEnabled = false;
        } catch (Exception e) {
            vaultEnabled = false;
        }
    }
    
    public boolean isEnabled() {
        return vaultEnabled;
    }
    
    public boolean hasEnoughMoney(Player player, double amount) {
        if (!vaultEnabled || economy == null) {
            return false; // 如果沒有經濟系統，不允許購買
        }
        
        try {
            if (economy instanceof String) {
                String economyType = (String) economy;
                if ("essentials".equals(economyType)) {
                    // Essentials 支援
                    Object essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                    Object user = essentials.getClass().getMethod("getUser", Player.class).invoke(essentials, player);
                    Object balance = user.getClass().getMethod("getMoney").invoke(user);
                    return ((Number) balance).doubleValue() >= amount;
                } else if ("cmi".equals(economyType)) {
                    // CMI 支援
                    Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
                    Object cmi = cmiClass.getMethod("getInstance").invoke(null);
                    Object playerManager = cmi.getClass().getMethod("getPlayerManager").invoke(cmi);
                    Object cmiUser = playerManager.getClass().getMethod("getUser", Player.class).invoke(playerManager, player);
                    Object balance = cmiUser.getClass().getMethod("getBalance").invoke(cmiUser);
                    return ((Number) balance).doubleValue() >= amount;
                }
            } else {
                // Vault 支援 - 使用安全的反射調用
                try {
                    return (Boolean) economy.getClass().getMethod("has", Player.class, double.class).invoke(economy, player, amount);
                } catch (NoSuchMethodException e) {
                    // 嘗試其他可能的方法簽名
                    try {
                        return (Boolean) economy.getClass().getMethod("has", String.class, double.class).invoke(economy, player.getName(), amount);
                    } catch (NoSuchMethodException e2) {
                        // 如果都失敗，回退到 getBalance 比較
                        double balance = getBalance(player);
                        return balance >= amount;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 發生錯誤時，嘗試回退到基本檢查
            try {
                double balance = getBalance(player);
                return balance >= amount;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }
    
    public boolean withdrawMoney(Player player, double amount) {
        if (!vaultEnabled || economy == null) {
            return false; // 如果沒有經濟系統，扣款失敗
        }
        
        try {
            if (economy instanceof String) {
                String economyType = (String) economy;
                if ("essentials".equals(economyType)) {
                    // Essentials 支援
                    Object essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                    Object user = essentials.getClass().getMethod("getUser", Player.class).invoke(essentials, player);
                    Object currentBalance = user.getClass().getMethod("getMoney").invoke(user);
                    double newBalance = ((Number) currentBalance).doubleValue() - amount;
                    if (newBalance >= 0) {
                        Class<?> bigDecimalClass = Class.forName("java.math.BigDecimal");
                        Object newBalanceBD = bigDecimalClass.getConstructor(double.class).newInstance(newBalance);
                        user.getClass().getMethod("setMoney", bigDecimalClass).invoke(user, newBalanceBD);
                        return true;
                    }
                    return false;
                } else if ("cmi".equals(economyType)) {
                    // CMI 支援
                    Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
                    Object cmi = cmiClass.getMethod("getInstance").invoke(null);
                    Object playerManager = cmi.getClass().getMethod("getPlayerManager").invoke(cmi);
                    Object cmiUser = playerManager.getClass().getMethod("getUser", Player.class).invoke(playerManager, player);
                    Object currentBalance = cmiUser.getClass().getMethod("getBalance").invoke(cmiUser);
                    double newBalance = ((Number) currentBalance).doubleValue() - amount;
                    if (newBalance >= 0) {
                        cmiUser.getClass().getMethod("setBalance", double.class).invoke(cmiUser, newBalance);
                        return true;
                    }
                    return false;
                }
            } else {
                // Vault 支援 - 使用安全的反射調用
                try {
                    Object response = economy.getClass().getMethod("withdrawPlayer", Player.class, double.class).invoke(economy, player, amount);
                    return (Boolean) response.getClass().getMethod("transactionSuccess").invoke(response);
                } catch (NoSuchMethodException e) {
                    // 嘗試其他可能的方法簽名
                    try {
                        Object response = economy.getClass().getMethod("withdrawPlayer", String.class, double.class).invoke(economy, player.getName(), amount);
                        return (Boolean) response.getClass().getMethod("transactionSuccess").invoke(response);
                    } catch (NoSuchMethodException e2) {
                        // 如果都失敗，嘗試使用 EconomyResponse 類型
                        try {
                            Class<?> economyResponseClass = Class.forName("net.milkbowl.vault.economy.EconomyResponse");
                            Object response = economy.getClass().getMethod("withdrawPlayer", String.class, String.class, double.class).invoke(economy, player.getName(), player.getWorld().getName(), amount);
                            return (Boolean) response.getClass().getMethod("transactionSuccess").invoke(response);
                        } catch (Exception e3) {
                            // 最後的回退：無法扣款，返回失敗
                            Bukkit.getLogger().severe("[ClearDrop] 錯誤：無法通過 Vault 扣款，所有反射方法都失敗。請檢查經濟插件兼容性。");
                            return false; // 扣款失敗，不允許購買
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double getBalance(Player player) {
        if (!vaultEnabled || economy == null) {
            return 0.0;
        }
        
        try {
            if (economy instanceof String) {
                String economyType = (String) economy;
                if ("essentials".equals(economyType)) {
                    // Essentials 支援
                    Object essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                    Object user = essentials.getClass().getMethod("getUser", Player.class).invoke(essentials, player);
                    Object balance = user.getClass().getMethod("getMoney").invoke(user);
                    return ((Number) balance).doubleValue();
                } else if ("cmi".equals(economyType)) {
                    // CMI 支援
                    Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
                    Object cmi = cmiClass.getMethod("getInstance").invoke(null);
                    Object playerManager = cmi.getClass().getMethod("getPlayerManager").invoke(cmi);
                    Object cmiUser = playerManager.getClass().getMethod("getUser", Player.class).invoke(playerManager, player);
                    Object balance = cmiUser.getClass().getMethod("getBalance").invoke(cmiUser);
                    return ((Number) balance).doubleValue();
                }
            } else {
                // Vault 支援 - 使用安全的反射調用
                try {
                    return (Double) economy.getClass().getMethod("getBalance", Player.class).invoke(economy, player);
                } catch (NoSuchMethodException e) {
                    // 嘗試其他可能的方法簽名
                    try {
                        return (Double) economy.getClass().getMethod("getBalance", String.class).invoke(economy, player.getName());
                    } catch (NoSuchMethodException e2) {
                        // 如果都失敗，返回 0
                        e2.printStackTrace();
                        return 0.0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public boolean depositPlayer(Player player, double amount) {
        if (!vaultEnabled || economy == null) {
            return false;
        }
        
        try {
            if (economy instanceof String) {
                String economyType = (String) economy;
                if ("essentials".equals(economyType)) {
                    // Essentials 支援
                    Object essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                    Object user = essentials.getClass().getMethod("getUser", Player.class).invoke(essentials, player);
                    Object currentBalance = user.getClass().getMethod("getMoney").invoke(user);
                    double newBalance = ((Number) currentBalance).doubleValue() + amount;
                    Class<?> bigDecimalClass = Class.forName("java.math.BigDecimal");
                    Object newBalanceBD = bigDecimalClass.getConstructor(double.class).newInstance(newBalance);
                    user.getClass().getMethod("setMoney", bigDecimalClass).invoke(user, newBalanceBD);
                    return true;
                } else if ("cmi".equals(economyType)) {
                    // CMI 支援
                    Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
                    Object cmi = cmiClass.getMethod("getInstance").invoke(null);
                    Object playerManager = cmi.getClass().getMethod("getPlayerManager").invoke(cmi);
                    Object cmiUser = playerManager.getClass().getMethod("getUser", Player.class).invoke(playerManager, player);
                    Object currentBalance = cmiUser.getClass().getMethod("getBalance").invoke(cmiUser);
                    double newBalance = ((Number) currentBalance).doubleValue() + amount;
                    cmiUser.getClass().getMethod("setBalance", double.class).invoke(cmiUser, newBalance);
                    return true;
                }
            } else {
                // Vault 支援
                try {
                    Object response = economy.getClass().getMethod("depositPlayer", Player.class, double.class).invoke(economy, player, amount);
                    return (Boolean) response.getClass().getMethod("transactionSuccess").invoke(response);
                } catch (NoSuchMethodException e) {
                    try {
                        Object response = economy.getClass().getMethod("depositPlayer", String.class, double.class).invoke(economy, player.getName(), amount);
                        return (Boolean) response.getClass().getMethod("transactionSuccess").invoke(response);
                    } catch (NoSuchMethodException e2) {
                        try {
                            Object response = economy.getClass().getMethod("depositPlayer", String.class, String.class, double.class).invoke(economy, player.getName(), player.getWorld().getName(), amount);
                            return (Boolean) response.getClass().getMethod("transactionSuccess").invoke(response);
                        } catch (Exception e3) {
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public String format(double amount) {
        if (!vaultEnabled || economy == null) {
            return String.valueOf(amount);
        }
        
        try {
            return (String) economy.getClass().getMethod("format", double.class).invoke(economy, amount);
        } catch (Exception e) {
            return String.valueOf(amount);
        }
    }
}