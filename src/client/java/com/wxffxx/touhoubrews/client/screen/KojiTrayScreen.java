package com.wxffxx.touhoubrews.client.screen;

import com.wxffxx.touhoubrews.menu.KojiTrayMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KojiTrayScreen extends AbstractContainerScreen<KojiTrayMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("touhou_brews", "textures/gui/koji_tray.png");

    public KojiTrayScreen(KojiTrayMenu menu, Inventory playerInv, Component title) {
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
            graphics.blit(TEXTURE, x + 97, y + 35, 176, 14, progress, 17);
        }

        // Light level indicator: moon (dark=good) at 176,0 or sun (bright=bad) at 176,31
        if (menu.isDarkEnough()) {
            graphics.blit(TEXTURE, x + 74, y + 54, 176, 0, 14, 14); // moon icon
        } else {
            graphics.blit(TEXTURE, x + 74, y + 54, 176, 31, 14, 14); // sun warning icon
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
