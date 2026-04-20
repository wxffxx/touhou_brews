package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.config.BrewConfigManager;
import com.wxffxx.touhoubrews.item.BrewItem;
import com.wxffxx.touhoubrews.menu.FermentationBarrelMenu;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import com.wxffxx.touhoubrews.util.MachineInputRules;
import com.wxffxx.touhoubrews.util.QualityAlgorithm;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FermentationBarrelBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, WorldlyContainer, BrewingMachine {
    // Process times are now read from config/touhou_brews/brew_recipes.json
    private static final int DEFAULT_PROCESS_TIME = 1000;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    private int progress = 0;
    private int currentMaxProgress = DEFAULT_PROCESS_TIME;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> currentMaxProgress;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> currentMaxProgress = value;
            }
        }
        @Override public int getCount() { return 2; }
    };

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTATION_BARREL, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FermentationBarrelBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack slot0 = entity.inventory.get(0);
        ItemStack slot1 = entity.inventory.get(1);
        ItemStack slot2 = entity.inventory.get(2);

        // Find first matching recipe from config
        com.wxffxx.touhoubrews.config.BrewRecipeEntry matched = null;
        for (com.wxffxx.touhoubrews.config.BrewRecipeEntry recipe
                : BrewConfigManager.recipes().fermentation_barrel) {
            if (matchesRecipe(recipe, slot0, slot1, slot2)) {
                matched = recipe;
                break;
            }
        }

        if (matched == null) {
            entity.resetProgress();
            if (!entity.inventory.get(3).isEmpty()) entity.inventory.set(3, ItemStack.EMPTY);
            return;
        }

        entity.currentMaxProgress = matched.perfect_time_ticks;
        entity.progress++;
        entity.setChanged();

        if (entity.progress % 30 == 0 && level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.BUBBLE_POP,
                    pos.getX()+0.5, pos.getY()+0.85, pos.getZ()+0.5,
                    2, 0.15, 0.0, 0.15, 0.01);
        }

        int penalty = QualityAlgorithm.computePenalty(
                slot0.getCount(),
                matched.require_slot1 ? slot1.getCount() : 0,
                slot2.getCount());
        int quality = QualityAlgorithm.calculateQuality(entity.progress, matched.perfect_time_ticks, penalty);
        entity.inventory.set(3, resolveBrewOutput(matched.output_brew_type, quality));
    }

    /** Check if the given slots match a fermentation barrel recipe from config. */
    private static boolean matchesRecipe(
            com.wxffxx.touhoubrews.config.BrewRecipeEntry recipe,
            ItemStack slot0, ItemStack slot1, ItemStack slot2) {
        if (slot0.isEmpty()) return false;
        if (!itemMatches(slot0, recipe.slot0)) return false;

        if (recipe.require_slot1) {
            if (slot1.isEmpty() || !itemMatches(slot1, recipe.slot1)) return false;
        } else {
            // If not required, slot1 must be empty (to avoid ambiguous matches)
            if (!slot1.isEmpty() && recipe.slot1 != null && !itemMatches(slot1, recipe.slot1)) return false;
        }

        if (slot2.isEmpty()) return false;
        return itemMatchesSlot2(slot2, recipe.slot2);
    }

    /** Match a stack against an item ID string, or null (empty ok). */
    private static boolean itemMatches(ItemStack stack, String itemId) {
        if (itemId == null) return true;
        try {
            net.minecraft.resources.ResourceLocation rl = new net.minecraft.resources.ResourceLocation(itemId);
            return stack.is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl));
        } catch (Exception e) { return false; }
    }

    /** Slot 2 supports the special keyword "water_bottle" in addition to item IDs. */
    private static boolean itemMatchesSlot2(ItemStack stack, String slot2Spec) {
        if ("water_bottle".equals(slot2Spec)) return MachineInputRules.isWaterBottle(stack);
        return itemMatches(stack, slot2Spec);
    }

    /** Resolve the output ItemStack for a brew type ID + quality. */
    private static ItemStack resolveBrewOutput(String brewTypeId, int quality) {
        return switch (brewTypeId) {
            case "beer"         -> new ItemStack(ModItems.getBeerByQuality(quality));
            case "remilia_wine" -> new ItemStack(ModItems.getWineByQuality(quality));
            case "eirin_umeshu" -> new ItemStack(ModItems.getUmeshuByQuality(quality));
            case "aomeshu"      -> new ItemStack(ModItems.getAomeshuByQuality(quality));
            case "custom_1"     -> new ItemStack(ModItems.getCustomByQuality(1, quality));
            case "custom_2"     -> new ItemStack(ModItems.getCustomByQuality(2, quality));
            case "custom_3"     -> new ItemStack(ModItems.getCustomByQuality(3, quality));
            case "custom_4"     -> new ItemStack(ModItems.getCustomByQuality(4, quality));
            case "custom_5"     -> new ItemStack(ModItems.getCustomByQuality(5, quality));
            default -> {
                // Try to look up the brew type's base item and use BrewItem.create()
                net.minecraft.world.item.Item base = resolveBaseItem(brewTypeId);
                yield base != null ? BrewItem.create(base, quality) : ItemStack.EMPTY;
            }
        };
    }

    /** Map a brew type ID to its base (fixedQuality=-1) item. */
    private static net.minecraft.world.item.Item resolveBaseItem(String brewTypeId) {
        return switch (brewTypeId) {
            case "ibuki_sake" -> ModItems.IBUKI_SAKE;
            case "mijiu"      -> ModItems.MIJIU;
            case "huangjiu"   -> ModItems.HUANGJIU;
            case "mead"       -> ModItems.MEAD;
            case "baijiu"     -> ModItems.BAIJIU;
            default -> null;
        };
    }

    @Override
    public void extractBrew() {
        ItemStack preview = inventory.get(3);
        if (preview.isEmpty() || progress == 0) return;

        // Give the player the item, or spawn it into the world
        ItemStack extracted = preview.copy();
        
        // Consume ingredients
        ItemStack slot0 = inventory.get(0);
        boolean hadSecondary = !inventory.get(1).isEmpty();
        boolean wasHoney = slot0.is(Items.HONEY_BOTTLE);
        boolean wasBeer = slot0.is(ModItems.WORT);
        
        inventory.set(0, ItemStack.EMPTY);
        if (hadSecondary || wasBeer) inventory.set(1, ItemStack.EMPTY);
        inventory.set(2, new ItemStack(Items.GLASS_BOTTLE)); // Return water bottle
        if (wasHoney && level != null) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), new ItemStack(Items.GLASS_BOTTLE));
        }
        
        inventory.set(3, ItemStack.EMPTY);
        progress = 0;
        setChanged();
        
        if (level instanceof ServerLevel sl) {
            sl.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 0.6f);
            // We shouldn't rely on player cursor to hold extracting because `onTake` gives them the item naturally.
            // Wait, if `onTake` gives them the preview stack, we don't need to manually spawn it!
            // OutputSlot onTake allows the player to hold the item they clicked. So we just clear the ingredients!
        }
    }

    // --- Container ---
    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(inventory, slot, amount); setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case 0 -> MachineInputRules.isFermentationPrimaryInput(stack);
            case 1 -> MachineInputRules.isFermentationSecondaryInput(stack);
            case 2 -> MachineInputRules.isWaterBottle(stack);
            default -> false;
        };
    }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0; }
    @Override public void clearContent() { inventory.clear(); }

    // --- WorldlyContainer (Disable Hoppers) ---
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0, 1, 2, 3}; }
    @Override public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) { return false; }
    @Override public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) { return false; }

    // --- MenuProvider ---
    @Override
    public Component getDisplayName() { return Component.translatable("container.touhou_brews.fermentation_barrel"); }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new FermentationBarrelMenu(syncId, playerInv, this, data);
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putInt("Progress", progress);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        inventory.clear();
        ContainerHelper.loadAllItems(tag, inventory);
        progress = tag.getInt("Progress");
    }

    @Nullable @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

    private void resetProgress() {
        if (progress != 0) {
            progress = 0;
            setChanged();
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }
}
