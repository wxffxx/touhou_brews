package com.wxffxx.touhoubrews.registry;

import com.wxffxx.touhoubrews.TouhouBrews;
import com.wxffxx.touhoubrews.item.DrinkableItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;

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

    // === Final Drinks ===
    public static final FoodProperties IBUKI_SAKE_FOOD = new FoodProperties.Builder()
            .nutrition(0).saturationMod(0f).alwaysEat()
            .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 2), 1.0f)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1), 1.0f)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 400, 1), 1.0f)
            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 1), 1.0f)
            .build();

    public static final Item IBUKI_SAKE = registerItem("ibuki_sake",
            new DrinkableItem(new FabricItemSettings().food(IBUKI_SAKE_FOOD).stacksTo(16)));

    public static final FoodProperties REMILIA_WINE_FOOD = new FoodProperties.Builder()
            .nutrition(0).saturationMod(0f).alwaysEat()
            .effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2400, 0), 1.0f)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1), 1.0f)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1), 1.0f)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0f)
            .build();

    public static final Item REMILIA_WINE = registerItem("remilia_wine",
            new DrinkableItem(new FabricItemSettings().food(REMILIA_WINE_FOOD).stacksTo(16)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(TouhouBrews.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TouhouBrews.LOGGER.info("Registering Mod Items for " + TouhouBrews.MOD_ID);
    }
}
