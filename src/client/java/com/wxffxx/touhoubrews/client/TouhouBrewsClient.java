package com.wxffxx.touhoubrews.client;

import com.wxffxx.touhoubrews.client.screen.FermentationBarrelScreen;
import com.wxffxx.touhoubrews.client.screen.KojiTrayScreen;
import com.wxffxx.touhoubrews.client.screen.PresserScreen;
import com.wxffxx.touhoubrews.client.screen.SteamerScreen;
import com.wxffxx.touhoubrews.registry.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class TouhouBrewsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenuTypes.STEAMER, SteamerScreen::new);
        MenuScreens.register(ModMenuTypes.KOJI_TRAY, KojiTrayScreen::new);
        MenuScreens.register(ModMenuTypes.FERMENTATION_BARREL, FermentationBarrelScreen::new);
        MenuScreens.register(ModMenuTypes.PRESSER, PresserScreen::new);
    }
}
