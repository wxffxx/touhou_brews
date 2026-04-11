package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.menu.FermentationBarrelMenu;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import com.wxffxx.touhoubrews.util.MachineInputRules;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
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

/**
 * 发酵桶 - 多配方支持:
 * 1. 清酒线: 米曲 + 蒸米 + 水瓶 → 酒醪 (60秒)
 * 2. 果酒线: 葡萄汁 + 水瓶 → 蕾米莉亚红酒 (45秒)
 */
public class FermentationBarrelBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Container {
    private static final int SAKE_PROCESS_TIME = 1200;
    private static final int WINE_PROCESS_TIME = 900;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    private int progress = 0;
    private int currentMaxProgress = SAKE_PROCESS_TIME;

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
        ItemStack output = entity.inventory.get(3);

        ItemStack resultItem = null;
        int maxTime = 0;

        if (!slot0.isEmpty() && slot0.is(ModItems.KOJI_RICE)
                && !slot1.isEmpty() && MachineInputRules.isFermentationSecondaryInput(slot1)
                && !slot2.isEmpty() && MachineInputRules.isWaterBottle(slot2)) {
            resultItem = new ItemStack(ModItems.SAKE_MASH);
            maxTime = SAKE_PROCESS_TIME;
        } else if (!slot0.isEmpty() && slot0.is(ModItems.GRAPE_JUICE)
                && !slot2.isEmpty() && MachineInputRules.isWaterBottle(slot2)) {
            resultItem = new ItemStack(ModItems.REMILIA_WINE);
            maxTime = WINE_PROCESS_TIME;
        }

        if (resultItem == null) { entity.resetProgress(); return; }

        boolean outputFree = output.isEmpty()
                || (ItemStack.isSameItemSameTags(output, resultItem)
                    && output.getCount() < output.getMaxStackSize());
        if (!outputFree) { entity.resetProgress(); return; }

        entity.currentMaxProgress = maxTime;
        entity.progress++;
        entity.setChanged();

        if (entity.progress % 30 == 0 && level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.BUBBLE_POP,
                    pos.getX()+0.5, pos.getY()+0.85, pos.getZ()+0.5,
                    2, 0.15, 0.0, 0.15, 0.01);
        }

        if (entity.progress >= maxTime) {
            entity.progress = 0;
            slot0.shrink(1);
            if (!slot1.isEmpty() && slot1.is(ModItems.STEAMED_RICE)) {
                slot1.shrink(1);
            }
            entity.inventory.set(2, new ItemStack(Items.GLASS_BOTTLE));

            if (output.isEmpty()) {
                entity.inventory.set(3, resultItem.copy());
            } else {
                output.grow(1);
            }
            entity.setChanged();

            if (level instanceof ServerLevel sl) {
                sl.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 0.6f);
            }
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

    // --- MenuProvider ---
    @Override
    public Component getDisplayName() { return Component.translatable("container.touhou_brews.fermentation_barrel"); }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new FermentationBarrelMenu(syncId, playerInv, this, data);
    }

    public NonNullList<ItemStack> getInventory() { return inventory; }

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
