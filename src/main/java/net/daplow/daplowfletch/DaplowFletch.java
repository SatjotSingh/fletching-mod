package net.daplow.daplowfletch;

import net.daplow.daplowfletch.block.ModBlocks;
import net.daplow.daplowfletch.data.FletchingBreakHandler;
import net.daplow.daplowfletch.other.entity.ModEntities;
import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.other.ModCommands;
import net.daplow.daplowfletch.other.recipes.FeatherRecipeLoader;
import net.daplow.daplowfletch.other.recipes.StemRecipeLoader;
import net.daplow.daplowfletch.screen.ModScreenHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DaplowFletch implements ModInitializer {
	public static final String MOD_ID = "daplow-fletch";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("WAKE UP DAPLOW!!!");

		ModItems.register();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.register(dispatcher);
		});

		ModBlocks.register();

		ModEntities.register();

		ModScreenHandler.register();

		FletchingBreakHandler.register();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new StemRecipeLoader());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new FeatherRecipeLoader());


	}
}