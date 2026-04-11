package com.wxffxx.touhoubrews.registry;

import com.wxffxx.touhoubrews.TouhouBrews;
import com.wxffxx.touhoubrews.block.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ModBlocks {

    public static final Block RICE_CROP = registerBlockWithoutItem("rice_crop",
            new RiceCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));
    public static final Block GREEN_PLUM_CROP = registerBlockWithoutItem("green_plum_crop",
            new GreenPlumCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));

    public static final Block STEAMER = registerBlock("steamer",
            new SteamerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).noOcclusion()));

    public static final Block KOJI_TRAY = registerBlock("koji_tray",
            new KojiTrayBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).noOcclusion()));

    public static final Block FERMENTATION_BARREL = registerBlock("fermentation_barrel",
            new FermentationBarrelBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).noOcclusion()));

    public static final Block PRESSER = registerBlock("presser",
            new PresserBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).noOcclusion()));
    public static final Block INFUSION_JAR = registerBlock("infusion_jar",
            new InfusionJarBlock(FabricBlockSettings.copyOf(Blocks.GLASS).strength(0.8f).noOcclusion()));

    public static final Block GRAPE_TRELLIS = registerBlock("grape_trellis",
            new GrapeTrellisBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).noOcclusion()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(TouhouBrews.MOD_ID, name), block);
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(TouhouBrews.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(TouhouBrews.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        TouhouBrews.LOGGER.info("Registering ModBlocks for " + TouhouBrews.MOD_ID);
    }
}
