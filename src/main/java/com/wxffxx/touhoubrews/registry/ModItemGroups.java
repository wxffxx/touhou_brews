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

                        // Brewing Intermediates
                        entries.accept(ModItems.STEAMED_RICE);
                        entries.accept(ModItems.KOJI_SPORES);
                        entries.accept(ModItems.KOJI_RICE);
                        entries.accept(ModItems.SAKE_MASH);
                        entries.accept(ModItems.GRAPE_JUICE);

                        // Final Drinks
                        entries.accept(ModItems.IBUKI_SAKE);
                        entries.accept(ModItems.REMILIA_WINE);
                        entries.accept(ModItems.EIRIN_UMESHU);

                        // Machines & Structures
                        entries.accept(ModBlocks.STEAMER);
                        entries.accept(ModBlocks.KOJI_TRAY);
                        entries.accept(ModBlocks.FERMENTATION_BARREL);
                        entries.accept(ModBlocks.PRESSER);
                        entries.accept(ModBlocks.INFUSION_JAR);
                        entries.accept(ModBlocks.GRAPE_TRELLIS);
                    })
                    .build()
    );

    public static void registerItemGroups() {
        TouhouBrews.LOGGER.info("Registering Item Groups for " + TouhouBrews.MOD_ID);
    }
}
