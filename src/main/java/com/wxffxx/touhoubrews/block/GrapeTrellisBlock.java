package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 葡萄架 (Grape Trellis) — 栅栏式结构，可自适应连接
 * age=0: 空架子
 * age=1: 幼苗藤蔓
 * age=2: 生长中的藤蔓
 * age=3: 成熟（挂满葡萄），右键收获
 *
 * 右键种子种上(0→1)，自然生长(1→2→3)，收获后重回 age=1
 */
public class GrapeTrellisBlock extends FenceBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public GrapeTrellisBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false)
                .setValue(WATERLOGGED, false).setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        int age = state.getValue(AGE);
        return age > 0 && age < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age > 0 && age < 3 && random.nextInt(5) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        int age = state.getValue(AGE);
        ItemStack held = player.getItemInHand(hand);

        // Plant grape seeds on empty trellis
        if (held.is(ModItems.GRAPE_SEEDS) && age == 0) {
            level.setBlock(pos, state.setValue(AGE, 1), 2);
            held.shrink(1);
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        // Harvest mature grapes
        if (age == 3) {
            int count = 1 + level.random.nextInt(3); // 1-3 grapes
            popResource(level, pos, new ItemStack(ModItems.GRAPES, count));
            // Small chance to also drop a seed
            if (level.random.nextFloat() < 0.3f) {
                popResource(level, pos, new ItemStack(ModItems.GRAPE_SEEDS, 1));
            }
            level.setBlock(pos, state.setValue(AGE, 1), 2);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                    SoundSource.BLOCKS, 1.0f, 0.8f + level.random.nextFloat() * 0.4f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
