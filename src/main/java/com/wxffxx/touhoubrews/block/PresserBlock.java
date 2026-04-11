package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.block.entity.PresserBlockEntity;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PresserBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PresserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) { return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PresserBlockEntity(pos, state); }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.PRESSER, PresserBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PresserBlockEntity presser)) return InteractionResult.PASS;
        ItemStack heldStack = player.getItemInHand(hand);
        var inv = presser.getInventory();

        if ((heldStack.is(ModItems.SAKE_MASH) || heldStack.is(ModItems.GRAPES)) && inv.get(0).isEmpty()) {
            inv.set(0, heldStack.copyWithCount(1)); heldStack.shrink(1); presser.setChanged();
            player.displayClientMessage(Component.literal("Pressing...").withStyle(ChatFormatting.GOLD), true);
            return InteractionResult.SUCCESS;
        }
        if (heldStack.isEmpty()) {
            if (!inv.get(1).isEmpty()) { player.getInventory().add(inv.get(1).copy()); inv.set(1, ItemStack.EMPTY); presser.setChanged(); return InteractionResult.SUCCESS; }
            if (!inv.get(0).isEmpty()) { player.getInventory().add(inv.get(0).copy()); inv.set(0, ItemStack.EMPTY); presser.setChanged(); return InteractionResult.SUCCESS; }
            int pct = (int) ((float) presser.getProgress() / presser.getMaxProgress() * 100);
            if (pct > 0) player.displayClientMessage(Component.literal("Pressing... " + pct + "%").withStyle(ChatFormatting.GOLD), true);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PresserBlockEntity presser) net.minecraft.world.Containers.dropContents(level, pos, presser.getInventory());
            super.onRemove(state, level, pos, newState, moved);
        }
    }
}
