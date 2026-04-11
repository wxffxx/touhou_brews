# 东方酒艺 (Touhou Brews) 开发任务清单

- [x] **Phase 1: 独立模组初始化**
  - [x] 复制 `fabric_mod_template` 目录到 `touhou_brews`。
  - [x] 修改 `gradle.properties`，设置基础属性。
  - [x] 修改 `fabric.mod.json` 以配置正确的 Mod ID (`touhou_brews`) 和中文名 (东方酒艺)。
  - [x] 清理和重命名主类及包结构。
  - [x] 测试模组是否成功构建及运行。

- [x] **Phase 2: 基础作物系统**
  - [x] 设定 `touhou_brews` 特有作物的注册结构 (Items, Blocks)。
  - [x] 注册"水稻"的种子和农作物方块。
  - [x] 注册对应的模型 (Models)、贴图 (Textures) 及方块状态 (Blockstates)。
  - [x] 确保这些作物能在原版泥土/耕地上正常生长。

- [x] **Phase 3: 酿造机器与完整清酒流水线**
  - [x] 水稻掉落物表 (Loot Table) - 成熟掉稻米+种子，未成熟掉种子
  - [x] BlockEntity 注册系统 (ModBlockEntities) - 4台机器全部注册
  - [x] 蒸锅 (SteamerBlock + SteamerBlockEntity)
    - 右键打开 GUI，放入稻米；底部需要营火/火/岩浆/岩浆块作为热源
    - 10秒加工完成，产出蒸米，冒蒸汽粒子
  - [x] 培育盘 (KojiTrayBlock + KojiTrayBlockEntity)
    - 低矮盘状方块(4px高)，右键打开 GUI 放入蒸米和曲霉孢子
    - 需要光照 ≤ 7 才能工作，30秒培育成米曲
    - 冒孢子花粒子效果
  - [x] 发酵桶 (FermentationBarrelBlock + FermentationBarrelBlockEntity)
    - 4槽位：米曲 + 蒸米 + 水瓶 → 酒醪（60秒）
    - 冒气泡粒子，发酵完成返回玻璃瓶
    - GUI 交互显示进度与状态提示
  - [x] 压榨床 (PresserBlock + PresserBlockEntity)
    - 酒醪 → 鬼族大吟酿（5秒）
    - 滴蜂蜜粒子效果模拟出液
  - [x] 所有4台机器的方块模型、贴图、方块状态
  - [x] 完善语言文件 (en_us / zh_cn) - 全部16条翻译
  - [x] 所有方块的掉落物表

- [ ] **Phase 4: 后续扩展**
  - [ ] 蒸馏塔 (Distiller) - 烈酒路线
  - [x] 泡酒罐 (Infusion Jar) - 配制酒路线（梅酒等）
    - 青梅作物 + 青梅种子
    - 泡酒罐 GUI：鬼族大吟酿 + 青梅 + 糖 → 八意永琳的幽雅梅酒
    - 泡酒完成后产出新传奇酒类并带有药膳风味增益
  - [x] 更多东方特色酒类（血红葡萄酒、幽雅梅酒等）
  - [ ] 更多作物（大麦、青梅等）
    - [x] 青梅作物
  - [ ] 醉意系统 (Drunkenness meter)
  - [ ] 传说容器遗物（伊吹瓢、星熊酒盏）
