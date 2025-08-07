# ClearDrop - 掉落物清理插件

一個功能強大的 Minecraft 掉落物清理插件，支援 1.21-1.21.8 版本，具有 GUI 商店功能和完善的管理系統。

## 功能特色

### 🧹 自動清理系統
- 可配置的清理間隔時間
- 多級警告系統（60秒、30秒、10秒、5秒前警告）
- 支援多世界清理
- 高性能異步清理
- 管理員強制清理功能

### 🛒 GUI 商店系統
- 被清理的掉落物自動進入商店
- 根據物品稀有度自動定價
- 完整的 GUI 界面
- 支援物品購買和管理
- 管理員一鍵清空商店

### ⚙️ 管理功能
- 計時器開關控制
- 世界管理（添加/移除清理世界）
- 插件重載功能
- 詳細的狀態查看
- 完善的權限系統

### 🚀 性能優化
- 異步處理，不影響伺服器性能
- 可配置的實體檢查限制
- 智能的清理算法
- 記憶體使用優化

## 安裝方法

1. 下載插件 jar 文件
2. 將文件放入伺服器的 `plugins` 資料夾
3. 重啟伺服器或使用 `/reload` 指令
4. 插件會自動生成配置文件

## 指令說明

### 主要指令

#### `/cleardrop` (別名: `/cd`, `/drop`)
管理員主指令，包含以下子指令：

- `/cleardrop help` - 顯示幫助信息
- `/cleardrop clear` - 立即清理掉落物
- `/cleardrop timer <start|stop|toggle|set> [時間]` - 計時器管理
- `/cleardrop shop <clear>` - 商店管理
- `/cleardrop world <add|remove|list> [世界名]` - 世界管理
- `/cleardrop reload` - 重載插件
- `/cleardrop status` - 查看插件狀態
- `/cleardrop info` - 查看插件信息

#### `/dropshop` (別名: `/ds`, `/shop`)
商店相關指令：

- `/dropshop` - 打開掉落物商店
- `/dropshop help` - 顯示商店幫助
- `/dropshop info` - 查看商店信息
- `/dropshop refresh` - 刷新商店

## 權限節點

| 權限 | 描述 | 預設 |
|------|------|------|
| `cleardrop.admin` | 管理員權限 | OP |
| `cleardrop.shop` | 使用商店權限 | 所有玩家 |
| `cleardrop.clear` | 強制清理權限 | OP |
| `cleardrop.reload` | 重載插件權限 | OP |

## 配置文件

### config.yml
主要配置文件，包含以下設置：

```yaml
# 清理設置
clear:
  interval: 300          # 清理間隔（秒）
  warning-times: [60, 30, 10, 5]  # 警告時間
  auto-start: true       # 自動啟動計時器
  worlds: ["world"]      # 清理世界列表
  add-to-shop: true      # 是否添加到商店

# 商店設置
shop:
  name: "&6&l掉落物商店"  # 商店名稱
  size: 54               # 商店大小
  enabled: true          # 是否啟用商店
  prices:                # 價格設置
    common: {min: 10, max: 50}
    uncommon: {min: 51, max: 150}
    rare: {min: 151, max: 400}
    epic: {min: 401, max: 700}
    legendary: {min: 701, max: 1000}
```

### shop.yml
商店數據文件，自動生成和管理，包含：
- 商店物品數據
- 統計信息
- 操作歷史

## 物品稀有度分類

插件會根據以下規則自動判定物品稀有度：

- **普通 (Common)**: 大部分基礎物品
- **稀有 (Uncommon)**: 鐵製品、紅石相關物品
- **史詩 (Rare)**: 鑽石製品、附魔書
- **傳說 (Epic)**: 下界之星、信標等稀有物品
- **神話 (Legendary)**: 龍蛋、終界之星等超稀有物品

## 使用範例

### 基本使用
1. 插件安裝後會自動開始清理計時器
2. 玩家可以使用 `/dropshop` 打開商店查看被清理的物品
3. 管理員可以使用 `/cleardrop status` 查看當前狀態

### 管理員操作
```
# 立即清理掉落物
/cleardrop clear

# 設置清理間隔為5分鐘
/cleardrop timer set 300

# 添加新的清理世界
/cleardrop world add world_nether

# 清空商店
/cleardrop shop clear

# 重載配置
/cleardrop reload
```

## 經濟插件整合

插件支援與經濟插件整合（需要 Vault）：
- 自動扣除玩家金錢
- 支援多種經濟插件
- 可配置的價格系統

## 性能說明

插件經過性能優化，包含以下特性：
- 異步處理，不阻塞主線程
- 智能實體檢查，避免過度掃描
- 記憶體使用優化
- 可配置的性能參數

## 故障排除

### 常見問題

**Q: 插件無法啟動？**
A: 檢查伺服器版本是否為 1.21-1.21.8，確保沒有衝突的插件。

**Q: 商店無法打開？**
A: 檢查玩家是否有 `cleardrop.shop` 權限。

**Q: 清理功能不工作？**
A: 檢查配置文件中的世界列表是否正確，確保計時器已啟動。

**Q: 購買物品失敗？**
A: 確保已安裝經濟插件並正確配置 Vault。

### 調試模式

在 `config.yml` 中啟用調試模式：
```yaml
debug:
  enabled: true
  log-clear-details: true
  log-shop-operations: true
```

## 更新日誌

### v1.0.0
- 初始版本發布
- 基本清理功能
- GUI 商店系統
- 完整的管理指令
- 多世界支援
- 性能優化

## 支援

如果您遇到問題或有建議，請：
1. 檢查本文檔的故障排除部分
2. 查看伺服器控制台的錯誤信息
3. 確保插件版本與伺服器版本相容

## 授權

本插件遵循 MIT 授權條款。

---

**ClearDrop v1.0.0** - 專為 Minecraft 1.21-1.21.8 設計的高性能掉落物清理插件