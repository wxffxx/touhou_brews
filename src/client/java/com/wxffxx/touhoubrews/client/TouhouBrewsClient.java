package com.wxffxx.touhoubrews.client;

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

public class TouhouBrewsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenuTypes.STEAMER, SteamerScreen::new);
        MenuScreens.register(ModMenuTypes.KOJI_TRAY, KojiTrayScreen::new);
        MenuScreens.register(ModMenuTypes.FERMENTATION_BARREL, FermentationBarrelScreen::new);
        MenuScreens.register(ModMenuTypes.PRESSER, PresserScreen::new);
        MenuScreens.register(ModMenuTypes.INFUSION_JAR, InfusionJarScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RICE_CROP, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GREEN_PLUM_CROP, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GRAPE_TRELLIS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INFUSION_JAR, RenderType.translucent());
    }
}
