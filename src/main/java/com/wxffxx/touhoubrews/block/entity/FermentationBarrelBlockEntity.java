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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 发酵桶 - 多配方支持:
 * 1. 清酒线: 米曲 + 蒸米 + 水瓶 → 酒醪 (60秒)
 * 2. 果酒线: 葡萄汁 + 水瓶 → 蕾米莉亚红酒 (45秒)
 *
 * Slots: 0=primary, 1=secondary, 2=water, 3=output
 */
public class FermentationBarrelBlockEntity extends BlockEntity {
    private static final int SAKE_PROCESS_TIME = 1200;   // 60 sec
    private static final int WINE_PROCESS_TIME = 900;    // 45 sec

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    private int progress = 0;
    private int currentMaxProgress = SAKE_PROCESS_TIME;

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTATION_BARREL, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FermentationBarrelBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack slot0 = entity.inventory.get(0);
        ItemStack slot1 = entity.inventory.get(1);
        ItemStack slot2 = entity.inventory.get(2);
        ItemStack output = entity.inventory.get(3);

        // Detect recipe
        ItemStack resultItem = null;
        int maxTime = 0;

        // Recipe 1: Sake — koji rice (0) + steamed rice (1) + water (2)
        if (!slot0.isEmpty() && slot0.is(ModItems.KOJI_RICE)
                && !slot1.isEmpty() && slot1.is(ModItems.STEAMED_RICE)
                && !slot2.isEmpty() && slot2.is(Items.POTION)) {
            resultItem = new ItemStack(ModItems.SAKE_MASH);
            maxTime = SAKE_PROCESS_TIME;
        }
        // Recipe 2: Wine — grape juice (0) + water (2), slot1 unused
        else if (!slot0.isEmpty() && slot0.is(ModItems.GRAPE_JUICE)
                && !slot2.isEmpty() && slot2.is(Items.POTION)) {
            resultItem = new ItemStack(ModItems.REMILIA_WINE);
            maxTime = WINE_PROCESS_TIME;
        }

        if (resultItem == null) { entity.progress = 0; return; }

        boolean outputFree = output.isEmpty()
                || (ItemStack.isSameItemSameTags(output, resultItem)
                    && output.getCount() < output.getMaxStackSize());
        if (!outputFree) { entity.progress = 0; return; }

        entity.currentMaxProgress = maxTime;
        entity.progress++;

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
                sl.playSound(null, pos, SoundEvents.BREWING_STAND_BREW,
                        SoundSource.BLOCKS, 1.0f, 0.6f);
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
