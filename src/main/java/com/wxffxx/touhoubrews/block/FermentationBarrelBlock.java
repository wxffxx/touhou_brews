package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.block.entity.FermentationBarrelBlockEntity;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FermentationBarrelBlock extends BaseEntityBlock {
    public FermentationBarrelBlock(Properties properties) { super(properties); }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FermentationBarrelBlockEntity(pos, state); }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.FERMENTATION_BARREL, FermentationBarrelBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FermentationBarrelBlockEntity barrel)) return InteractionResult.PASS;
        ItemStack heldStack = player.getItemInHand(hand);
        var inv = barrel.getInventory();

        if ((heldStack.is(ModItems.KOJI_RICE) || heldStack.is(ModItems.GRAPE_JUICE)) && inv.get(0).isEmpty()) {
            inv.set(0, heldStack.copyWithCount(1)); heldStack.shrink(1); barrel.setChanged();
            String name = heldStack.isEmpty() ? "ingredient" : heldStack.getItem().toString();
            player.displayClientMessage(Component.literal("+ Added").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        if (heldStack.is(ModItems.STEAMED_RICE) && inv.get(1).isEmpty()) {
            inv.set(1, new ItemStack(ModItems.STEAMED_RICE)); heldStack.shrink(1); barrel.setChanged();
            player.displayClientMessage(Component.literal("+ Steamed Rice").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        if (heldStack.is(Items.POTION) && inv.get(2).isEmpty()) {
            inv.set(2, heldStack.copy()); heldStack.shrink(1); barrel.setChanged();
            player.displayClientMessage(Component.literal("+ Water").withStyle(ChatFormatting.AQUA), true);
            return InteractionResult.SUCCESS;
        }
        if (heldStack.isEmpty()) {
            if (!inv.get(3).isEmpty()) { player.getInventory().add(inv.get(3).copy()); inv.set(3, ItemStack.EMPTY); barrel.setChanged(); return InteractionResult.SUCCESS; }
            if (!inv.get(2).isEmpty() && inv.get(2).is(Items.GLASS_BOTTLE)) { player.getInventory().add(inv.get(2).copy()); inv.set(2, ItemStack.EMPTY); barrel.setChanged(); return InteractionResult.SUCCESS; }
            for (int i = 0; i <= 2; i++) { if (!inv.get(i).isEmpty()) { player.getInventory().add(inv.get(i).copy()); inv.set(i, ItemStack.EMPTY); barrel.setChanged(); return InteractionResult.SUCCESS; } }
            int pct = (int) ((float) barrel.getProgress() / barrel.getMaxProgress() * 100);
            if (pct > 0) player.displayClientMessage(Component.literal("Fermenting... " + pct + "%").withStyle(ChatFormatting.GOLD), true);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FermentationBarrelBlockEntity barrel) net.minecraft.world.Containers.dropContents(level, pos, barrel.getInventory());
            super.onRemove(state, level, pos, newState, moved);
        }
    }
}
