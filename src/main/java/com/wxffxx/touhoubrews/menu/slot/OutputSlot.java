package com.wxffxx.touhoubrews.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OutputSlot extends Slot {
    public OutputSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(net.minecraft.world.entity.player.Player player, ItemStack stack) {
        if (this.container instanceof com.wxffxx.touhoubrews.block.entity.BrewingMachine m) {
            m.extractBrew();
        }
        super.onTake(player, stack);
    }
}
