package com.wxffxx.touhoubrews.menu;

import com.wxffxx.touhoubrews.block.entity.AdminBrewBlockEntity;
import com.wxffxx.touhoubrews.item.BrewItem;
import com.wxffxx.touhoubrews.registry.ModBlocks;
import com.wxffxx.touhoubrews.registry.ModItems;
import com.wxffxx.touhoubrews.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdminBrewMenu extends AbstractContainerMenu {
    public static final int BUTTON_CYCLE_TYPE = 0;
    public static final int BUTTON_CYCLE_QUALITY = 1;
    public static final int BUTTON_GENERATE = 2;

    private static final int DATA_COUNT = 2;
    private static final int PLAYER_INV_Y = 113;
    private static final int HOTBAR_Y = 171;

    private final ContainerData data;
    private final ContainerLevelAccess access;

    public AdminBrewMenu(int syncId, Inventory playerInv, FriendlyByteBuf buf) {
        this(syncId, playerInv, ContainerLevelAccess.NULL, new SimpleContainerData(DATA_COUNT));
    }

    public AdminBrewMenu(int syncId, Inventory playerInv, AdminBrewBlockEntity blockEntity, ContainerData data) {
        this(syncId, playerInv, ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), data);
    }

    private AdminBrewMenu(int syncId, Inventory playerInv, ContainerLevelAccess access, ContainerData data) {
        super(ModMenuTypes.ADMIN_BREW, syncId);
        checkContainerDataCount(data, DATA_COUNT);
        this.access = access;
        this.data = data;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, PLAYER_INV_Y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, HOTBAR_Y));
        }

        this.addDataSlots(data);
    }

    public int getQuality() {
        return data.get(0);
    }

    public BrewItem.BrewType getBrewType() {
        BrewItem.BrewType[] values = BrewItem.BrewType.values();
        int index = data.get(1);
        if (index < 0 || index >= values.length) {
            index = 0;
        }
        return values[index];
    }

    public ItemStack getPreviewStack() {
        if (getBrewType() == BrewItem.BrewType.BEER) {
            return new ItemStack(ModItems.getBeerByQuality(getQuality()));
        }
        if (getBrewType() == BrewItem.BrewType.WINE) {
            return new ItemStack(ModItems.getWineByQuality(getQuality()));
        }
        if (getBrewType() == BrewItem.BrewType.UMESHU) {
            return new ItemStack(ModItems.getUmeshuByQuality(getQuality()));
        }
        if (getBrewType() == BrewItem.BrewType.AOMESHU) {
            return new ItemStack(ModItems.getAomeshuByQuality(getQuality()));
        }
        return BrewItem.create(getTargetItem(), getQuality());
    }

    private Item getTargetItem() {
        return switch (getBrewType()) {
            case SAKE     -> ModItems.IBUKI_SAKE;
            case WINE     -> ModItems.getWineByQuality(getQuality());
            case UMESHU   -> ModItems.getUmeshuByQuality(getQuality());
            case AOMESHU  -> ModItems.getAomeshuByQuality(getQuality());
            case BEER     -> ModItems.getBeerByQuality(getQuality());
            case MIJIU    -> ModItems.MIJIU;
            case HUANGJIU -> ModItems.HUANGJIU;
            case MEAD     -> ModItems.MEAD;
            case BAIJIU   -> ModItems.BAIJIU;
            case CUSTOM_1 -> ModItems.getCustomByQuality(1, getQuality());
            case CUSTOM_2 -> ModItems.getCustomByQuality(2, getQuality());
            case CUSTOM_3 -> ModItems.getCustomByQuality(3, getQuality());
            case CUSTOM_4 -> ModItems.getCustomByQuality(4, getQuality());
            case CUSTOM_5 -> ModItems.getCustomByQuality(5, getQuality());
        };
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        return access.evaluate((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof AdminBrewBlockEntity admin)) {
                return false;
            }

            switch (id) {
                case BUTTON_CYCLE_TYPE -> admin.cycleType();
                case BUTTON_CYCLE_QUALITY -> admin.cycleQuality();
                case BUTTON_GENERATE -> {
                    ItemStack generated = admin.createBrewStack();
                    if (!player.addItem(generated)) {
                        player.drop(generated, false);
                    }
                }
                default -> {
                    return false;
                }
            }
            return true;
        }, false);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) ->
                        level.getBlockState(pos).is(ModBlocks.ADMIN_BREW)
                                && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D,
                true);
    }
}
