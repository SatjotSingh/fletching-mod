package net.daplow.daplowfletch.block;


import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.DaplowFletchClient;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;


public class ModBlocks {

    public static final Block ARROW_POWER_BLOCK =
            new ArrowPowerBlock(AbstractBlock.Settings.of(Material.AIR)
                    .noCollision()
                    .breakInstantly()
                    .dropsNothing());
    public static void register() {
        DaplowFletch.LOGGER.debug("Registering Mod Block for " + DaplowFletch.MOD_ID);
        Registry.register(Registry.BLOCK, new Identifier("daplow-fletch", "arrow_power_block"), ARROW_POWER_BLOCK);

    }

}
