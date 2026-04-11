package com.wxffxx.touhoubrews.menu;

import com.wxffxx.touhoubrews.menu.slot.FilteredSlot;
import com.wxffxx.touhoubrews.menu.slot.OutputSlot;
import com.wxffxx.touhoubrews.registry.ModMenuTypes;
import com.wxffxx.touhoubrews.util.MachineInputRules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KojiTrayMenu extends AbstractContainerMenu {
    private static final int MACHINE_SLOT_COUNT = 3;
    private static final int PLAYER_INV_START = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container container;
    private final ContainerData data;

    public KojiTrayMenu(int syncId, Inventory playerInv, FriendlyByteBuf buf) {
        this(syncId, playerInv, new SimpleContainer(3), new SimpleContainerData(3));
    }

    public KojiTrayMenu(int syncId, Inventory playerInv, Container container, ContainerData data) {
        super(ModMenuTypes.KOJI_TRAY, syncId);
        checkContainerSize(container, MACHINE_SLOT_COUNT);
        this.container = container;
        this.data = data;
        container.startOpen(playerInv.player);

        // Input 1: Steamed Rice
        this.addSlot(new FilteredSlot(container, 0, 38, 35, MachineInputRules::isKojiRiceInput));
        // Input 2: Koji Spores
        this.addSlot(new FilteredSlot(container, 1, 74, 35, MachineInputRules::isKojiSporesInput));
        // Output: Koji Rice
        this.addSlot(new OutputSlot(container, 2, 134, 35));

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getProgress() { return data.get(0); }
    public int getMaxProgress() { return data.get(1); }
    public boolean isDarkEnough() { return data.get(2) != 0; }

    public int getScaledProgress() {
        int max = getMaxProgress();
        return max > 0 ? getProgress() * 24 / max : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < MACHINE_SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) return ItemStack.EMPTY;
        } else {
            if (MachineInputRules.isKojiRiceInput(stack)) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else if (MachineInputRules.isKojiSporesInput(stack)) {
                if (!this.moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
            } else if (index < PLAYER_INV_END) {
                if (!this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) { return container.stillValid(player); }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
