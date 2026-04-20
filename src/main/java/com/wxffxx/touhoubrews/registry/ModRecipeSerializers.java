package com.wxffxx.touhoubrews.registry;

import com.wxffxx.touhoubrews.TouhouBrews;
import com.wxffxx.touhoubrews.recipe.HoshigumaHaiRecipe;
import com.wxffxx.touhoubrews.recipe.IbukiHyouRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ModRecipeSerializers {
    public static final RecipeSerializer<IbukiHyouRecipe> IBUKI_HYOU =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                    new ResourceLocation(TouhouBrews.MOD_ID, "ibuki_hyou"),
                    new SimpleCraftingRecipeSerializer<>(IbukiHyouRecipe::new));

    public static final RecipeSerializer<HoshigumaHaiRecipe> HOSHIGUMA_HAI =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                    new ResourceLocation(TouhouBrews.MOD_ID, "hoshiguma_hai"),
                    new SimpleCraftingRecipeSerializer<>(HoshigumaHaiRecipe::new));

    public static void register() {
        TouhouBrews.LOGGER.info("Registering recipe serializers for {}", TouhouBrews.MOD_ID);
    }
}
