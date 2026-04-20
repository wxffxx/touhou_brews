package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.block.entity.AdminBrewBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AdminBrewBlock extends BaseEntityBlock {
    public AdminBrewBlock(Properties properties) { super(properties); }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    
    @Nullable @Override 
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { 
        return new AdminBrewBlockEntity(pos, state); 
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AdminBrewBlockEntity admin) {
            player.openMenu(admin);
        }
        return InteractionResult.CONSUME;
    }
}
