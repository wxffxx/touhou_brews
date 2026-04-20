package com.wxffxx.touhoubrews.item;

import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IbukiHyouItem extends RelicItem {
    private static final String LOADED_BREW_KEY = "LoadedBrew";
    private static final String LOADED_QUALITY_KEY = "LoadedQuality";
    private static final int COOLDOWN_TICKS = 20 * 60 * 5;

    public IbukiHyouItem(Properties properties, String descriptionKey) {
        super(properties, descriptionKey);
    }

    public static ItemStack loadWithBrew(ItemStack gourdStack, ItemStack brewStack) {
        ItemStack result = gourdStack.copyWithCount(1);
        CompoundTag tag = result.getOrCreateTag();
        ResourceLocation brewId = BuiltInRegistries.ITEM.getKey(brewStack.getItem());
        tag.putString(LOADED_BREW_KEY, brewId.toString());
        tag.putInt(LOADED_QUALITY_KEY, BrewItem.getQuality(brewStack));
        return result;
    }

    public static boolean hasLoadedBrew(ItemStack stack) {
        return getLoadedBrewType(stack) != null;
    }

    @Nullable
    public static Item getLoadedBrewItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(LOADED_BREW_KEY)) {
            return null;
        }

        ResourceLocation brewId = ResourceLocation.tryParse(tag.getString(LOADED_BREW_KEY));
        if (brewId == null || !BuiltInRegistries.ITEM.containsKey(brewId)) {
            return null;
        }

        Item item = BuiltInRegistries.ITEM.get(brewId);
        return item instanceof BrewItem ? item : null;
    }

    @Nullable
    public static BrewItem.BrewType getLoadedBrewType(ItemStack stack) {
        Item item = getLoadedBrewItem(stack);
        return item instanceof BrewItem brewItem ? brewItem.getBrewType() : null;
    }

    public static int getLoadedQuality(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(LOADED_QUALITY_KEY)) {
            return 0;
        }
        return Math.max(0, Math.min(5, tag.getInt(LOADED_QUALITY_KEY)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!hasLoadedBrew(stack)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("tooltip.touhou_brews.ibuki_hyou.empty"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide) {
            BrewItem.BrewType brewType = getLoadedBrewType(stack);
            if (brewType != null) {
                BrewItem.applyDrinkEffects(entityLiving, brewType, getLoadedQuality(stack));
                if (entityLiving instanceof Player player) {
                    player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                }
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        BrewItem.BrewType brewType = getLoadedBrewType(stack);
        if (brewType == null) {
            tooltipComponents.add(Component.translatable("tooltip.touhou_brews.ibuki_hyou.unloaded").withStyle(ChatFormatting.DARK_GRAY));
            tooltipComponents.add(Component.translatable("tooltip.touhou_brews.ibuki_hyou.load_hint").withStyle(ChatFormatting.GRAY));
            return;
        }

        int quality = getLoadedQuality(stack);
        tooltipComponents.add(
                Component.translatable("tooltip.touhou_brews.ibuki_hyou.loaded", BrewItem.getDisplayName(brewType, quality))
                        .withStyle(BrewItem.getQualityStyle(quality))
        );
        tooltipComponents.add(
                Component.translatable("tooltip.touhou_brews.ibuki_hyou.cooldown", BrewItem.formatDuration(COOLDOWN_TICKS))
                        .withStyle(ChatFormatting.AQUA)
        );

        List<Component> effectLines = BrewItem.getEffectTooltipLines(brewType, quality);
        if (!effectLines.isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.touhou_brews.effects").withStyle(ChatFormatting.GOLD));
            tooltipComponents.addAll(effectLines);
        }
    }
}
