package com.wxffxx.touhoubrews.util;

import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public final class MachineInputRules {
    private MachineInputRules() {}

    public static boolean isSteamerInput(ItemStack stack) {
        return stack.is(ModItems.RICE) || stack.is(Items.WHEAT);
    }

    public static boolean isKojiRiceInput(ItemStack stack) {
        return stack.is(ModItems.STEAMED_RICE);
    }

    public static boolean isKojiSporesInput(ItemStack stack) {
        return stack.is(ModItems.KOJI_SPORES);
    }

    public static boolean isFermentationPrimaryInput(ItemStack stack) {
        return stack.is(ModItems.KOJI_RICE)
                || stack.is(ModItems.GRAPE_JUICE)
                || stack.is(ModItems.WORT)
                || stack.is(ModItems.STEAMED_RICE)
                || stack.is(Items.HONEY_BOTTLE);
    }

    public static boolean isFermentationSecondaryInput(ItemStack stack) {
        return stack.is(ModItems.STEAMED_RICE) || stack.is(Items.SUGAR) || stack.is(ModItems.HOPS);
    }

    public static boolean isWaterBottle(ItemStack stack) {
        return stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.WATER;
    }

    public static boolean isPresserInput(ItemStack stack) {
        return stack.is(ModItems.SAKE_MASH) || stack.is(ModItems.GRAPES) || stack.is(ModItems.HUANGJIU);
    }

    public static boolean isInfusionBaseInput(ItemStack stack) {
        return stack.is(ModItems.IBUKI_SAKE) || stack.is(ModItems.BAIJIU);
    }

    public static boolean isInfusionFruitInput(ItemStack stack) {
        return stack.is(ModItems.GREEN_PLUM) || stack.is(Items.NETHER_WART);
    }

    public static boolean isInfusionSweetenerInput(ItemStack stack) {
        return stack.is(Items.SUGAR);
    }
}
