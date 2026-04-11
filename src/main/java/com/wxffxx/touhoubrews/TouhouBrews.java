package com.wxffxx.touhoubrews;

import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModBlocks;
import com.wxffxx.touhoubrews.registry.ModItemGroups;
import com.wxffxx.touhoubrews.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TouhouBrews implements ModInitializer {
	public static final String MOD_ID = "touhou_brews";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Touhou Brews (东方酒艺)...");

		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModBlockEntities.registerBlockEntities();
		ModItemGroups.registerItemGroups();
	}
}