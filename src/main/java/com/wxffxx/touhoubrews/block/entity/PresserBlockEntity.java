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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 压榨床 - 多配方支持:
 * 1. 酒醪 (Sake Mash) → 鬼族大吟酿 (Ibuki Sake)
 * 2. 葡萄 (Grapes)     → 葡萄汁 (Grape Juice)
 */
public class PresserBlockEntity extends BlockEntity {
    private static final int SAKE_PROCESS_TIME = 100;   // 5 sec
    private static final int GRAPE_PROCESS_TIME = 60;    // 3 sec

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private int progress = 0;
    private int currentMaxProgress = SAKE_PROCESS_TIME;

    public PresserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSER, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PresserBlockEntity entity) {
        if (level.isClientSide) return;
        ItemStack input = entity.inventory.get(0);
        ItemStack output = entity.inventory.get(1);

        if (input.isEmpty()) { entity.progress = 0; return; }

        // Determine recipe
        ItemStack resultItem;
        int maxTime;
        if (input.is(ModItems.SAKE_MASH)) {
            resultItem = new ItemStack(ModItems.IBUKI_SAKE);
            maxTime = SAKE_PROCESS_TIME;
        } else if (input.is(ModItems.GRAPES)) {
            resultItem = new ItemStack(ModItems.GRAPE_JUICE);
            maxTime = GRAPE_PROCESS_TIME;
        } else {
            entity.progress = 0; return;
        }

        boolean outputFree = output.isEmpty()
                || (ItemStack.isSameItemSameTags(output, resultItem)
                    && output.getCount() < output.getMaxStackSize());
        if (!outputFree) { entity.progress = 0; return; }

        entity.currentMaxProgress = maxTime;
        entity.progress++;

        if (entity.progress % 10 == 0 && level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.DRIPPING_HONEY,
                    pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, 1, 0.2, 0.0, 0.2, 0.0);
        }

        if (entity.progress >= maxTime) {
            entity.progress = 0;
            input.shrink(1);
            if (output.isEmpty()) {
                entity.inventory.set(1, resultItem.copy());
            } else {
                output.grow(1);
            }
            entity.setChanged();
            if (level instanceof ServerLevel sl) {
                sl.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.2f);
            }
        }
    }

    public NonNullList<ItemStack> getInventory() { return inventory; }
    public int getProgress() { return progress; }
    public int getMaxProgress() { return currentMaxProgress; }

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
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
}
