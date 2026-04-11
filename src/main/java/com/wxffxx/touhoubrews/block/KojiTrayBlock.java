package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.block.entity.KojiTrayBlockEntity;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KojiTrayBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 4, 15);

    public KojiTrayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KojiTrayBlockEntity(pos, state);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.KOJI_TRAY, KojiTrayBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof KojiTrayBlockEntity tray)) return InteractionResult.PASS;

        ItemStack heldStack = player.getItemInHand(hand);
        var inv = tray.getInventory();

        if (heldStack.is(ModItems.STEAMED_RICE) && inv.get(0).isEmpty()) {
            inv.set(0, new ItemStack(ModItems.STEAMED_RICE));
            heldStack.shrink(1); tray.setChanged();
            return InteractionResult.SUCCESS;
        }
        if (heldStack.is(ModItems.KOJI_SPORES) && inv.get(1).isEmpty()) {
            inv.set(1, new ItemStack(ModItems.KOJI_SPORES));
            heldStack.shrink(1); tray.setChanged();
            return InteractionResult.SUCCESS;
        }
        if (heldStack.isEmpty()) {
            for (int i = 2; i >= 0; i--) {
                if (!inv.get(i).isEmpty()) {
                    player.getInventory().add(inv.get(i).copy());
                    inv.set(i, ItemStack.EMPTY); tray.setChanged();
                    return InteractionResult.SUCCESS;
                }
            }
        }

        int lightLevel = level.getMaxLocalRawBrightness(pos.above());
        if (lightLevel > 7) {
            player.displayClientMessage(Component.literal("⚠ Too bright! Koji needs darkness (light ≤ 7)")
                    .withStyle(ChatFormatting.RED), true);
        } else if (tray.getProgress() > 0) {
            int pct = (int) ((float) tray.getProgress() / tray.getMaxProgress() * 100);
            player.displayClientMessage(Component.literal("Cultivating... " + pct + "%")
                    .withStyle(ChatFormatting.GREEN), true);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof KojiTrayBlockEntity tray) {
                net.minecraft.world.Containers.dropContents(level, pos, tray.getInventory());
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }
}
