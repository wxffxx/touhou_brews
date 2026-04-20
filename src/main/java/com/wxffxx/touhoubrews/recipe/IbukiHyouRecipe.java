package com.wxffxx.touhoubrews.recipe;

import com.wxffxx.touhoubrews.item.BrewItem;
import com.wxffxx.touhoubrews.item.IbukiHyouItem;
import com.wxffxx.touhoubrews.registry.ModItems;
import com.wxffxx.touhoubrews.registry.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class IbukiHyouRecipe extends CustomRecipe {
    public IbukiHyouRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasGourd = false;
        boolean hasBrew = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.IBUKI_HYOU)) {
                if (hasGourd) return false;
                hasGourd = true;
            } else if (stack.getItem() instanceof BrewItem) {
                if (hasBrew) return false;
                // ★6 hidden brews cannot be loaded into the gourd
                if (BrewItem.getQuality(stack) >= 6) return false;
                hasBrew = true;
            } else {
                return false;
            }
        }

        return hasGourd && hasBrew;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack gourdStack = ItemStack.EMPTY;
        ItemStack brewStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.IBUKI_HYOU)) {
                gourdStack = stack;
            } else if (stack.getItem() instanceof BrewItem) {
                brewStack = stack;
            }
        }

        if (gourdStack.isEmpty() || brewStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return IbukiHyouItem.loadWithBrew(gourdStack, brewStack);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItems.IBUKI_HYOU);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.IBUKI_HYOU;
    }
}
