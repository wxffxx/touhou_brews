package com.wxffxx.touhoubrews.item;

import com.wxffxx.touhoubrews.config.BrewConfigManager;
import com.wxffxx.touhoubrews.config.BrewEffectEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrewItem extends Item {

    public enum BrewType {
        SAKE("ibuki_sake", 0xD6F8FF), 
        WINE("remilia_wine", 0x8A0303), 
        UMESHU("eirin_umeshu", 0x98FF98),
        AOMESHU("aomeshu", 0xB8E8A0),
        BEER("beer", 0xE2A52C),
        MIJIU("mijiu", 0xF1E2B8),
        HUANGJIU("huangjiu", 0xC98A34),
        MEAD("mead", 0xF4B53F),
        BAIJIU("baijiu", 0xF6F7F7),
        // Pre-registered custom brew slots — names/effects/recipes defined in config
        CUSTOM_1("custom_1", 0xAAAAAA),
        CUSTOM_2("custom_2", 0xAAAAAA),
        CUSTOM_3("custom_3", 0xAAAAAA),
        CUSTOM_4("custom_4", 0xAAAAAA),
        CUSTOM_5("custom_5", 0xAAAAAA);

        public final String id;
        public final int color;
        BrewType(String id, int color) {
            this.id = id;
            this.color = color;
        }
    }

    private final BrewType brewType;
    private final int fixedQuality; // -1 = read from NBT; >= 0 = baked-in quality (used for split beer items)
    private static final int FIVE_STAR_DURATION = 20 * 60 * 5;

    public BrewItem(Properties properties, BrewType type) {
        this(properties, type, -1);
    }

    public BrewItem(Properties properties, BrewType type, int fixedQuality) {
        super(properties.food(
            new FoodProperties.Builder().alwaysEat().build()
        ));
        this.brewType = type;
        this.fixedQuality = fixedQuality;
    }

    public static int getQuality(ItemStack stack) {
        // If the item has a baked-in quality (e.g. separate beer_q1 item), use that directly.
        if (stack.getItem() instanceof BrewItem b && b.fixedQuality >= 0) {
            return b.fixedQuality;
        }
        if (stack.hasTag() && stack.getTag().contains("Quality")) {
            return Math.max(0, Math.min(5, stack.getTag().getInt("Quality")));
        }
        return 0; // Default to 0 if spawned without tag
    }

    public static ItemStack create(Item item, int quality) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putInt("Quality", quality);
        return stack;
    }

    public BrewType getBrewType() {
        return this.brewType;
    }

    public static BrewType getBrewType(ItemStack stack) {
        return stack.getItem() instanceof BrewItem brewItem ? brewItem.getBrewType() : null;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide) {
            int quality = getQuality(stack);
            
            if (quality == 0) {
                // Spoiled food
                entityLiving.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 1));
                entityLiving.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                entityLiving.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 1));
            } else {
                applyDrinkEffects(entityLiving, this.brewType, quality);
            }
        }
        
        ItemStack result = super.finishUsingItem(stack, level, entityLiving);
        if (entityLiving instanceof Player player && !player.getAbilities().instabuild) {
            if (result.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            } else {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return result;
    }

    public static void applyDrinkEffects(LivingEntity entity, BrewType type, int quality) {
        List<BrewEffectEntry> entries = BrewConfigManager.effects().getEffects(type.id, quality);
        for (BrewEffectEntry entry : entries) {
            resolveEffect(entry.effect).ifPresent(effect ->
                entity.addEffect(new MobEffectInstance(effect, entry.duration, entry.amplifier))
            );
        }
    }

    /** Resolves a Minecraft effect ID string to a MobEffect. */
    private static Optional<MobEffect> resolveEffect(String effectId) {
        try {
            ResourceLocation rl = new ResourceLocation(effectId);
            if (BuiltInRegistries.MOB_EFFECT.containsKey(rl)) {
                return Optional.of(BuiltInRegistries.MOB_EFFECT.get(rl));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
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
    public Component getName(ItemStack stack) {
        int quality = getQuality(stack);
        return getDisplayName(this.brewType, quality);
    }

    public static Component getDisplayName(BrewType brewType, int quality) {
        // Check config override first
        String configName = BrewConfigManager.display().getQualityName(brewType.id, quality);
        if (configName != null && !configName.isBlank()) {
            return Component.literal(configName).withStyle(getQualityStyle(quality));
        }
        // Fallback to lang key
        String translationKey = (quality == 0)
            ? "item.touhou_brews.spoiled_brew"
            : getQualityTranslationKey(brewType, quality);
        return Component.translatable(translationKey).withStyle(getQualityStyle(quality));
    }

    public static String getQualityTranslationKey(BrewType brewType, int quality) {
        return switch (quality) {
            case 1 -> "item.touhou_brews." + brewType.id + "_q1";
            case 2 -> "item.touhou_brews." + brewType.id + "_q2";
            case 3 -> "item.touhou_brews." + brewType.id + "_q3";
            case 4 -> "item.touhou_brews." + brewType.id + "_q4";
            default -> "item.touhou_brews." + brewType.id;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        int quality = getQuality(stack);
        
        StringBuilder stars = new StringBuilder();
        int maxStars = Math.max(5, quality);
        for (int i=0; i<maxStars; i++) {
            if (i < quality) stars.append("★");
            else stars.append("☆");
        }
        
        tooltipComponents.add(Component.translatable("tooltip.touhou_brews.quality").append(": " + stars.toString()).withStyle(getQualityStyle(quality)));
        tooltipComponents.add(getDescription(this.brewType));
        
        List<Component> effectLines = getEffectTooltipLines(this.brewType, quality);
        if (!effectLines.isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.touhou_brews.effects").withStyle(ChatFormatting.GOLD));
            tooltipComponents.addAll(effectLines);
        }
    }

    public static Component getDescription(BrewType brewType) {
        // Config override takes priority
        String configDesc = BrewConfigManager.display().getDescription(brewType.id);
        if (configDesc != null && !configDesc.isBlank()) {
            return Component.literal(configDesc).withStyle(ChatFormatting.GRAY);
        }
        return Component.translatable("tooltip.touhou_brews.desc." + brewType.id).withStyle(ChatFormatting.GRAY);
    }

    public static List<Component> getEffectTooltipLines(BrewType brewType, int quality) {
        List<Component> lines = new ArrayList<>();

        // ★0 spoiled: universal hardcoded bad effects
        if (quality == 0) {
            lines.add(formatEffectLine(MobEffects.CONFUSION, 1, 400, false));
            lines.add(formatEffectLine(MobEffects.POISON, 0, 200, false));
            lines.add(formatEffectLine(MobEffects.HUNGER, 1, 300, false));
            lines.add(Component.translatable("tooltip.touhou_brews.spoiled_desc").withStyle(ChatFormatting.DARK_RED));
            return lines;
        }

        List<BrewEffectEntry> entries = BrewConfigManager.effects().getEffects(brewType.id, quality);
        for (BrewEffectEntry entry : entries) {
            resolveEffect(entry.effect).ifPresent(effect ->
                lines.add(formatEffectLine(effect, entry.amplifier, entry.duration, isPositiveEffect(entry.effect)))
            );
        }
        return lines;
    }

    /** Negative effects shown in red in tooltip. */
    private static boolean isPositiveEffect(String effectId) {
        return switch (effectId) {
            case "minecraft:nausea", "minecraft:poison", "minecraft:hunger",
                 "minecraft:weakness", "minecraft:wither", "minecraft:blindness",
                 "minecraft:mining_fatigue", "minecraft:slowness" -> false;
            default -> true;
        };
    }

    private static Component formatEffectLine(MobEffect effect, int amplifier, int durationTicks, boolean positive) {
        MutableComponent line = Component.literal("• ").append(Component.translatable(effect.getDescriptionId()));
        if (amplifier > 0) {
            line.append(" ").append(Component.translatable("potion.potency." + amplifier));
        }
        line.append(Component.literal(" (" + formatDuration(durationTicks) + ")"));
        return line.withStyle(positive ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    public static String formatDuration(int durationTicks) {
        int totalSeconds = durationTicks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static Style getQualityStyle(int quality) {
        return switch (quality) {
            case 0 -> Style.EMPTY.withColor(0xFFFFFF); // White
            case 1 -> Style.EMPTY.withColor(0x00FF00); // Green
            case 2 -> Style.EMPTY.withColor(0x00BFFF); // Blue
            case 3 -> Style.EMPTY.withColor(0xB232D9); // Purple
            case 4 -> Style.EMPTY.withColor(0xFFFF00); // Yellow
            case 5 -> Style.EMPTY.withColor(0xFFA500); // Orange
            case 6 -> Style.EMPTY.withColor(0xFF0000); // Red — hidden exclusive
            default -> Style.EMPTY.withColor(ChatFormatting.GRAY);
        };
    }
}
