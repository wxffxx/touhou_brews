import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GenerateGuiTextures {

    static final Color BG_BORDER = new Color(198, 198, 198);
    static final Color INNER_AREA = new Color(139, 139, 139);
    static final Color SLOT_BG = new Color(55, 55, 55);
    static final Color SLOT_BORDER = new Color(139, 139, 139);
    static final Color ARROW_COLOR = new Color(110, 110, 110);
    static final Color WHITE_HIGHLIGHT = new Color(255, 255, 255);
    static final Color DARK_EDGE = new Color(55, 55, 55);
    static final Color DARKER_EDGE = new Color(30, 30, 30);
    static final Color TITLE_COLOR = new Color(64, 64, 64);

    static final String OUTPUT_DIR = "src/main/resources/assets/touhou_brews/textures/gui/";

    // Standard MC GUI dimensions
    static final int GUI_W = 176;
    static final int GUI_H = 166;

    // Player inventory position (standard)
    static final int INV_X = 7;
    static final int INV_Y = 83;
    static final int HOTBAR_Y = 141;

    public static void main(String[] args) throws Exception {
        new File(OUTPUT_DIR).mkdirs();

        generateSteamer();
        generateKojiTray();
        generateFermentationBarrel();
        generatePresser();

        System.out.println("All 4 GUI textures generated successfully.");
    }

    // ---- Draw Helpers ----

    static BufferedImage createBase() {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        // Transparent background
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 256, 256);
        g.setComposite(AlphaComposite.SrcOver);
        g.dispose();
        return img;
    }

    static void drawGuiBackground(Graphics2D g) {
        // Outer border - beveled MC style
        // Top-left highlight
        g.setColor(WHITE_HIGHLIGHT);
        g.fillRect(0, 0, GUI_W, 1);       // top edge
        g.fillRect(0, 0, 1, GUI_H);       // left edge
        // Bottom-right shadow
        g.setColor(DARK_EDGE);
        g.fillRect(0, GUI_H - 1, GUI_W, 1);  // bottom edge
        g.fillRect(GUI_W - 1, 0, 1, GUI_H);  // right edge

        // Inner border (1px in)
        g.setColor(new Color(220, 220, 220));
        g.fillRect(1, 1, GUI_W - 2, 1);
        g.fillRect(1, 1, 1, GUI_H - 2);
        g.setColor(new Color(85, 85, 85));
        g.fillRect(1, GUI_H - 2, GUI_W - 2, 1);
        g.fillRect(GUI_W - 2, 1, 1, GUI_H - 2);

        // Fill main background
        g.setColor(BG_BORDER);
        g.fillRect(2, 2, GUI_W - 4, GUI_H - 4);

        // Inner content area (darker region where slots go) - upper section
        g.setColor(INNER_AREA);
        // We don't fill a full inner area; MC GUIs have the gray border as background
        // and slots are drawn individually
    }

    static void drawSlot(Graphics2D g, int x, int y) {
        // Standard MC slot: 18x18 outer, 16x16 inner
        // Top and left edges are dark (inset look)
        g.setColor(DARKER_EDGE);
        g.fillRect(x, y, 18, 1);      // top
        g.fillRect(x, y, 1, 18);      // left

        // Bottom and right edges are white (raised look)
        g.setColor(WHITE_HIGHLIGHT);
        g.fillRect(x, y + 17, 18, 1);   // bottom
        g.fillRect(x + 17, y, 1, 18);   // right

        // Inner border
        g.setColor(SLOT_BORDER);
        g.fillRect(x + 1, y + 17, 16, 1);  // bottom inner - actually let's keep it simple
        // Actually the MC slot style is:
        // top-left corner dark, inner area dark gray
        g.setColor(new Color(120, 120, 120));
        g.fillRect(x + 1, y + 1, 16, 1);   // inner top
        g.fillRect(x + 1, y + 1, 1, 16);   // inner left

        // Fill slot background
        g.setColor(SLOT_BG);
        // Correction: MC slots have a lighter gray fill, not super dark
        g.setColor(new Color(139, 139, 139));
        g.fillRect(x + 1, y + 1, 16, 16);

        // Re-do proper beveled slot:
        // The slot in MC has:
        // - Dark top-left edges (shadow - looks inset)
        // - Light bottom-right edges (highlight)
        // - Medium gray fill

        // Shadow edges (top, left)
        g.setColor(new Color(55, 55, 55));
        g.drawLine(x, y, x + 17, y);         // top
        g.drawLine(x, y, x, y + 17);         // left

        // Highlight edges (bottom, right)
        g.setColor(WHITE_HIGHLIGHT);
        g.drawLine(x + 1, y + 17, x + 17, y + 17);  // bottom
        g.drawLine(x + 17, y + 1, x + 17, y + 17);  // right

        // Inner shadow
        g.setColor(new Color(100, 100, 100));
        g.drawLine(x + 1, y + 1, x + 16, y + 1);    // inner top
        g.drawLine(x + 1, y + 1, x + 1, y + 16);    // inner left

        // Slot fill
        g.setColor(new Color(139, 139, 139));
        g.fillRect(x + 2, y + 2, 15, 15);
    }

    static void drawLargeOutputSlot(Graphics2D g, int x, int y) {
        // Output slot is 26x26 with 24x24 inner
        int w = 26, h = 26;

        g.setColor(new Color(55, 55, 55));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);

        g.setColor(WHITE_HIGHLIGHT);
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);

        g.setColor(new Color(100, 100, 100));
        g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
        g.drawLine(x + 1, y + 1, x + 1, y + h - 2);

        g.setColor(new Color(139, 139, 139));
        g.fillRect(x + 2, y + 2, w - 3, h - 3);
    }

    static void drawPlayerInventory(Graphics2D g) {
        // 3 rows of 9 slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlot(g, INV_X + col * 18, INV_Y + row * 18);
            }
        }
        // Hotbar - 1 row of 9 slots with gap
        for (int col = 0; col < 9; col++) {
            drawSlot(g, INV_X + col * 18, HOTBAR_Y);
        }
    }

    static void drawArrow(Graphics2D g, int x, int y) {
        // Right-pointing arrow, approximately 24x17
        // Arrow body (horizontal bar)
        g.setColor(ARROW_COLOR);
        g.fillRect(x, y + 5, 17, 6);  // bar

        // Arrow head (triangle pointing right)
        for (int i = 0; i < 8; i++) {
            g.drawLine(x + 17 + i, y + 8 - i, x + 17 + i, y + 8 + i);
        }
        // Slight border
        g.setColor(new Color(90, 90, 90));
        g.drawLine(x, y + 5, x + 16, y + 5);    // top of bar
        g.drawLine(x, y + 10, x + 16, y + 10);   // bottom of bar
    }

    static void drawArrowOutline(Graphics2D g, int x, int y) {
        // Hollow arrow outline for progress overlay area (24x17)
        g.setColor(new Color(200, 200, 200));
        g.fillRect(x, y, 24, 17);

        g.setColor(ARROW_COLOR);
        // Bar outline
        g.drawRect(x, y + 5, 16, 6);
        // Arrow head outline
        for (int i = 0; i < 8; i++) {
            g.drawLine(x + 17 + i, y + 8 - i, x + 17 + i, y + 8 - i);
            g.drawLine(x + 17 + i, y + 8 + i, x + 17 + i, y + 8 + i);
        }
    }

    static void drawFilledArrowSprite(Graphics2D g, int x, int y) {
        // Filled white arrow for progress overlay (24x17)
        g.setColor(WHITE_HIGHLIGHT);
        g.fillRect(x, y + 5, 17, 6);
        for (int i = 0; i < 8; i++) {
            g.drawLine(x + 17 + i, y + 8 - i, x + 17 + i, y + 8 + i);
        }
    }

    static void drawFlameIcon(Graphics2D g, int x, int y) {
        // Simple flame icon 14x14 - orange/red
        g.setColor(new Color(200, 100, 0));
        // Flame shape
        g.fillOval(x + 3, y + 5, 8, 9);
        g.setColor(new Color(255, 160, 0));
        g.fillOval(x + 4, y + 3, 6, 8);
        g.setColor(new Color(255, 220, 50));
        g.fillOval(x + 5, y + 5, 4, 5);
        // Tip
        g.setColor(new Color(255, 160, 0));
        g.fillOval(x + 5, y + 1, 4, 5);
    }

    static void drawMoonIcon(Graphics2D g, int x, int y) {
        // Crescent moon 14x14
        g.setColor(new Color(220, 220, 180));
        g.fillOval(x + 2, y + 1, 10, 12);
        g.setColor(BG_BORDER); // cut out part to make crescent
        g.fillOval(x + 5, y + 1, 10, 12);
    }

    static void drawSunIcon(Graphics2D g, int x, int y) {
        // Simple sun 14x14
        g.setColor(new Color(255, 200, 50));
        g.fillOval(x + 3, y + 3, 8, 8);
        // Rays
        g.setColor(new Color(255, 180, 30));
        g.drawLine(x + 7, y, x + 7, y + 2);     // top
        g.drawLine(x + 7, y + 11, x + 7, y + 13); // bottom
        g.drawLine(x, y + 7, x + 2, y + 7);     // left
        g.drawLine(x + 11, y + 7, x + 13, y + 7); // right
        // Diagonal rays
        g.drawLine(x + 2, y + 2, x + 4, y + 4);
        g.drawLine(x + 9, y + 2, x + 11, y + 4);
        g.drawLine(x + 2, y + 11, x + 4, y + 9);
        g.drawLine(x + 9, y + 11, x + 11, y + 9);
    }

    static void drawBubbles(Graphics2D g, int x, int y) {
        // Bubble column 8x16
        g.setColor(new Color(100, 150, 255));
        g.drawOval(x + 1, y + 11, 5, 5);
        g.drawOval(x + 3, y + 6, 4, 4);
        g.drawOval(x + 1, y + 1, 3, 3);
        g.drawOval(x + 4, y + 3, 2, 2);
    }

    static void drawTitle(Graphics2D g, String title) {
        g.setColor(TITLE_COLOR);
        g.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g.drawString(title, 8, 12);
    }

    static void drawInventoryLabel(Graphics2D g) {
        g.setColor(TITLE_COLOR);
        g.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g.drawString("Inventory", 8, INV_Y - 2);
    }

    static void save(BufferedImage img, String name) throws Exception {
        File f = new File(OUTPUT_DIR + name);
        ImageIO.write(img, "png", f);
        System.out.println("Saved: " + f.getAbsolutePath() + " (" + f.length() + " bytes)");
    }

    // ---- GUI Generators ----

    static void generateSteamer() throws Exception {
        BufferedImage img = createBase();
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        drawGuiBackground(g);
        drawTitle(g, "Steamer");
        drawInventoryLabel(g);

        // Input slot at (55,34)
        drawSlot(g, 55, 34);

        // Output slot at (115,34) - large
        drawLargeOutputSlot(g, 111, 30);

        // Arrow between them at (79,35)
        drawArrow(g, 79, 35);

        // Player inventory
        drawPlayerInventory(g);

        // Extra arrow sprite at (176,14) 24x17
        drawArrowOutline(g, 176, 14);
        drawFilledArrowSprite(g, 176, 14);

        // Flame icon at (176,0) 14x14
        drawFlameIcon(g, 176, 0);

        g.dispose();
        save(img, "steamer.png");
    }

    static void generateKojiTray() throws Exception {
        BufferedImage img = createBase();
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        drawGuiBackground(g);
        drawTitle(g, "Koji Tray");
        drawInventoryLabel(g);

        // 2 input slots
        drawSlot(g, 37, 34);
        drawSlot(g, 73, 34);

        // Output slot at (133,34) - large
        drawLargeOutputSlot(g, 129, 30);

        // Arrow from inputs to output at (97,35)
        drawArrow(g, 97, 35);

        // Player inventory
        drawPlayerInventory(g);

        // Extra arrow sprite at (176,14) 24x17
        drawFilledArrowSprite(g, 176, 14);

        // Moon icon at (176,0) 14x14
        drawMoonIcon(g, 176, 0);

        // Sun icon at (176,31) 14x14
        drawSunIcon(g, 176, 31);

        g.dispose();
        save(img, "koji_tray.png");
    }

    static void generateFermentationBarrel() throws Exception {
        BufferedImage img = createBase();
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        drawGuiBackground(g);
        drawTitle(g, "Fermentation Barrel");
        drawInventoryLabel(g);

        // 3 input slots
        drawSlot(g, 31, 24);  // primary
        drawSlot(g, 31, 46);  // secondary
        drawSlot(g, 55, 35);  // water

        // Output slot at (123,34) - large
        drawLargeOutputSlot(g, 119, 30);

        // Arrow at (85,35)
        drawArrow(g, 85, 35);

        // Player inventory
        drawPlayerInventory(g);

        // Extra arrow sprite at (176,14) 24x17
        drawFilledArrowSprite(g, 176, 14);

        // Bubble area at (176,31) 8x16
        drawBubbles(g, 176, 31);

        g.dispose();
        save(img, "fermentation_barrel.png");
    }

    static void generatePresser() throws Exception {
        BufferedImage img = createBase();
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        drawGuiBackground(g);
        drawTitle(g, "Presser");
        drawInventoryLabel(g);

        // Input slot at (55,34)
        drawSlot(g, 55, 34);

        // Output slot at (115,34) - large
        drawLargeOutputSlot(g, 111, 30);

        // Arrow between them at (79,35)
        drawArrow(g, 79, 35);

        // Player inventory
        drawPlayerInventory(g);

        // Extra arrow sprite at (176,14) 24x17
        drawFilledArrowSprite(g, 176, 14);

        g.dispose();
        save(img, "presser.png");
    }
}
