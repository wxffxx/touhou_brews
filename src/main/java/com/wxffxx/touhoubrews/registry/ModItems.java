package com.wxffxx.touhoubrews.registry;

import com.wxffxx.touhoubrews.TouhouBrews;
import com.wxffxx.touhoubrews.item.BrewItem;
import com.wxffxx.touhoubrews.item.IbukiHyouItem;
import com.wxffxx.touhoubrews.item.RelicItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;

public class ModItems {

    // === Basic Agriculture ===
    public static final Item RICE = registerItem("rice",
            new Item(new FabricItemSettings()));
    public static final Item RICE_SEEDS = registerItem("rice_seeds",
            new ItemNameBlockItem(ModBlocks.RICE_CROP, new FabricItemSettings()));
    public static final Item GRAPES = registerItem("grapes",
            new Item(new FabricItemSettings()));
    public static final Item GRAPE_SEEDS = registerItem("grape_seeds",
            new Item(new FabricItemSettings()));
    public static final Item GREEN_PLUM = registerItem("green_plum",
            new Item(new FabricItemSettings()));
    public static final Item GREEN_PLUM_SEEDS = registerItem("green_plum_seeds",
            new ItemNameBlockItem(ModBlocks.GREEN_PLUM_CROP, new FabricItemSettings()));
    public static final Item HOPS = registerItem("hops",
            new Item(new FabricItemSettings()));
    public static final Item HOPS_SEEDS = registerItem("hops_seeds",
            new ItemNameBlockItem(ModBlocks.HOPS_CROP, new FabricItemSettings()));

    // === Brewing Intermediates ===
    public static final Item STEAMED_RICE = registerItem("steamed_rice",
            new Item(new FabricItemSettings()));
    public static final Item KOJI_SPORES = registerItem("koji_spores",
            new Item(new FabricItemSettings()));
    public static final Item KOJI_RICE = registerItem("koji_rice",
            new Item(new FabricItemSettings()));
    public static final Item SAKE_MASH = registerItem("sake_mash",
            new Item(new FabricItemSettings()));
    public static final Item GRAPE_JUICE = registerItem("grape_juice",
            new Item(new FabricItemSettings()));
    public static final Item WORT = registerItem("wort",
            new Item(new FabricItemSettings()));

