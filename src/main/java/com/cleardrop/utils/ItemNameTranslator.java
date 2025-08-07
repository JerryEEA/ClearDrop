package com.cleardrop.utils;

import org.bukkit.Material;
import java.util.HashMap;
import java.util.Map;

public class ItemNameTranslator {
    
    private static final Map<Material, String> CHINESE_NAMES = new HashMap<>();
    
    static {
        // 基礎方塊
        CHINESE_NAMES.put(Material.STONE, "石頭");
        CHINESE_NAMES.put(Material.COBBLESTONE, "鵝卵石");
        CHINESE_NAMES.put(Material.DIRT, "泥土");
        CHINESE_NAMES.put(Material.GRASS_BLOCK, "草方塊");
        CHINESE_NAMES.put(Material.SAND, "沙子");
        CHINESE_NAMES.put(Material.GRAVEL, "礫石");
        CHINESE_NAMES.put(Material.OAK_LOG, "橡木原木");
        CHINESE_NAMES.put(Material.BIRCH_LOG, "樺木原木");
        CHINESE_NAMES.put(Material.SPRUCE_LOG, "雲杉原木");
        CHINESE_NAMES.put(Material.JUNGLE_LOG, "叢林原木");
        CHINESE_NAMES.put(Material.ACACIA_LOG, "相思木原木");
        CHINESE_NAMES.put(Material.DARK_OAK_LOG, "黑橡木原木");
        
        // 礦物
        CHINESE_NAMES.put(Material.COAL, "煤炭");
        CHINESE_NAMES.put(Material.IRON_INGOT, "鐵錠");
        CHINESE_NAMES.put(Material.GOLD_INGOT, "金錠");
        CHINESE_NAMES.put(Material.DIAMOND, "鑽石");
        CHINESE_NAMES.put(Material.EMERALD, "綠寶石");
        CHINESE_NAMES.put(Material.NETHERITE_INGOT, "下界合金錠");
        CHINESE_NAMES.put(Material.COAL_ORE, "煤礦石");
        CHINESE_NAMES.put(Material.IRON_ORE, "鐵礦石");
        CHINESE_NAMES.put(Material.GOLD_ORE, "金礦石");
        CHINESE_NAMES.put(Material.DIAMOND_ORE, "鑽石礦石");
        CHINESE_NAMES.put(Material.EMERALD_ORE, "綠寶石礦石");
        
        // 食物
        CHINESE_NAMES.put(Material.APPLE, "蘋果");
        CHINESE_NAMES.put(Material.BREAD, "麵包");
        CHINESE_NAMES.put(Material.COOKED_BEEF, "熟牛肉");
        CHINESE_NAMES.put(Material.COOKED_PORKCHOP, "熟豬肉");
        CHINESE_NAMES.put(Material.COOKED_CHICKEN, "熟雞肉");
        CHINESE_NAMES.put(Material.COOKED_MUTTON, "熟羊肉");
        CHINESE_NAMES.put(Material.COOKED_SALMON, "熟鮭魚");
        CHINESE_NAMES.put(Material.COOKED_COD, "熟鱈魚");
        CHINESE_NAMES.put(Material.GOLDEN_APPLE, "金蘋果");
        CHINESE_NAMES.put(Material.ENCHANTED_GOLDEN_APPLE, "附魔金蘋果");
        
        // 工具
        CHINESE_NAMES.put(Material.WOODEN_SWORD, "木劍");
        CHINESE_NAMES.put(Material.STONE_SWORD, "石劍");
        CHINESE_NAMES.put(Material.IRON_SWORD, "鐵劍");
        CHINESE_NAMES.put(Material.GOLDEN_SWORD, "金劍");
        CHINESE_NAMES.put(Material.DIAMOND_SWORD, "鑽石劍");
        CHINESE_NAMES.put(Material.NETHERITE_SWORD, "下界合金劍");
        
        CHINESE_NAMES.put(Material.WOODEN_PICKAXE, "木鎬");
        CHINESE_NAMES.put(Material.STONE_PICKAXE, "石鎬");
        CHINESE_NAMES.put(Material.IRON_PICKAXE, "鐵鎬");
        CHINESE_NAMES.put(Material.GOLDEN_PICKAXE, "金鎬");
        CHINESE_NAMES.put(Material.DIAMOND_PICKAXE, "鑽石鎬");
        CHINESE_NAMES.put(Material.NETHERITE_PICKAXE, "下界合金鎬");
        
        // 裝備
        CHINESE_NAMES.put(Material.LEATHER_HELMET, "皮革頭盔");
        CHINESE_NAMES.put(Material.LEATHER_CHESTPLATE, "皮革胸甲");
        CHINESE_NAMES.put(Material.LEATHER_LEGGINGS, "皮革護腿");
        CHINESE_NAMES.put(Material.LEATHER_BOOTS, "皮革靴子");
        
        CHINESE_NAMES.put(Material.IRON_HELMET, "鐵頭盔");
        CHINESE_NAMES.put(Material.IRON_CHESTPLATE, "鐵胸甲");
        CHINESE_NAMES.put(Material.IRON_LEGGINGS, "鐵護腿");
        CHINESE_NAMES.put(Material.IRON_BOOTS, "鐵靴子");
        
        CHINESE_NAMES.put(Material.DIAMOND_HELMET, "鑽石頭盔");
        CHINESE_NAMES.put(Material.DIAMOND_CHESTPLATE, "鑽石胸甲");
        CHINESE_NAMES.put(Material.DIAMOND_LEGGINGS, "鑽石護腿");
        CHINESE_NAMES.put(Material.DIAMOND_BOOTS, "鑽石靴子");
        
        // 其他常見物品
        CHINESE_NAMES.put(Material.STICK, "木棒");
        CHINESE_NAMES.put(Material.STRING, "線");
        CHINESE_NAMES.put(Material.FEATHER, "羽毛");
        CHINESE_NAMES.put(Material.GUNPOWDER, "火藥");
        CHINESE_NAMES.put(Material.WHEAT, "小麥");
        CHINESE_NAMES.put(Material.WHEAT_SEEDS, "小麥種子");
        CHINESE_NAMES.put(Material.EGG, "雞蛋");
        CHINESE_NAMES.put(Material.LEATHER, "皮革");
        CHINESE_NAMES.put(Material.MILK_BUCKET, "牛奶桶");
        CHINESE_NAMES.put(Material.WATER_BUCKET, "水桶");
        CHINESE_NAMES.put(Material.LAVA_BUCKET, "熔岩桶");
        CHINESE_NAMES.put(Material.ROTTEN_FLESH, "腐肉");
        CHINESE_NAMES.put(Material.BONE, "骨頭");
        CHINESE_NAMES.put(Material.SPIDER_EYE, "蜘蛛眼");
        CHINESE_NAMES.put(Material.SLIME_BALL, "史萊姆球");
        CHINESE_NAMES.put(Material.GLOW_INK_SAC, "螢光墨囊");
        CHINESE_NAMES.put(Material.INK_SAC, "墨囊");
        CHINESE_NAMES.put(Material.JUNGLE_LEAVES, "叢林樹葉");
        CHINESE_NAMES.put(Material.OAK_LEAVES, "橡木樹葉");
        CHINESE_NAMES.put(Material.BIRCH_LEAVES, "樺木樹葉");
        CHINESE_NAMES.put(Material.SPRUCE_LEAVES, "雲杉樹葉");
        CHINESE_NAMES.put(Material.ACACIA_LEAVES, "相思木樹葉");
        CHINESE_NAMES.put(Material.DARK_OAK_LEAVES, "黑橡木樹葉");
        CHINESE_NAMES.put(Material.BROWN_MUSHROOM, "棕色蘑菇");
        CHINESE_NAMES.put(Material.RED_MUSHROOM, "紅色蘑菇");
        CHINESE_NAMES.put(Material.SUGAR_CANE, "甘蔗");
        CHINESE_NAMES.put(Material.CACTUS, "仙人掌");
        CHINESE_NAMES.put(Material.KELP, "海帶");
        CHINESE_NAMES.put(Material.SEAGRASS, "海草");
        CHINESE_NAMES.put(Material.BAMBOO, "竹子");
        
        // 下界物品
        CHINESE_NAMES.put(Material.NETHERRACK, "下界岩");
        CHINESE_NAMES.put(Material.NETHER_WART, "下界疙瘩");
        CHINESE_NAMES.put(Material.BLAZE_ROD, "烈焰棒");
        CHINESE_NAMES.put(Material.BLAZE_POWDER, "烈焰粉");
        CHINESE_NAMES.put(Material.GHAST_TEAR, "惡魂之淚");
        CHINESE_NAMES.put(Material.ENDER_PEARL, "終界珍珠");
        CHINESE_NAMES.put(Material.ENDER_EYE, "終界之眼");
        
        // 紅石相關
        CHINESE_NAMES.put(Material.REDSTONE, "紅石粉");
        CHINESE_NAMES.put(Material.REDSTONE_TORCH, "紅石火把");
        CHINESE_NAMES.put(Material.REPEATER, "中繼器");
        CHINESE_NAMES.put(Material.COMPARATOR, "比較器");
        CHINESE_NAMES.put(Material.PISTON, "活塞");
        CHINESE_NAMES.put(Material.STICKY_PISTON, "黏性活塞");
        
        // 植物
        CHINESE_NAMES.put(Material.OAK_SAPLING, "橡木樹苗");
        CHINESE_NAMES.put(Material.BIRCH_SAPLING, "樺木樹苗");
        CHINESE_NAMES.put(Material.SPRUCE_SAPLING, "雲杉樹苗");
        CHINESE_NAMES.put(Material.JUNGLE_SAPLING, "叢林樹苗");
        CHINESE_NAMES.put(Material.ACACIA_SAPLING, "相思木樹苗");
        CHINESE_NAMES.put(Material.DARK_OAK_SAPLING, "黑橡木樹苗");
        
        // 花朵
        CHINESE_NAMES.put(Material.POPPY, "虞美人");
        CHINESE_NAMES.put(Material.DANDELION, "蒲公英");
        CHINESE_NAMES.put(Material.BLUE_ORCHID, "藍色蘭花");
        CHINESE_NAMES.put(Material.ALLIUM, "紫紅球花");
        CHINESE_NAMES.put(Material.AZURE_BLUET, "茜草花");
        CHINESE_NAMES.put(Material.RED_TULIP, "紅色鬱金香");
        CHINESE_NAMES.put(Material.ORANGE_TULIP, "橙色鬱金香");
        CHINESE_NAMES.put(Material.WHITE_TULIP, "白色鬱金香");
        CHINESE_NAMES.put(Material.PINK_TULIP, "粉色鬱金香");
        
        // 染料
        CHINESE_NAMES.put(Material.WHITE_DYE, "白色染料");
        CHINESE_NAMES.put(Material.BLACK_DYE, "黑色染料");
        CHINESE_NAMES.put(Material.RED_DYE, "紅色染料");
        CHINESE_NAMES.put(Material.GREEN_DYE, "綠色染料");
        CHINESE_NAMES.put(Material.BLUE_DYE, "藍色染料");
        CHINESE_NAMES.put(Material.YELLOW_DYE, "黃色染料");
        CHINESE_NAMES.put(Material.ORANGE_DYE, "橙色染料");
        CHINESE_NAMES.put(Material.PURPLE_DYE, "紫色染料");
        CHINESE_NAMES.put(Material.PINK_DYE, "粉色染料");
        CHINESE_NAMES.put(Material.LIME_DYE, "淺綠色染料");
        CHINESE_NAMES.put(Material.CYAN_DYE, "青色染料");
        CHINESE_NAMES.put(Material.LIGHT_BLUE_DYE, "淺藍色染料");
        CHINESE_NAMES.put(Material.MAGENTA_DYE, "品紅色染料");
        CHINESE_NAMES.put(Material.LIGHT_GRAY_DYE, "淺灰色染料");
        CHINESE_NAMES.put(Material.GRAY_DYE, "灰色染料");
        CHINESE_NAMES.put(Material.BROWN_DYE, "棕色染料");
    }
    
    public static String getChineseName(Material material) {
        return CHINESE_NAMES.getOrDefault(material, formatEnglishName(material));
    }
    
    private static String formatEnglishName(Material material) {
        String name = material.name().toLowerCase().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    
    public static boolean hasChineseName(Material material) {
        return CHINESE_NAMES.containsKey(material);
    }
}