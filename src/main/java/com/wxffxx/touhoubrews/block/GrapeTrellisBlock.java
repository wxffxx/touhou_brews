package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    /** True when there's a solid block below OR no horizontal connections (standalone) */
    public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported");

    // Post: thin vertical support only
    private static final VoxelShape POST_SHAPE = Block.box(6, 0, 6, 10, 14, 10);
    // Horizontal beams extending to each side at top
    private static final VoxelShape NORTH_SHAPE = Block.box(5, 12, 0, 11, 14, 8);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5, 12, 8, 11, 14, 16);
    private static final VoxelShape WEST_SHAPE  = Block.box(0, 12, 5, 8, 14, 11);
    private static final VoxelShape EAST_SHAPE  = Block.box(8, 12, 5, 16, 14, 11);
    // Beam-only shape (no post, just a thin horizontal slab at top)
    private static final VoxelShape BEAM_ONLY_SHAPE = Block.box(5, 12, 5, 11, 14, 11);

    public GrapeTrellisBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false)
                .setValue(WATERLOGGED, false).setValue(AGE, 0)
                .setValue(SUPPORTED, true));
    }

    private boolean hasAnyConnection(BlockState state) {
        return state.getValue(NORTH) || state.getValue(SOUTH)
            || state.getValue(WEST) || state.getValue(EAST);
    }

    private boolean needsPost(BlockState state) {
        // Show post if: has block below (supported) OR standalone (no connections)
        return state.getValue(SUPPORTED) || !hasAnyConnection(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = needsPost(state) ? POST_SHAPE : BEAM_ONLY_SHAPE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(WEST))  shape = Shapes.or(shape, WEST_SHAPE);
        if (state.getValue(EAST))  shape = Shapes.or(shape, EAST_SHAPE);
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public boolean connectsTo(BlockState state, boolean isSideSolid, Direction direction) {
        return state.getBlock() instanceof GrapeTrellisBlock || super.connectsTo(state, isSideSolid, direction);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        // Update SUPPORTED when block below changes
        if (direction == Direction.DOWN) {
            boolean supported = !neighborState.isAir();
            updated = updated.setValue(SUPPORTED, supported);
        }
        // Also recalculate SUPPORTED for initial placement
        if (updated.hasProperty(SUPPORTED)) {
            BlockState below = level.getBlockState(pos.below());
            updated = updated.setValue(SUPPORTED, !below.isAir());
        }
        return updated;
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
            state = state.setValue(SUPPORTED, !below.isAir());
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE, SUPPORTED);
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
