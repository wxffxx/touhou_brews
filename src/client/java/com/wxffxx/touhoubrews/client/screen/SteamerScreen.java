package com.wxffxx.touhoubrews.client.screen;

import com.wxffxx.touhoubrews.menu.SteamerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SteamerScreen extends AbstractContainerScreen<SteamerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("touhou_brews", "textures/gui/steamer.png");

    public SteamerScreen(SteamerMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Progress arrow (from texture at 176,14 → drawn at arrow position)
        int progress = menu.getScaledProgress();
        if (progress > 0) {
            graphics.blit(TEXTURE, x + 79, y + 35, 176, 14, progress, 17);
        }

        // Heat source flame icon (from texture at 176,0)
        if (menu.hasHeatSource()) {
            graphics.blit(TEXTURE, x + 56, y + 54, 176, 0, 14, 14);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        if (isHovering(56, 54, 14, 14, mouseX, mouseY)) {
            Component tooltip = menu.hasHeatSource()
                    ? Component.translatable("gui.touhou_brews.steamer.heat_ready")
                    : Component.translatable("gui.touhou_brews.steamer.heat_missing");
            graphics.renderTooltip(font, tooltip, mouseX, mouseY);
        } else if (menu.getProgress() > 0 && isHovering(79, 35, 24, 17, mouseX, mouseY)) {
            graphics.renderTooltip(font,
                    Component.translatable("gui.touhou_brews.progress", getProgressPercent()),
                    mouseX, mouseY);
        }
    }

    private boolean isHovering(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width
                && mouseY >= topPos + y && mouseY < topPos + y + height;
    }

    private int getProgressPercent() {
        int max = menu.getMaxProgress();
        return max > 0 ? menu.getProgress() * 100 / max : 0;
    }
}
