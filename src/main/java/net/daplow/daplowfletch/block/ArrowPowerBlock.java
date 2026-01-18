package net.daplow.daplowfletch.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@SuppressWarnings("deprecation")
public class ArrowPowerBlock extends Block {

    public ArrowPowerBlock(Settings settings) {
        super(Block.Settings.of(Material.AIR).noCollision().strength(0.0f) .breakInstantly()  .dropsNothing());

    }

    // Emit strong redstone power
    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, net.minecraft.util.math.BlockPos pos, Direction direction) {
        return 15; // full signal
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, net.minecraft.util.math.BlockPos pos, Direction direction) {
        return 15; // full signal
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }
}
