package net.daplow.daplowfletch.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void init(CallbackInfo info) {
		// This code is injected into the start of MinecraftServer.loadWorld()V



    /*To understand what public void openFurnaceGui does
    BlockState state → Current block data (like orientation).
    World world → The world the block is in (Overworld, Nether, etc.).
    BlockPos pos → Position of the block in the world.
    PlayerEntity player → The player who clicked.
    Hand hand → Whether they used main hand or off-hand.
    BlockHitResult hit → Info about where exactly on the block the player clicked.
    CallbackInfoReturnable<ActionResult> cir → Special Mixin object that lets us:
     */

    /*
    //Injects a onUse method to generate a function called "openFurnaceGui"
    //the "HEAD" means that this injection injects at the start of the fletching table code.
    //The cancellable Chatgpt: Allows us to stop the original method from running if we want (so the Fletching Table GUI doesn’t open).
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    public void openFurnaceGui(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {

        //Check if on Server, then check if player is a server side player,,,
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            // Open Furnace GUI
            serverPlayer.openHandledScreen(
                    //Gemeratses GUI?
                    new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, playerEntity) ->
                                    new FletchingScreenHandler(syncId, inventory),
                            Text.literal("Fletching Table")
                    )
            );
            cir.setReturnValue(ActionResult.SUCCESS); // Cancel normal Fletching Table behavior
        }
    }

     */
	}
}