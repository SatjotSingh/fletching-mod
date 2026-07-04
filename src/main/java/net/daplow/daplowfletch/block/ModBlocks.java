package net.daplow.daplowfletch.block;


import net.daplow.daplowfletch.DaplowFletch;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
