package net.daplow.daplowfletch;

import net.daplow.daplowfletch.block.ModBlocks;
import net.daplow.daplowfletch.entity.ModEntities;
import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.screen.FletchingScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.daplow.daplowfletch.screen.FletchingScreen;
import net.daplow.daplowfletch.screen.ModScreenHandler;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.daplow.daplowfletch.network.ModPackets.*;

@SuppressWarnings("deprecation")
public class DaplowFletchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        HandledScreens.register(ModScreenHandler.CUSTOM_FLETCHING,
                (FletchingScreenHandler handler, PlayerInventory inv, Text title) ->
                        new FletchingScreen(handler, inv, title)
        );
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ARROW_POWER_BLOCK, RenderLayer.getTranslucent());



        ClientPlayNetworking.registerGlobalReceiver(POTION_INFO, (client, handler, buf, responseSender) -> {
            int height = buf.readInt();
            int color = buf.readInt();
            client.execute(() -> {
                if (client.currentScreen instanceof FletchingScreen screen) {
                    screen.ClientHeight = height; // update client-side variable
                    screen.ClientColor = color; // update client-side variable
                }
            });
        });





        for (int i = 0; i < 6; i++) {
            final int bit = i;
            ModelPredicateProviderRegistry.register(
                    ModItems.OP_ARROW,
                    new Identifier("b" + bit),
                    (stack, world, entity, seed) -> {
                        int index = 0;
                        if (stack.hasNbt() && stack.getNbt().contains("index")) {
                            index = stack.getNbt().getInt("index"); // 0-39
                        } else {

                        }
                        // Return 1.0 if the bit is set, 0.0 otherwise
                        return (index >> bit) & 1;
                    }
            );
        }
        FabricModelPredicateProviderRegistry.register(
                ModItems.OP_ARROW,
                new Identifier("corrupt"),
                (stack, world, entity, seed) -> {

                    if (stack == null || !stack.hasNbt()) {
                        return 1.0f; // corrupt
                    }

                    var nbt = stack.getNbt();

                    if (nbt == null || !nbt.contains("index")) {
                        return 1.0f; // corrupt
                    }

                    int index = nbt.getInt("index");

                    // Optional validation range
                    if (index < 0 || index > 999) {
                        return 1.0f; // corrupt
                    }

                    return 0.0f; // valid
                }
        );


        ModelPredicateProviderRegistry.register(
                ModItems.OP_ARROW,
                new Identifier("daplowfletch", "stem"),
                (stack, world, entity, seed) ->
                        stack.hasNbt() && stack.getNbt().contains("stem") ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.OP_ARROW,
                new Identifier("daplowfletch", "potion_stem"),
                (stack, world, entity, seed) -> {
                    if (!stack.hasNbt()) return 0f;
                    boolean hasPotion = stack.getNbt().getBoolean("potion");
                    boolean hasStem = stack.getNbt().contains("stem_variant");
                    return (hasPotion && hasStem) ? 1f : 0f;
                }
        );

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 3) { // potion layer
                if (!stack.hasNbt() || !stack.getNbt().contains("Potion")) return 0xFFFFFF;
                return PotionUtil.getColor(stack);
            }

            if (tintIndex == 1) { // stem layer
                if (!stack.hasNbt()) return 0xFFFFFF;
                String stem = stack.getNbt().getString("stem_variant");
                if ("gold".equals(stem)) return 0xFFD700;
                if ("iron".equals(stem)) return 0xBEBEBE;
                return 0xFFFFFF;
            }

            return 0xFFFFFF; // base arrow
        }, ModItems.OP_ARROW);


        EntityRendererRegistry.register(ModEntities.OP_ARROW_ENTITY, ArrowEntityRenderer::new);




        /*ModelPredicateProviderRegistry.register(ModItems.OP_ARROW, new Identifier("stem"), (stack, world, entity, seed) ->
                stack.getOrCreateNbt().getBoolean("stem") ? 1f : 0f
        );
                ModelLoadingRegistry.INSTANCE.registerModelModifier((manager, modelId, model) -> {
            if (modelId.getPath().equals("op_arrow")) {
                return new OpArrowBakedModel(model);
            }
            return model;
        });
                BuiltinItemRendererRegistry.INSTANCE.register(ModItems.OP_ARROW, new OpArrowRenderer());

        ModelPredicateProviderRegistry.register(ModItems.OP_ARROW, new Identifier("potion"), (stack, world, entity, seed) ->
                stack.getOrCreateNbt().getBoolean("potion") ? 1f : 0f
        );

                ModelPredicateProviderRegistry.register(
                ModItems.OP_ARROW,
                new Identifier("daplowfletch", "potion"),
                (stack, world, entity, seed) ->
                        stack.hasNbt() && stack.getNbt().contains("potion") ? 1.0F : 0.0F
        );
        */

    }
}