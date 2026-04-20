package com.wxffxx.touhoubrews;

import com.wxffxx.touhoubrews.command.ReloadCommand;
import com.wxffxx.touhoubrews.config.BrewConfigManager;
import com.wxffxx.touhoubrews.registry.ModBlockEntities;
import com.wxffxx.touhoubrews.registry.ModBlocks;
import com.wxffxx.touhoubrews.registry.ModItemGroups;
import com.wxffxx.touhoubrews.registry.ModItems;
import com.wxffxx.touhoubrews.registry.ModMenuTypes;
import com.wxffxx.touhoubrews.registry.ModRecipeSerializers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TouhouBrews implements ModInitializer {
	public static final String MOD_ID = "touhou_brews";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Touhou Brews (东方酒艺)...");

		// Load config first — must be before items apply effects
		BrewConfigManager.load();

		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModBlockEntities.registerBlockEntities();
		ModMenuTypes.register();
		ModRecipeSerializers.register();
		ModItemGroups.registerItemGroups();

		// Register /touhoubrews reload command
		CommandRegistrationCallback.EVENT.register(ReloadCommand::register);
	}
}