    public static final Item IBUKI_SAKE = registerItem("ibuki_sake",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.SAKE));

    // === Remilia Wine: 6 separate items by quality tier ===
    public static final Item REMILIA_WINE_SPOILED = registerItem("remilia_wine_spoiled",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.WINE, 0));
    public static final Item REMILIA_WINE_Q1 = registerItem("remilia_wine_q1",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.WINE, 1));
    public static final Item REMILIA_WINE_Q2 = registerItem("remilia_wine_q2",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.WINE, 2));
    public static final Item REMILIA_WINE_Q3 = registerItem("remilia_wine_q3",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.WINE, 3));
    public static final Item REMILIA_WINE_Q4 = registerItem("remilia_wine_q4",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.WINE, 4));
    public static final Item REMILIA_WINE = registerItem("remilia_wine",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.WINE, 5));

    /** Returns the correct wine item for a given quality (0–5). */
    public static Item getWineByQuality(int quality) {
        return switch (quality) {
            case 1 -> REMILIA_WINE_Q1;
            case 2 -> REMILIA_WINE_Q2;
            case 3 -> REMILIA_WINE_Q3;
            case 4 -> REMILIA_WINE_Q4;
            case 5 -> REMILIA_WINE;
            default -> REMILIA_WINE_SPOILED;
        };
    }

    // === Eirin Umeshu (梅酒): 6 separate items by quality tier ===
    public static final Item EIRIN_UMESHU_SPOILED = registerItem("eirin_umeshu_spoiled",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.UMESHU, 0));
    public static final Item EIRIN_UMESHU_Q1 = registerItem("eirin_umeshu_q1",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.UMESHU, 1));
    public static final Item EIRIN_UMESHU_Q2 = registerItem("eirin_umeshu_q2",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.UMESHU, 2));
    public static final Item EIRIN_UMESHU_Q3 = registerItem("eirin_umeshu_q3",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.UMESHU, 3));
    public static final Item EIRIN_UMESHU_Q4 = registerItem("eirin_umeshu_q4",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.UMESHU, 4));
    public static final Item EIRIN_UMESHU = registerItem("eirin_umeshu",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.UMESHU, 5));

    /** Returns the correct umeshu item for a given quality (0–5). */
    public static Item getUmeshuByQuality(int quality) {
        return switch (quality) {
            case 1 -> EIRIN_UMESHU_Q1;
            case 2 -> EIRIN_UMESHU_Q2;
            case 3 -> EIRIN_UMESHU_Q3;
            case 4 -> EIRIN_UMESHU_Q4;
            case 5 -> EIRIN_UMESHU;
            default -> EIRIN_UMESHU_SPOILED;
        };
    }

    // === Aomeshu (青梅酒): 6 separate items by quality tier ===
    public static final Item AOMESHU_SPOILED = registerItem("aomeshu_spoiled",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.AOMESHU, 0));
    public static final Item AOMESHU_Q1 = registerItem("aomeshu_q1",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.AOMESHU, 1));
    public static final Item AOMESHU_Q2 = registerItem("aomeshu_q2",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.AOMESHU, 2));
    public static final Item AOMESHU_Q3 = registerItem("aomeshu_q3",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.AOMESHU, 3));
    public static final Item AOMESHU_Q4 = registerItem("aomeshu_q4",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.AOMESHU, 4));
    public static final Item AOMESHU = registerItem("aomeshu",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.AOMESHU, 5));

    /** Returns the correct aomeshu item for a given quality (0–5). */
    public static Item getAomeshuByQuality(int quality) {
        return switch (quality) {
            case 1 -> AOMESHU_Q1;
            case 2 -> AOMESHU_Q2;
            case 3 -> AOMESHU_Q3;
            case 4 -> AOMESHU_Q4;
            case 5 -> AOMESHU;
            default -> AOMESHU_SPOILED;
        };
    }


    // === Beer: 6 separate items by quality tier ===
    public static final Item BEER_SPOILED = registerItem("beer_spoiled",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BEER, 0));
    public static final Item BEER_Q1 = registerItem("beer_q1",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BEER, 1));
    public static final Item BEER_Q2 = registerItem("beer_q2",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BEER, 2));
    public static final Item BEER_Q3 = registerItem("beer_q3",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BEER, 3));
    public static final Item BEER_Q4 = registerItem("beer_q4",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BEER, 4));
    public static final Item BEER = registerItem("beer",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BEER, 5));

    /** Hidden exclusive beer — ★6, unobtainable in survival. */
    public static final Item BEER_HIDDEN = registerItem("beer_hidden",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BEER, 6));

    /** Returns the correct beer item for a given quality (0–5). */
    public static Item getBeerByQuality(int quality) {
        return switch (quality) {
            case 1 -> BEER_Q1;
            case 2 -> BEER_Q2;
            case 3 -> BEER_Q3;
            case 4 -> BEER_Q4;
            case 5 -> BEER;
            default -> BEER_SPOILED;
        };
    }

    public static final Item MIJIU = registerItem("mijiu",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.MIJIU));

    public static final Item HUANGJIU = registerItem("huangjiu",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.HUANGJIU));

    public static final Item MEAD = registerItem("mead",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.MEAD));

    public static final Item BAIJIU = registerItem("baijiu",
            new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.BAIJIU));

    public static final Item IBUKI_HYOU = registerItem("ibuki_hyou",
            new IbukiHyouItem(new FabricItemSettings().stacksTo(1), "tooltip.touhou_brews.desc.ibuki_hyou"));

    public static final Item HOSHIGUMA_HAI = registerItem("hoshiguma_hai",
            new RelicItem(new FabricItemSettings().stacksTo(1), "tooltip.touhou_brews.desc.hoshiguma_hai"));

    // === Custom Brew Slots (custom_1 ~ custom_5) ===
    // Names, effects, and recipes for these are defined in config files.
    // Textures can be overridden via resource packs.

    public static final Item CUSTOM_1_SPOILED = registerItem("custom_1_spoiled", new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_1, 0));
    public static final Item CUSTOM_1_Q1      = registerItem("custom_1_q1",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_1, 1));
    public static final Item CUSTOM_1_Q2      = registerItem("custom_1_q2",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_1, 2));
    public static final Item CUSTOM_1_Q3      = registerItem("custom_1_q3",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_1, 3));
    public static final Item CUSTOM_1_Q4      = registerItem("custom_1_q4",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_1, 4));
    public static final Item CUSTOM_1         = registerItem("custom_1",          new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_1, 5));

    public static final Item CUSTOM_2_SPOILED = registerItem("custom_2_spoiled", new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_2, 0));
    public static final Item CUSTOM_2_Q1      = registerItem("custom_2_q1",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_2, 1));
    public static final Item CUSTOM_2_Q2      = registerItem("custom_2_q2",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_2, 2));
    public static final Item CUSTOM_2_Q3      = registerItem("custom_2_q3",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_2, 3));
    public static final Item CUSTOM_2_Q4      = registerItem("custom_2_q4",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_2, 4));
    public static final Item CUSTOM_2         = registerItem("custom_2",          new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_2, 5));

    public static final Item CUSTOM_3_SPOILED = registerItem("custom_3_spoiled", new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_3, 0));
    public static final Item CUSTOM_3_Q1      = registerItem("custom_3_q1",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_3, 1));
    public static final Item CUSTOM_3_Q2      = registerItem("custom_3_q2",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_3, 2));
    public static final Item CUSTOM_3_Q3      = registerItem("custom_3_q3",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_3, 3));
    public static final Item CUSTOM_3_Q4      = registerItem("custom_3_q4",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_3, 4));
    public static final Item CUSTOM_3         = registerItem("custom_3",          new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_3, 5));

    public static final Item CUSTOM_4_SPOILED = registerItem("custom_4_spoiled", new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_4, 0));
    public static final Item CUSTOM_4_Q1      = registerItem("custom_4_q1",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_4, 1));
    public static final Item CUSTOM_4_Q2      = registerItem("custom_4_q2",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_4, 2));
    public static final Item CUSTOM_4_Q3      = registerItem("custom_4_q3",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_4, 3));
    public static final Item CUSTOM_4_Q4      = registerItem("custom_4_q4",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_4, 4));
    public static final Item CUSTOM_4         = registerItem("custom_4",          new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_4, 5));

    public static final Item CUSTOM_5_SPOILED = registerItem("custom_5_spoiled", new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_5, 0));
    public static final Item CUSTOM_5_Q1      = registerItem("custom_5_q1",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_5, 1));
    public static final Item CUSTOM_5_Q2      = registerItem("custom_5_q2",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_5, 2));
    public static final Item CUSTOM_5_Q3      = registerItem("custom_5_q3",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_5, 3));
    public static final Item CUSTOM_5_Q4      = registerItem("custom_5_q4",      new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_5, 4));
    public static final Item CUSTOM_5         = registerItem("custom_5",          new BrewItem(new FabricItemSettings().stacksTo(16), BrewItem.BrewType.CUSTOM_5, 5));

    /** Returns the correct custom brew item for a given slot (1-5) and quality (0-5). */
    public static Item getCustomByQuality(int slot, int quality) {
        return switch (slot) {
            case 1 -> switch (quality) { case 1->CUSTOM_1_Q1; case 2->CUSTOM_1_Q2; case 3->CUSTOM_1_Q3; case 4->CUSTOM_1_Q4; case 5->CUSTOM_1; default->CUSTOM_1_SPOILED; };
            case 2 -> switch (quality) { case 1->CUSTOM_2_Q1; case 2->CUSTOM_2_Q2; case 3->CUSTOM_2_Q3; case 4->CUSTOM_2_Q4; case 5->CUSTOM_2; default->CUSTOM_2_SPOILED; };
            case 3 -> switch (quality) { case 1->CUSTOM_3_Q1; case 2->CUSTOM_3_Q2; case 3->CUSTOM_3_Q3; case 4->CUSTOM_3_Q4; case 5->CUSTOM_3; default->CUSTOM_3_SPOILED; };
            case 4 -> switch (quality) { case 1->CUSTOM_4_Q1; case 2->CUSTOM_4_Q2; case 3->CUSTOM_4_Q3; case 4->CUSTOM_4_Q4; case 5->CUSTOM_4; default->CUSTOM_4_SPOILED; };
            case 5 -> switch (quality) { case 1->CUSTOM_5_Q1; case 2->CUSTOM_5_Q2; case 3->CUSTOM_5_Q3; case 4->CUSTOM_5_Q4; case 5->CUSTOM_5; default->CUSTOM_5_SPOILED; };
            default -> CUSTOM_1_SPOILED;
        };
    }

    /** Resolve a brewTypeId + quality to an ItemStack. Used by machines that need to produce brew outputs. */
    public static ItemStack resolveBrewOutput(String brewTypeId, int quality) {
        return switch (brewTypeId) {
            case "beer"         -> new ItemStack(getBeerByQuality(quality));
            case "remilia_wine" -> new ItemStack(getWineByQuality(quality));
            case "eirin_umeshu" -> new ItemStack(getUmeshuByQuality(quality));
            case "aomeshu"      -> new ItemStack(getAomeshuByQuality(quality));
            case "custom_1"     -> new ItemStack(getCustomByQuality(1, quality));
            case "custom_2"     -> new ItemStack(getCustomByQuality(2, quality));
            case "custom_3"     -> new ItemStack(getCustomByQuality(3, quality));
            case "custom_4"     -> new ItemStack(getCustomByQuality(4, quality));
            case "custom_5"     -> new ItemStack(getCustomByQuality(5, quality));
            case "ibuki_sake"   -> BrewItem.create(IBUKI_SAKE, quality);
            case "mijiu"        -> BrewItem.create(MIJIU, quality);
            case "huangjiu"     -> BrewItem.create(HUANGJIU, quality);
            case "mead"         -> BrewItem.create(MEAD, quality);
            case "baijiu"       -> BrewItem.create(BAIJIU, quality);
            default -> ItemStack.EMPTY;
        };
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(TouhouBrews.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TouhouBrews.LOGGER.info("Registering Mod Items for " + TouhouBrews.MOD_ID);
    }
}
