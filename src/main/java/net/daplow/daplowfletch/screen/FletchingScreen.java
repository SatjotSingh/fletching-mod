package net.daplow.daplowfletch.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FletchingScreen extends HandledScreen<FletchingScreenHandler> {

    public int ClientHeight = 0;
    public int ClientColor = 5000;

    private static final Identifier TEXTURE =
            new Identifier("daplow-fletch", "textures/gui/custom_fletching.png");

    public FletchingScreen(FletchingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }


    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.client.getTextureManager().bindTexture(TEXTURE);

        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, this.x, this.y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        int height = ClientHeight;
        int color = ClientColor;

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        RenderSystem.setShaderColor(r, g, b, 1.0F);

        drawTexture(
                matrices,
                x + 81,
                y + 19 + (18 - height),
                176,
                18 - height,
                18,
                height
        );


    }



    private void drawTitleText(MatrixStack matrices) {
        String title = "Fletching Table";
        int guiWidth = this.backgroundWidth;
        int textWidth = this.textRenderer.getWidth(title);
        int x = this.x + (guiWidth - textWidth) / 2;
        int y = this.y + 9;
        this.textRenderer.draw(matrices, title, x, y, 0x404040);
    }
    private void drawPlayerInventory(MatrixStack matrices, int x, int y) {
        this.client.getTextureManager().bindTexture(new Identifier("minecraft", "textures/gui/container/inventory.png"));
        DrawableHelper.drawTexture(matrices, x + 8, y + 4, 0, 0, 162, 54, 176, 54);


    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, delta);

        drawMouseoverTooltip(matrices, mouseX, mouseY);

        drawTitleText(matrices);
    }
}
