package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class HopsCropBlock extends CropBlock {
    public HopsCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.HOPS_SEEDS;
    }
}
