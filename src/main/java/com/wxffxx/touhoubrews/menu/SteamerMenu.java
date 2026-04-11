package com.wxffxx.touhoubrews.menu;

import com.wxffxx.touhoubrews.registry.ModMenuTypes;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class SteamerMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    // Client constructor
    public SteamerMenu(int syncId, Inventory playerInv, FriendlyByteBuf buf) {
        this(syncId, playerInv, new SimpleContainer(2), new SimpleContainerData(3));
    }

    // Server constructor
    public SteamerMenu(int syncId, Inventory playerInv, Container container, ContainerData data) {
        super(ModMenuTypes.STEAMER, syncId);
        this.container = container;
        this.data = data;

        // Machine slots
        this.addSlot(new Slot(container, 0, 56, 35));          // Input
        this.addSlot(new ResultSlot(container, 1, 116, 35));   // Output

        // Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getProgress() { return data.get(0); }
    public int getMaxProgress() { return data.get(1); }
    public boolean hasHeatSource() { return data.get(2) != 0; }

    public int getScaledProgress() {
        int max = getMaxProgress();
        return max > 0 ? getProgress() * 24 / max : 0; // 24px arrow width
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 2) {
            // Machine → Player
            if (!this.moveItemStackTo(stack, 2, 38, true)) return ItemStack.EMPTY;
        } else {
            // Player → Machine input only
            if (stack.is(ModItems.RICE)) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    // Output-only slot
    private static class ResultSlot extends Slot {
        public ResultSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }
        @Override
        public boolean mayPlace(ItemStack stack) { return false; }
    }
}
