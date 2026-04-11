package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SteamerBlockEntity extends BlockEntity {
    private static final int PROCESS_TIME = 200;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private int progress = 0;

    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STEAMER, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack stack = entity.inventory.get(0);
        if (stack.isEmpty() || !stack.is(ModItems.RICE)) {
            entity.progress = 0;
            return;
        }

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
            stack.shrink(1);
            if (entity.inventory.get(0).isEmpty()) {
                entity.inventory.set(0, new ItemStack(ModItems.STEAMED_RICE));
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

    public NonNullList<ItemStack> getInventory() { return inventory; }
    public ItemStack getStack() { return inventory.get(0); }
    public void setStack(ItemStack stack) { inventory.set(0, stack); setChanged(); }
    public int getProgress() { return progress; }
    public int getMaxProgress() { return PROCESS_TIME; }

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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
