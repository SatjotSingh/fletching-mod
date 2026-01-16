package net.daplow.daplowfletch.mixin;

import io.netty.buffer.Unpooled;
import net.daplow.daplowfletch.block.FletchingTableBlockEntity;
import net.daplow.daplowfletch.data.FletchingPotionData;
import net.daplow.daplowfletch.data.FletchingTableWorldData;
import net.daplow.daplowfletch.network.ModPackets;
import net.daplow.daplowfletch.screen.FletchingScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.server.world.ServerWorld;
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

    @Inject(method = "onUse", at = @At("RETURN"), cancellable = true)
    private void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeBlockPos(pos);


                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable(" ");
                }

                @Override
                public FletchingScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity playerEntity) {
                    System.out.println("Yo generated");

                    FletchingScreenHandler handler = new FletchingScreenHandler(syncId, inv, pos);


                    handler.loadPotionDataServer();
                    handler.syncToClient();
                    return handler;

                }
            });
        }
    }
}