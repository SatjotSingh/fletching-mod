package net.daplow.daplowfletch;

import net.daplow.daplowfletch.block.ModBlockEntities;
import net.daplow.daplowfletch.block.ModBlocks;
import net.daplow.daplowfletch.entity.ModEntities;
import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.screen.ModScreenHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaplowFletch implements ModInitializer {
	public static final String MOD_ID = "daplow-fletch";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("WAKE UP DAPLOW!!!");
		ModItems.registerItems();


		ModEntities.register();

		ModBlockEntities.register();
		ModScreenHandler.register();


	}
}