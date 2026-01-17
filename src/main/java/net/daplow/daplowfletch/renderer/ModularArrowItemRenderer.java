package net.daplow.daplowfletch.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class ModularArrowItemRenderer implements BuiltinItemRenderer {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    @Override
    public void render(
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        ItemRenderer itemRenderer = MC.getItemRenderer();
        matrices.push();

        // Stem
        if (stack.getOrCreateNbt().getBoolean("stem")) {
            renderLayer(itemRenderer, "op_arrow_stem", matrices, vertexConsumers, light, overlay);
        }

        // Tip/Potion
        if (stack.getOrCreateNbt().getBoolean("potion")) {
            renderLayer(itemRenderer, "op_arrow_potion", matrices, vertexConsumers, light, overlay);
        }

        // Payload
        String payload = stack.getOrCreateNbt().getString("HasAddon");
        if (!payload.isEmpty()) {
            renderLayer(itemRenderer, "op_arrow_addon_" + payload, matrices, vertexConsumers, light, overlay);
        }

        matrices.pop();
    }

    private void renderLayer(
            ItemRenderer itemRenderer,
            String modelName,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        // Create a dummy ItemStack using CustomModelData to point to the model JSON
        ItemStack temp = new ItemStack(Items.STICK);
        temp.getOrCreateNbt().putInt("CustomModelData", getCustomModelData(modelName));

        // Render the model
        itemRenderer.renderItem(
                temp,                        // ItemStack
                ModelTransformation.Mode.GUI,// Transformation type
                light,                       // int light
                overlay,                     // int overlay
                matrices,                     // MatrixStack
                vertexConsumers,              // VertexConsumerProvider
                0                             // seed (can just pass 0)
        );
    }

    private int getCustomModelData(String modelName) {
        // Map model name to the integer you set in your JSON overrides
        // Example: in your resource pack, overrides look like:
        // "predicate": { "custom_model_data": 1 }, "model": "daplow-fletch:item/op_arrow_stem"
        return switch (modelName) {
            case "op_arrow_stem" -> 1;
            case "op_arrow_potion" -> 2;
            case "op_arrow_addon_tnt" -> 3;
            default -> 0;
        };
    }
}