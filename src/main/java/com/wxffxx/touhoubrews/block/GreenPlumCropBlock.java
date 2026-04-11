package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class GreenPlumCropBlock extends CropBlock {
    public GreenPlumCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.GREEN_PLUM_SEEDS;
    }
}
