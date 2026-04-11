package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.menu.SteamerMenu;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SteamerBlockEntity extends BlockEntity implements MenuProvider, Container {
    private static final int PROCESS_TIME = 200;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME;
                case 2 -> hasHeatSource() ? 1 : 0;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            if (index == 0) progress = value;
        }
        @Override public int getCount() { return 3; }
    };

    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STEAMER, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack input = entity.inventory.get(0);
        ItemStack output = entity.inventory.get(1);

        if (input.isEmpty() || !input.is(ModItems.RICE)) {
            entity.progress = 0;
            return;
        }

        boolean outputFree = output.isEmpty()
                || (output.is(ModItems.STEAMED_RICE) && output.getCount() < output.getMaxStackSize());
        if (!outputFree) { entity.progress = 0; return; }

        if (!entity.hasHeatSource()) {
            entity.progress = 0;
            return;
        }

        entity.progress++;

        if (entity.progress % 20 == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    3, 0.2, 0.1, 0.2, 0.01);
        }

        if (entity.progress >= PROCESS_TIME) {
            entity.progress = 0;
            input.shrink(1);
            if (output.isEmpty()) {
                entity.inventory.set(1, new ItemStack(ModItems.STEAMED_RICE));
            } else {
                output.grow(1);
            }
            entity.setChanged();

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, pos, SoundEvents.BREWING_STAND_BREW,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private boolean hasHeatSource() {
        if (level == null) return false;
        BlockPos below = worldPosition.below();
        BlockState belowState = level.getBlockState(below);

        if (belowState.getBlock() instanceof CampfireBlock) {
            return belowState.getValue(CampfireBlock.LIT);
        }
        return belowState.is(Blocks.LAVA)
                || belowState.is(Blocks.FIRE)
                || belowState.is(Blocks.SOUL_FIRE)
                || belowState.is(Blocks.MAGMA_BLOCK);
    }

    // --- Container ---
    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(inventory, slot, amount); setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0; }
    @Override public void clearContent() { inventory.clear(); }

    // --- MenuProvider ---
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.touhou_brews.steamer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new SteamerMenu(syncId, playerInv, this, data);
    }

    public NonNullList<ItemStack> getInventory() { return inventory; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putInt("Progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.clear();
        ContainerHelper.loadAllItems(tag, inventory);
        progress = tag.getInt("Progress");
    }

    @Nullable @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
}
