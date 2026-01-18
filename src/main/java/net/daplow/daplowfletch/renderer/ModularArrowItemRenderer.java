package net.daplow.daplowfletch.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class ModularArrowItemRenderer implements BuiltinItemRenderer {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    @Override
    public void render(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        ItemRenderer itemRenderer = MC.getItemRenderer();

        // -----------------------
        // 1️⃣ World / Hand mode
        // -----------------------
        ModelTransformation.Mode mode = ModelTransformation.Mode.FIXED;

        // Base arrow
        renderLayer(stack, "daplow-fletch:item/op_arrow_stem", matrices, vertexConsumers, light, overlay, mode);

        // Stem layer
        if (stack.getOrCreateNbt().getBoolean("stem")) {
            renderLayer(stack, "daplow-fletch:item/op_arrow_stem", matrices, vertexConsumers, light, overlay, mode);
        }

        // Tip layer
        if (stack.getOrCreateNbt().getBoolean("potion")) {
            renderLayer(stack, "daplow-fletch:item/arrow_tip_potion", matrices, vertexConsumers, light, overlay, mode);
        }

        // Payload / addon layer
        String payload = stack.getOrCreateNbt().getString("HasAddon");
        if (!payload.isEmpty()) {
            renderLayer(stack, "daplow-fletch:item/arrow_tip_stem", matrices, vertexConsumers, light, overlay, mode);
        }

        matrices.pop();
    }
    private void renderOverlay(ItemStack stack, String textureId, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int light, int overlay,
                               ModelTransformation.Mode mode) {
        var model = MC.getBakedModelManager().getModel((ModelIdentifier) new Identifier(textureId));
        MC.getItemRenderer().renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, model);
    }
    private void renderLayer(ItemStack stack, String textureId,
                             MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                             int light, int overlay, ModelTransformation.Mode mode) {
        var model = MC.getBakedModelManager().getModel((ModelIdentifier) new Identifier(textureId));
        MC.getItemRenderer().renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, model);
    }
}