package com.wxffxx.touhoubrews.client.screen;

import com.wxffxx.touhoubrews.item.BrewItem;
import com.wxffxx.touhoubrews.menu.AdminBrewMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AdminBrewScreen extends AbstractContainerScreen<AdminBrewMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("touhou_brews", "textures/gui/admin_brew.png");
    private static final Component TYPE_BUTTON =
            Component.translatable("gui.touhou_brews.admin_brew.type_button");
    private static final Component QUALITY_BUTTON =
            Component.translatable("gui.touhou_brews.admin_brew.quality_button");
    private static final Component GENERATE_BUTTON =
            Component.translatable("gui.touhou_brews.admin_brew.generate_button");

    public AdminBrewScreen(AdminBrewMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageHeight = 196;
        this.inventoryLabelY = 106;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 6;

        addRenderableWidget(Button.builder(TYPE_BUTTON, button -> sendButton(AdminBrewMenu.BUTTON_CYCLE_TYPE))
                .bounds(leftPos + 16, topPos + 20, 64, 20)
                .build());

        addRenderableWidget(Button.builder(QUALITY_BUTTON, button -> sendButton(AdminBrewMenu.BUTTON_CYCLE_QUALITY))
                .bounds(leftPos + 96, topPos + 20, 64, 20)
                .build());

        addRenderableWidget(Button.builder(GENERATE_BUTTON, button -> sendButton(AdminBrewMenu.BUTTON_GENERATE))
                .bounds(leftPos + 40, topPos + 50, 96, 20)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);

        Component summary = Component.translatable(
                "gui.touhou_brews.admin_brew.summary",
                getTypeLabel(),
                getQualityStars()
        );
        graphics.drawString(font, summary, 36, 81, 0x303030, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        ItemStack preview = menu.getPreviewStack();
        int previewX = leftPos + 16;
        int previewY = topPos + 77;
        graphics.renderItem(preview, previewX, previewY);
        graphics.renderItemDecorations(font, preview, previewX, previewY);

        renderTooltip(graphics, mouseX, mouseY);
        if (mouseX >= previewX && mouseX < previewX + 16 && mouseY >= previewY && mouseY < previewY + 16) {
            graphics.renderTooltip(font, preview, mouseX, mouseY);
        }
    }

    private void sendButton(int buttonId) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
        }
    }

    private Component getTypeLabel() {
        return switch (menu.getBrewType()) {
            case SAKE     -> Component.translatable("gui.touhou_brews.admin_brew.type.sake");
            case WINE     -> Component.translatable("gui.touhou_brews.admin_brew.type.wine");
            case UMESHU   -> Component.translatable("gui.touhou_brews.admin_brew.type.umeshu");
            case AOMESHU  -> Component.translatable("gui.touhou_brews.admin_brew.type.aomeshu");
            case BEER     -> Component.translatable("gui.touhou_brews.admin_brew.type.beer");
            case MIJIU    -> Component.translatable("gui.touhou_brews.admin_brew.type.mijiu");
            case HUANGJIU -> Component.translatable("gui.touhou_brews.admin_brew.type.huangjiu");
            case MEAD     -> Component.translatable("gui.touhou_brews.admin_brew.type.mead");
            case BAIJIU   -> Component.translatable("gui.touhou_brews.admin_brew.type.baijiu");
            case CUSTOM_1 -> Component.literal("Custom 1");
            case CUSTOM_2 -> Component.literal("Custom 2");
            case CUSTOM_3 -> Component.literal("Custom 3");
            case CUSTOM_4 -> Component.literal("Custom 4");
            case CUSTOM_5 -> Component.literal("Custom 5");
        };
    }

    private Component getQualityStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stars.append(i < menu.getQuality() ? "★" : "☆");
        }
        return Component.literal(stars.toString()).withStyle(BrewItem.getQualityStyle(menu.getQuality()));
    }
}
