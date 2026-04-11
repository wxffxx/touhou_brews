# 东方酿酒模组 (Touhou Brews) 架构计划

为了实现您要求的“完全解耦”和“原版独立种植”，我们需要将这个东方酿酒模组作为一个全新的、独立的 Fabric 模组进行开发，而不是作为高级农业 (`advanced_agriculture`) 的一个子模块。

这样得到的好处是：**即使玩家没有安装高级农业，仍然可以在原版的耕地和泥土上种植酿酒所需的农作物（如水稻、大麦、葡萄等），体验完整的酿酒流程。**

> [!IMPORTANT]
> **需要您的确认**：这个新模组我们将基于您之前整理好的 `fabric_mod_template` 模板来创建。
> 建议的模组名称(Mod Name): **Touhou Brews** (东方酿)
> 建议的模组ID(Mod ID): `touhou_brews`
> 建议的包名(Package): `com.wxffxx.touhoubrews`

## 跨模组兼容性策略 (解耦方案)

为了防止未来当玩家同时安装《东方酿》和《高级农业》时出现冲突或割裂感，我们将采取以下策略：
1. **独立注册**：《东方酿》会独立注册自己需要的作物（例如：`touhou_brews:rice_seeds`, `touhou_brews:rice`）。
2. **使用公共标签 (Fabric Tags)**：我们会为作物添加通用的 Fabric 标签（例如 `#c:crops/rice`, `#c:grain` 等）。这样，如果您的《高级农业》模组也需要用到米，或者有其他机器能处理米，只要双方都认 `#c:crops/rice` 这个标签，它们就可以完美互通，而不需要在代码中互相引用 (Hard Dependency)。
3. **原版种植方式**：不依赖任何自定义土壤。水稻/大麦种在原版耕地 (Farmland) 上，葡萄藤种在泥土或特定方块的侧面。

---

## Proposed Changes (实装计划)

### Phase 1: 独立模组初始化
*   **[NEW]** 复制 `/Users/wxffxx/Documents/26s/mc_mod/fabric_mod_template` 创建新目录 `/Users/wxffxx/Documents/26s/mc_mod/touhou_brews`。
*   **[MODIFY]** 修改 `gradle.properties`，设置 `archives_base_name = touhou_brews`，包名和 Mod ID。
*   **[MODIFY]** 根据新信息修改 `fabric.mod.json` 等配置文件。

### Phase 2: 基础作物系统 (在原版中种植)
在这个阶段，我们会先实现用于酿造第一批特色饮品的基础作物。
*   **[NEW] 水稻 (Rice)**：用于酿造清酒。普通农作物形式（类似小麦的种植逻辑，可在此基础上稍微魔改需要临近水源）。
*   **[NEW] 暗夜葡萄 (Nightshade Grape)**：可在低亮度种植的特殊葡萄，用于酿造斯卡雷特血红葡萄酒。

### Phase 3: 模块化酿造机制 (核心多方块/单方块流水线)
为了还原现实世界的发酵工艺，我们将根据分类好的 5 个层级来分步实装机制：
1.  **[NEW] 培育盘 (Koji Tray)**：接收“蒸米”和“菌种”，经过时间后产出“米曲”。
2.  **[NEW] 隔水蒸锅 (Steamer)**：可以将底部的热源（如营火）传导，将水稻蒸熟。
3.  **[NEW] 发酵大木桶 (Fermentation Barrel)**：核心槽位（可放入液体和多种固体），将米曲、蒸米、水与酵母结合，经过多天发酵为“酒醪”。
4.  **[NEW] 压榨床 (Presser)**：压榨酒醪，产出原酒并掉落酒粕残渣。
5.  **[NEW] 蒸馏塔 (Distiller)** 与 **泡酒罐 (Infusion Jar)**：后续用于拓展烈酒和梅酒等饮品。

### Phase 4: 第一批传奇饮品
*   **[NEW] 鬼族大吟酿 (Ibuki Sake)**：
    *   **效果**：力量 III，抗性提升 II。副作用：反胃 II，缓慢 II。只有持有传说容器的人能抵御。
    *   **获取路径**：水稻 -> 蒸米 -> (培育)鬼族特供米曲 -> 发酵 -> 压榨。

## Verification Plan

### Manual Verification
1.  进入 IDE，确认在创造模式物品栏能同时看到“高级农业”与“东方酒艺”的物品（如果是同一工作区），或者单独测试“东方酒艺”的独立。
2.  在原版泥土/耕地上，手持“稻米种子”能成功种下并成长。
3.  用骨粉催熟后能掉落稻米。
4.  各个方块（发酵桶等）的 GUI 和加工行为能正常工作。
