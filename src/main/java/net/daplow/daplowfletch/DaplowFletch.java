package net.daplow.daplowfletch;

import net.daplow.daplowfletch.block.ModBlocks;
import net.daplow.daplowfletch.data.FletchingBreakHandler;
import net.daplow.daplowfletch.entity.ModEntities;
import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.screen.ModScreenHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DaplowFletch implements ModInitializer {
	public static final String MOD_ID = "daplow-fletch";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("WAKE UP DAPLOW!!!");

		ModItems.register();

		ModBlocks.register();

		ModEntities.register();

		ModScreenHandler.register();

		FletchingBreakHandler.register();


	}
}