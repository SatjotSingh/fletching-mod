package net.daplow.daplowfletch.screen;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.block.FletchingTableBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class ModScreenHandler {

    public static ScreenHandlerType<FletchingScreenHandler> CUSTOM_FLETCHING;

    public static void register() {
        CUSTOM_FLETCHING = Registry.register(
                Registry.SCREEN_HANDLER,
                new Identifier(DaplowFletch.MOD_ID, "custom_fletching"),
                new ScreenHandlerType<>((syncId, inv) -> new FletchingScreenHandler(syncId, inv, ScreenHandlerContext.EMPTY))
        );
    }
}