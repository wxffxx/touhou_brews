package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.menu.KojiTrayMenu;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KojiTrayBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Container {
    private static final int PROCESS_TIME = 600;
    private static final int MAX_LIGHT_LEVEL = 7;
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME;
                case 2 -> isDarkEnough() ? 1 : 0;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            if (index == 0) progress = value;
        }
        @Override public int getCount() { return 3; }
    };

    public KojiTrayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KOJI_TRAY, pos, state);
    }

    private boolean isDarkEnough() {
        return level != null && level.getMaxLocalRawBrightness(worldPosition.above()) <= MAX_LIGHT_LEVEL;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KojiTrayBlockEntity entity) {
        if (level.isClientSide) return;
        ItemStack steamedRice = entity.inventory.get(0);
        ItemStack kojiSpores = entity.inventory.get(1);
        ItemStack output = entity.inventory.get(2);

        boolean hasInputs = !steamedRice.isEmpty() && MachineInputRules.isKojiRiceInput(steamedRice)
                && !kojiSpores.isEmpty() && MachineInputRules.isKojiSporesInput(kojiSpores);
        boolean outputFree = output.isEmpty()
                || (output.is(ModItems.KOJI_RICE) && output.getCount() < output.getMaxStackSize());
        if (!hasInputs || !outputFree) { entity.resetProgress(); return; }

        if (!entity.isDarkEnough()) { entity.resetProgress(); return; }

        entity.progress++;
        entity.setChanged();
        if (entity.progress % 40 == 0 && level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                    pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 2, 0.3, 0.1, 0.3, 0.0);
        }
        if (entity.progress >= PROCESS_TIME) {
            entity.progress = 0;
            steamedRice.shrink(1); kojiSpores.shrink(1);
            if (output.isEmpty()) entity.inventory.set(2, new ItemStack(ModItems.KOJI_RICE));
            else output.grow(1);
            entity.setChanged();
            if (level instanceof ServerLevel sl) sl.playSound(null, pos, SoundEvents.MOSS_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);
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
            case 0 -> MachineInputRules.isKojiRiceInput(stack);
            case 1 -> MachineInputRules.isKojiSporesInput(stack);
            default -> false;
        };
    }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0; }
    @Override public void clearContent() { inventory.clear(); }

    // --- MenuProvider ---
    @Override
    public Component getDisplayName() { return Component.translatable("container.touhou_brews.koji_tray"); }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new KojiTrayMenu(syncId, playerInv, this, data);
    }

    public NonNullList<ItemStack> getInventory() { return inventory; }

    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, inventory); tag.putInt("Progress", progress); }
    @Override public void load(CompoundTag tag) { super.load(tag); inventory.clear(); ContainerHelper.loadAllItems(tag, inventory); progress = tag.getInt("Progress"); }
    @Nullable @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

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
