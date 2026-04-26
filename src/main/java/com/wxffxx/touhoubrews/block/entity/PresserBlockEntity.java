package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.menu.PresserMenu;
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
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PresserBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, WorldlyContainer, BrewingMachine {
    private static final int SAKE_PROCESS_TIME = 100;
    private static final int GRAPE_PROCESS_TIME = 60;
    private static final int BAIJIU_PROCESS_TIME = 140;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
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

    public PresserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSER, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PresserBlockEntity entity) {
        if (level.isClientSide) return;
        ItemStack input = entity.inventory.get(0);

        if (input.isEmpty()) {
            entity.resetProgress();
            if (!entity.inventory.get(1).isEmpty()) entity.inventory.set(1, ItemStack.EMPTY);
            return;
        }

        String brewTypeId = null;
        int perfectTime;

        if (input.is(ModItems.SAKE_MASH)) {
            brewTypeId = "ibuki_sake";
            perfectTime = SAKE_PROCESS_TIME;
        } else if (input.is(ModItems.GRAPES)) {
            perfectTime = GRAPE_PROCESS_TIME;
        } else if (input.is(ModItems.HUANGJIU)) {
            brewTypeId = "baijiu";
            perfectTime = BAIJIU_PROCESS_TIME;
        } else {
            entity.resetProgress();
            if (!entity.inventory.get(1).isEmpty()) entity.inventory.set(1, ItemStack.EMPTY);
            return;
        }

        entity.currentMaxProgress = perfectTime;
        entity.progress++;
        entity.setChanged();

        if (entity.progress % 10 == 0 && level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.DRIPPING_HONEY,
                    pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, 1, 0.2, 0.0, 0.2, 0.0);
        }

        if (brewTypeId != null) {
            int quality = QualityAlgorithm.calculateQuality(entity.progress, perfectTime, 0);
            entity.inventory.set(1, ModItems.resolveBrewOutput(brewTypeId, quality));
        } else {
            entity.inventory.set(1, new ItemStack(ModItems.GRAPE_JUICE));
        }
    }

    @Override
    public void extractBrew() {
        ItemStack output = inventory.get(1);
        if (output.isEmpty() || progress == 0) return;

        inventory.set(0, ItemStack.EMPTY);
        inventory.set(1, ItemStack.EMPTY);
        progress = 0;
        setChanged();

        if (level instanceof ServerLevel sl) {
            sl.playSound(null, worldPosition, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
    }

    // --- Container ---
    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(inventory, slot, amount); setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == 0 && MachineInputRules.isPresserInput(stack); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0; }
    @Override public void clearContent() { inventory.clear(); }

    // --- WorldlyContainer (block hoppers) ---
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0, 1}; }
    @Override public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) { return false; }
    @Override public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) { return false; }

    // --- MenuProvider ---
    @Override
    public Component getDisplayName() { return Component.translatable("container.touhou_brews.presser"); }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new PresserMenu(syncId, playerInv, this, data);
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
