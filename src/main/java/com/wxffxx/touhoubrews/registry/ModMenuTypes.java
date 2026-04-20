package com.wxffxx.touhoubrews.registry;

import com.wxffxx.touhoubrews.menu.AdminBrewMenu;
import com.wxffxx.touhoubrews.menu.SteamerMenu;
import com.wxffxx.touhoubrews.menu.KojiTrayMenu;
import com.wxffxx.touhoubrews.menu.FermentationBarrelMenu;
import com.wxffxx.touhoubrews.menu.InfusionJarMenu;
import com.wxffxx.touhoubrews.menu.PresserMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final MenuType<SteamerMenu> STEAMER =
            Registry.register(BuiltInRegistries.MENU, new ResourceLocation("touhou_brews", "steamer"),
                    new ExtendedScreenHandlerType<>(SteamerMenu::new));

    public static final MenuType<KojiTrayMenu> KOJI_TRAY =
            Registry.register(BuiltInRegistries.MENU, new ResourceLocation("touhou_brews", "koji_tray"),
                    new ExtendedScreenHandlerType<>(KojiTrayMenu::new));

    public static final MenuType<FermentationBarrelMenu> FERMENTATION_BARREL =
            Registry.register(BuiltInRegistries.MENU, new ResourceLocation("touhou_brews", "fermentation_barrel"),
                    new ExtendedScreenHandlerType<>(FermentationBarrelMenu::new));

    public static final MenuType<PresserMenu> PRESSER =
            Registry.register(BuiltInRegistries.MENU, new ResourceLocation("touhou_brews", "presser"),
                    new ExtendedScreenHandlerType<>(PresserMenu::new));

    public static final MenuType<InfusionJarMenu> INFUSION_JAR =
            Registry.register(BuiltInRegistries.MENU, new ResourceLocation("touhou_brews", "infusion_jar"),
                    new ExtendedScreenHandlerType<>(InfusionJarMenu::new));

    public static final MenuType<AdminBrewMenu> ADMIN_BREW =
            Registry.register(BuiltInRegistries.MENU, new ResourceLocation("touhou_brews", "admin_brew"),
                    new ExtendedScreenHandlerType<>(AdminBrewMenu::new));

    public static void register() {
        // Static init triggers registration
    }
}
