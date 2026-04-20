package com.wxffxx.touhoubrews.client;

import com.wxffxx.touhoubrews.client.screen.AdminBrewScreen;
import com.wxffxx.touhoubrews.client.screen.FermentationBarrelScreen;
import com.wxffxx.touhoubrews.client.screen.InfusionJarScreen;
import com.wxffxx.touhoubrews.client.screen.KojiTrayScreen;
import com.wxffxx.touhoubrews.client.screen.PresserScreen;
import com.wxffxx.touhoubrews.client.screen.SteamerScreen;
import com.wxffxx.touhoubrews.registry.ModBlocks;
import com.wxffxx.touhoubrews.registry.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.gui.screens.MenuScreens;
import com.wxffxx.touhoubrews.registry.ModItems;

public class TouhouBrewsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenuTypes.STEAMER, SteamerScreen::new);
        MenuScreens.register(ModMenuTypes.KOJI_TRAY, KojiTrayScreen::new);
        MenuScreens.register(ModMenuTypes.FERMENTATION_BARREL, FermentationBarrelScreen::new);
        MenuScreens.register(ModMenuTypes.PRESSER, PresserScreen::new);
        MenuScreens.register(ModMenuTypes.INFUSION_JAR, InfusionJarScreen::new);
        MenuScreens.register(ModMenuTypes.ADMIN_BREW, AdminBrewScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RICE_CROP, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GREEN_PLUM_CROP, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HOPS_CROP, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GRAPE_TRELLIS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFUSION_JAR, RenderType.translucent());

        // All beer variants use real pixel-art textures, not potion overlays,
        // so they are intentionally excluded from the color tint registry.
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof com.wxffxx.touhoubrews.item.BrewItem brew) {
                return brew.getBrewType().color;
            }
            return -1;
        }, ModItems.IBUKI_SAKE,
                ModItems.MIJIU, ModItems.HUANGJIU, ModItems.MEAD, ModItems.BAIJIU);
    }
}
