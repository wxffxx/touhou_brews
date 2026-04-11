package com.wxffxx.touhoubrews.block;

import com.wxffxx.touhoubrews.registry.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class RiceCropBlock extends CropBlock {
    public RiceCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.RICE_SEEDS;
    }
}
