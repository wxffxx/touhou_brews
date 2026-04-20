package com.wxffxx.touhoubrews.block.entity;

import com.wxffxx.touhoubrews.item.BrewItem;
import com.wxffxx.touhoubrews.menu.AdminBrewMenu;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AdminBrewBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    private int quality = 5;
    private int typeIndex = 0;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> quality;
                case 1 -> typeIndex;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> quality = clampQuality(value);
                case 1 -> typeIndex = clampTypeIndex(value);
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public AdminBrewBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADMIN_BREW, pos, state);
    }

    public int getQuality() { return quality; }
    public void cycleQuality() {
        this.quality = clampQuality(this.quality + 1);
        sync();
    }

    public BrewItem.BrewType getBrewType() {
        return BrewItem.BrewType.values()[this.typeIndex];
    }

    public void cycleType() {
        this.typeIndex = clampTypeIndex(this.typeIndex + 1);
        sync();
    }

    public ItemStack createBrewStack() {
        return BrewItem.create(getTargetItem(), quality);
    }

    public Item getTargetItem() {
        return switch (getBrewType()) {
            case SAKE     -> ModItems.IBUKI_SAKE;
            case WINE     -> ModItems.getWineByQuality(this.quality);
            case UMESHU   -> ModItems.getUmeshuByQuality(this.quality);
            case AOMESHU  -> ModItems.getAomeshuByQuality(this.quality);
            case BEER     -> ModItems.getBeerByQuality(this.quality);
            case MIJIU    -> ModItems.MIJIU;
            case HUANGJIU -> ModItems.HUANGJIU;
            case MEAD     -> ModItems.MEAD;
            case BAIJIU   -> ModItems.BAIJIU;
            case CUSTOM_1 -> ModItems.getCustomByQuality(1, this.quality);
            case CUSTOM_2 -> ModItems.getCustomByQuality(2, this.quality);
            case CUSTOM_3 -> ModItems.getCustomByQuality(3, this.quality);
            case CUSTOM_4 -> ModItems.getCustomByQuality(4, this.quality);
            case CUSTOM_5 -> ModItems.getCustomByQuality(5, this.quality);
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.touhou_brews.admin_brew");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new AdminBrewMenu(syncId, playerInv, this, data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("AdminQuality", quality);
        tag.putInt("AdminType", typeIndex);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.quality = tag.contains("AdminQuality") ? clampQuality(tag.getInt("AdminQuality")) : 5;
        this.typeIndex = tag.contains("AdminType") ? clampTypeIndex(tag.getInt("AdminType")) : 0;
    }

    private void sync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private static int clampQuality(int value) {
        int normalized = value % 6;
        return normalized < 0 ? normalized + 6 : normalized;
    }

    private static int clampTypeIndex(int value) {
        int count = BrewItem.BrewType.values().length;
        int normalized = value % count;
        return normalized < 0 ? normalized + count : normalized;
    }
}
