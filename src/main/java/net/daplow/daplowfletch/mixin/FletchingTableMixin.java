package net.daplow.daplowfletch.mixin;

import net.daplow.daplowfletch.block.FletchingTableBlockEntity;
import net.daplow.daplowfletch.screen.FletchingScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FletchingTableBlock.class)
public abstract class FletchingTableMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void openCustomGui(BlockState state, World world, BlockPos pos,
                               PlayerEntity player, Hand hand, BlockHitResult hit,
                               CallbackInfoReturnable<ActionResult> cir) {

        if (world.isClient) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }


        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inv, playerEntity) ->
                        new FletchingScreenHandler(
                                syncId,
                                inv,
                                ScreenHandlerContext.create(world, pos)
                        ),
                Text.translatable("container.fletching")
        ));
        cir.setReturnValue(ActionResult.CONSUME);
    }
}