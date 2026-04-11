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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class FermentationBarrelMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public FermentationBarrelMenu(int syncId, Inventory playerInv, FriendlyByteBuf buf) {
        this(syncId, playerInv, new SimpleContainer(4), new SimpleContainerData(2));
    }

    public FermentationBarrelMenu(int syncId, Inventory playerInv, Container container, ContainerData data) {
        super(ModMenuTypes.FERMENTATION_BARREL, syncId);
        this.container = container;
        this.data = data;

        // Slot 0: Primary (koji rice / grape juice)
        this.addSlot(new Slot(container, 0, 32, 25));
        // Slot 1: Secondary (steamed rice)
        this.addSlot(new Slot(container, 1, 32, 47));
        // Slot 2: Water (potion)
        this.addSlot(new Slot(container, 2, 56, 36));
        // Slot 3: Output
        this.addSlot(new ResultSlot(container, 3, 124, 35));

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

        if (index < 4) {
            if (!this.moveItemStackTo(stack, 4, 40, true)) return ItemStack.EMPTY;
        } else {
            if (stack.is(ModItems.KOJI_RICE) || stack.is(ModItems.GRAPE_JUICE)) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else if (stack.is(ModItems.STEAMED_RICE)) {
                if (!this.moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
            } else if (stack.is(Items.POTION)) {
                if (!this.moveItemStackTo(stack, 2, 3, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) { return container.stillValid(player); }

    private static class ResultSlot extends Slot {
        public ResultSlot(Container container, int index, int x, int y) { super(container, index, x, y); }
        @Override public boolean mayPlace(ItemStack stack) { return false; }
    }
}
