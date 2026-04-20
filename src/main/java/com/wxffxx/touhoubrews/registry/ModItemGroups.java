package com.wxffxx.touhoubrews.registry;

import com.wxffxx.touhoubrews.TouhouBrews;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final CreativeModeTab TOUHOU_BREWS_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            new ResourceLocation(TouhouBrews.MOD_ID, "touhou_brews_tab"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.touhou_brews"))
                    .icon(() -> new ItemStack(ModItems.IBUKI_SAKE))
                    .displayItems((displayContext, entries) -> {
                        // Agriculture
                        entries.accept(ModItems.RICE_SEEDS);
                        entries.accept(ModItems.RICE);
                        entries.accept(ModItems.GRAPE_SEEDS);
                        entries.accept(ModItems.GRAPES);
                        entries.accept(ModItems.GREEN_PLUM_SEEDS);
                        entries.accept(ModItems.GREEN_PLUM);
                        entries.accept(ModItems.HOPS_SEEDS);
                        entries.accept(ModItems.HOPS);

                        // Brewing Intermediates
                        entries.accept(ModItems.STEAMED_RICE);
                        entries.accept(ModItems.KOJI_SPORES);
                        entries.accept(ModItems.KOJI_RICE);
                        entries.accept(ModItems.SAKE_MASH);
                        entries.accept(ModItems.GRAPE_JUICE);
                        entries.accept(ModItems.WORT);

                        // Final Drinks
                        entries.accept(ModItems.IBUKI_SAKE);
                        entries.accept(ModItems.REMILIA_WINE_SPOILED);
                        entries.accept(ModItems.REMILIA_WINE_Q1);
                        entries.accept(ModItems.REMILIA_WINE_Q2);
                        entries.accept(ModItems.REMILIA_WINE_Q3);
                        entries.accept(ModItems.REMILIA_WINE_Q4);
                        entries.accept(ModItems.REMILIA_WINE);
                        entries.accept(ModItems.EIRIN_UMESHU_SPOILED);
                        entries.accept(ModItems.EIRIN_UMESHU_Q1);
                        entries.accept(ModItems.EIRIN_UMESHU_Q2);
                        entries.accept(ModItems.EIRIN_UMESHU_Q3);
                        entries.accept(ModItems.EIRIN_UMESHU_Q4);
                        entries.accept(ModItems.EIRIN_UMESHU);
                        entries.accept(ModItems.AOMESHU_SPOILED);
                        entries.accept(ModItems.AOMESHU_Q1);
                        entries.accept(ModItems.AOMESHU_Q2);
                        entries.accept(ModItems.AOMESHU_Q3);
                        entries.accept(ModItems.AOMESHU_Q4);
                        entries.accept(ModItems.AOMESHU);
                        entries.accept(ModItems.BEER_SPOILED);
                        entries.accept(ModItems.BEER_Q1);
                        entries.accept(ModItems.BEER_Q2);
                        entries.accept(ModItems.BEER_Q3);
                        entries.accept(ModItems.BEER_Q4);
                        entries.accept(ModItems.BEER); // ★5 幻想乡精酿
                        entries.accept(ModItems.BEER_HIDDEN); // ★6 ZUN精酿 — 隐藏款
                        entries.accept(ModItems.MIJIU);
                        entries.accept(ModItems.HUANGJIU);
                        entries.accept(ModItems.MEAD);
                        entries.accept(ModItems.BAIJIU);

                        // Custom brew slots (names/effects configurable in config/)
                        entries.accept(ModItems.CUSTOM_1_SPOILED); entries.accept(ModItems.CUSTOM_1_Q1); entries.accept(ModItems.CUSTOM_1_Q2); entries.accept(ModItems.CUSTOM_1_Q3); entries.accept(ModItems.CUSTOM_1_Q4); entries.accept(ModItems.CUSTOM_1);
                        entries.accept(ModItems.CUSTOM_2_SPOILED); entries.accept(ModItems.CUSTOM_2_Q1); entries.accept(ModItems.CUSTOM_2_Q2); entries.accept(ModItems.CUSTOM_2_Q3); entries.accept(ModItems.CUSTOM_2_Q4); entries.accept(ModItems.CUSTOM_2);
                        entries.accept(ModItems.CUSTOM_3_SPOILED); entries.accept(ModItems.CUSTOM_3_Q1); entries.accept(ModItems.CUSTOM_3_Q2); entries.accept(ModItems.CUSTOM_3_Q3); entries.accept(ModItems.CUSTOM_3_Q4); entries.accept(ModItems.CUSTOM_3);
                        entries.accept(ModItems.CUSTOM_4_SPOILED); entries.accept(ModItems.CUSTOM_4_Q1); entries.accept(ModItems.CUSTOM_4_Q2); entries.accept(ModItems.CUSTOM_4_Q3); entries.accept(ModItems.CUSTOM_4_Q4); entries.accept(ModItems.CUSTOM_4);
                        entries.accept(ModItems.CUSTOM_5_SPOILED); entries.accept(ModItems.CUSTOM_5_Q1); entries.accept(ModItems.CUSTOM_5_Q2); entries.accept(ModItems.CUSTOM_5_Q3); entries.accept(ModItems.CUSTOM_5_Q4); entries.accept(ModItems.CUSTOM_5);

                        // Machines & Structures
                        entries.accept(ModBlocks.STEAMER);
                        entries.accept(ModBlocks.KOJI_TRAY);
                        entries.accept(ModBlocks.FERMENTATION_BARREL);
                        entries.accept(ModBlocks.PRESSER);
                        entries.accept(ModBlocks.INFUSION_JAR);
                        entries.accept(ModBlocks.GRAPE_TRELLIS);

                        // Admin tools
                        entries.accept(ModBlocks.ADMIN_BREW);
                        entries.accept(ModItems.IBUKI_HYOU);
                        entries.accept(ModItems.HOSHIGUMA_HAI);
                    })
                    .build()
    );

    public static void registerItemGroups() {
        TouhouBrews.LOGGER.info("Registering Item Groups for " + TouhouBrews.MOD_ID);
    }
}
