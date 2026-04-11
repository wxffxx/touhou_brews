package com.wxffxx.touhoubrews.client.screen;

import com.wxffxx.touhoubrews.menu.FermentationBarrelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FermentationBarrelScreen extends AbstractContainerScreen<FermentationBarrelMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("touhou_brews", "textures/gui/fermentation_barrel.png");

    public FermentationBarrelScreen(FermentationBarrelMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Progress arrow
        int progress = menu.getScaledProgress();
        if (progress > 0) {
            graphics.blit(TEXTURE, x + 85, y + 35, 176, 14, progress, 17);
        }

        // Bubble animation (frame based on tick)
        int bubbleFrame = (int) (System.currentTimeMillis() / 500 % 4);
        if (menu.getProgress() > 0) {
            int bubbleHeight = 4 + bubbleFrame * 3;
            graphics.blit(TEXTURE, x + 73, y + 52 - bubbleHeight, 176, 31, 8, bubbleHeight);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
