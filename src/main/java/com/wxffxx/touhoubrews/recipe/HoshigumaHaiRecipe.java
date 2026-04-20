package com.wxffxx.touhoubrews.recipe;

import com.wxffxx.touhoubrews.item.BrewItem;
import com.wxffxx.touhoubrews.registry.ModItems;
import com.wxffxx.touhoubrews.registry.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class HoshigumaHaiRecipe extends CustomRecipe {
    public HoshigumaHaiRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasCup = false;
        boolean hasBrew = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.HOSHIGUMA_HAI)) {
                if (hasCup) return false;
                hasCup = true;
            } else if (stack.getItem() instanceof BrewItem) {
                if (hasBrew) return false;
                // ★6 hidden brews cannot be upgraded
                if (BrewItem.getQuality(stack) >= 6) return false;
                hasBrew = true;
            } else {
                return false;
            }
        }

        return hasCup && hasBrew;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof BrewItem) {
                Item brewItem = stack.getItem();
                return BrewItem.create(brewItem, 5);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(ModItems.HOSHIGUMA_HAI)) {
                remaining.set(i, stack.copyWithCount(1));
            }
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HOSHIGUMA_HAI;
    }
}
