package com.wxffxx.touhoubrews.registry;

import com.wxffxx.touhoubrews.TouhouBrews;
import com.wxffxx.touhoubrews.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<SteamerBlockEntity> STEAMER =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    new ResourceLocation(TouhouBrews.MOD_ID, "steamer"),
                    FabricBlockEntityTypeBuilder.create(SteamerBlockEntity::new,
                            ModBlocks.STEAMER).build());

    public static final BlockEntityType<KojiTrayBlockEntity> KOJI_TRAY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    new ResourceLocation(TouhouBrews.MOD_ID, "koji_tray"),
                    FabricBlockEntityTypeBuilder.create(KojiTrayBlockEntity::new,
                            ModBlocks.KOJI_TRAY).build());

    public static final BlockEntityType<FermentationBarrelBlockEntity> FERMENTATION_BARREL =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    new ResourceLocation(TouhouBrews.MOD_ID, "fermentation_barrel"),
                    FabricBlockEntityTypeBuilder.create(FermentationBarrelBlockEntity::new,
                            ModBlocks.FERMENTATION_BARREL).build());

    public static final BlockEntityType<PresserBlockEntity> PRESSER =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    new ResourceLocation(TouhouBrews.MOD_ID, "presser"),
                    FabricBlockEntityTypeBuilder.create(PresserBlockEntity::new,
                            ModBlocks.PRESSER).build());

    public static final BlockEntityType<InfusionJarBlockEntity> INFUSION_JAR =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    new ResourceLocation(TouhouBrews.MOD_ID, "infusion_jar"),
                    FabricBlockEntityTypeBuilder.create(InfusionJarBlockEntity::new,
                            ModBlocks.INFUSION_JAR).build());

    public static void registerBlockEntities() {
        TouhouBrews.LOGGER.info("Registering Block Entities for " + TouhouBrews.MOD_ID);
    }
}
