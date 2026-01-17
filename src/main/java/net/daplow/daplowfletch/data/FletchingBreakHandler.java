package net.daplow.daplowfletch.data;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;

public class FletchingBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient()) return;
            if (state.getBlock() instanceof FletchingTableBlock) {
                FletchingTableWorldData worldData = FletchingTableWorldData.get((ServerWorld) world);
                worldData.remove(pos);
                worldData.markDirtyForSave();
            }
        });
    }
}