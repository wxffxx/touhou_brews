# Touhou Brews (东方酒艺)

[简体中文](README_ZH.md)

<!-- [Logo Placeholder] -->

A Minecraft Fabric 1.20.1 mod inspired by Touhou Project, focusing on realistic, immersive brewing and characteristic alcohol production machinery. 

### Demonstration
![In-game Demonstration](docs/demonstration.png)

---

## Brewing Pipelines

This mod features a highly realistic brewing process combined with immersive interaction mechanics. The machines have been transitioned to GUI-based container interactions: right-click to open the machine interface, place ingredients in the slots, and observe the processing through progress and state indicators.

### 🍶 Sake Pipeline - **[Completed]**
The classic "multiple parallel fermentation" process, divided into four stages:
1. **Rice Farming**: Plant rice seeds and harvest 🌾**Rice**.
2. **Steamer**: Place a heat source (campfire/lava) below. Open the GUI and insert Rice to steam it into 🍚**Steamed Rice**. (10s)
3. **Koji Tray**: Must be placed in a dim area (light level ≤ 7). Open the GUI and insert Steamed Rice alongside Koji Spores to cultivate **Koji Rice**. (30s)
4. **Fermentation Barrel**: Open the GUI and insert Koji Rice + Steamed Rice + Water Bottle. After fermentation, it produces **Sake Mash**. (60s)
5. **Presser**: Open the GUI and press the Sake Mash to yield the legendary 🍶**Ibuki Sake**. (5s)

*Ibuki Sake Effect: Strength II, Resistance I, Slowness & Nausea (tipsyness penalty).*

### 🍷 Wine Pipeline - **[Completed]**
A direct fermentation process without saccharification:
1. **Grape Trellis**: A fence-like connecting structure. Plant grape seeds, and vines will grow on the trellis. Harvest 🍇**Grapes** repeatedly without breaking the vine.
2. **Presser**: Open the GUI to crush grapes into 🥤**Grape Juice**. (3s)
3. **Fermentation Barrel**: Open the GUI and insert Grape Juice + Water Bottle for direct fermentation, yielding 🍷**Remilia's Blood Red Wine**. (45s)

*Blood Red Wine Effect: Night Vision II, Strength II, Regeneration II, brief Nausea.*

### 🍑 Infusion Pipeline - **[Completed]**
A compound wine pipeline using base spirits to infuse green plums:
1. **Green Plum Farming**: Plant green plum seeds to harvest 🍑**Green Plums** suited for infusion.
2. **Base Spirit**: Complete the Sake Pipeline to obtain 🍶**Ibuki Sake**, which serves as the current version's base spirit.
3. **Infusion Jar**: Open the GUI and insert Ibuki Sake + Green Plum + Sugar. After sealing and resting, it yields **Eirin's Elegant Umeshu**. (60s)

*Elegant Umeshu Effect: Regeneration I, Speed I, Haste I, and extremely slight Nausea.*

---

## Roadmap & Progress

- [x] **Phase 1: Core Foundation** (Fabric Mod initialization, BlockEntity and Tick synchronization architecture, Mojang mapping standards).
- [x] **Phase 2: Crop Systems** (8-stage rice crop, 3-stage adaptive grape trellis vines, Loot Tables, Fortune enchantment support).
- [x] **Phase 3: Sake Pipeline** (Steamer, Koji Tray, Fermentation Barrel, and Presser interaction logic, particle effects, multi-model states, bidirectional localization).
- [x] **Phase 4A: Wine Extension** (Reusing Presser and Barrel via multi-recipe support for the Western red wine branch).
- [x] **Phase 4B: Infusion Process** (Added Green Plum crops and Infusion Jars. Infuse green plums with sugar and Ibuki Sake base to produce Eirin's Elegant Umeshu).
- [ ] **Phase 4C: Distillation Process**: Plan to introduce a Distillation Tower machine to purify low-proof spirits into high-proof alcohol bases.
- [ ] **Phase 4D: Drunkenness System & Legendary Containers**: Plan to implement a global multi-stage drunkenness debuff system alongside legendary items (e.g., the Ibuki Gourd which auto-replenishes).

---

## Installation

- **Minecraft Version**: `1.20.1`
- **Mod Loader**: `Fabric 0.15+`
- **Dependencies**: Requires `Fabric API`.
- To contribute to development, clone the repository and run `./gradlew build` (JDK 21 recommended).
