package net.daplow.daplowfletch.screen;

import net.daplow.daplowfletch.DaplowFletch;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModScreenHandler {

    public static ExtendedScreenHandlerType<FletchingScreenHandler> CUSTOM_FLETCHING;

    public static void register() {
        CUSTOM_FLETCHING = Registry.register(
                Registry.SCREEN_HANDLER,
                new Identifier(DaplowFletch.MOD_ID, "custom_fletching"),
                new ExtendedScreenHandlerType<>((syncId, inv, buf) ->
                        new FletchingScreenHandler(syncId, inv, buf.readBlockPos())
                )
        );
    }
}