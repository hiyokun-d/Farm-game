package Screen;

import Entity.CollisionChecker;
import NPC.BaseNPC;
import NPC.MerchantNPC;
import Player.Player;
import Tile.Render_Objects;
import Tile.Render_tiles;
import UI.Components.ShopUI;
import UI.UIContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    // WINDOW SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 13;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    public final int maxWorldCol = 65;  // or read from CSV
    public final int maxWorldRow = 65;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    public int frameCounter;

    public boolean inMainMenu = true;
    private final String[] mainMenuOptions = {"Play", "Credits", "???"};
    private int mainMenuSelectedIndex = 0;
    private boolean mainMenuMouseClicked = false;
    private boolean lastMenuUp = false, lastMenuDown = false, lastMenuEnter = false;

    public CollisionChecker collisionChecker = new CollisionChecker(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    // Initialize tile and crop renderers before the player so item icons
    // can be assigned from crop tiles before the player's inventory is built.
    public Render_tiles render_tiles = new Render_tiles(this);
    public Render_Objects renderingObjects = new Render_Objects(this);

    public Player player = new Player(this, keyH);

    public UIContainer uiContainer = new UIContainer();

    // List of all NPCs in the world (currently just the merchant)
    public List<BaseNPC> npcs = new ArrayList<>();

    // Merchant shop state
    public boolean shopOpen = false;
    private ShopUI shopUI;

    public GamePanel() throws IOException {
//        Filehandler.load();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.white);
        this.setDoubleBuffered(true); // better rendering performance
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Mouse support for main menu
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (inMainMenu) {
                    mainMenuMouseClicked = true;
                }
            }
        });

        // Create a single merchant NPC near the starting farm area
        npcs.add(new MerchantNPC(
                this,
                "Merchant",
                tileSize * 50,
                tileSize * 24,
                new String[]{
                        "Welcome!",
                        "Sell crops, buy seeds with me."
                }
        ));
    }

    public void update() {
        uiContainer.update();

        // MAIN MENU STATE: handle navigation & selection
        if (inMainMenu) {
            updateMainMenu();
            return;
        }

        renderingObjects.updatePlantGrowth();

        for (BaseNPC npc : npcs) {
            npc.update();
        }

        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (inMainMenu) {
            drawMainMenu(g2);
            g2.dispose();
            return;
        }

        // PLS TAMBAHIN ELEMEN DISINI COK, KALO ADA ELEMEN YANG HILANG! CHECK DISINI DULU
        // GW CAPEK DEBUGGING GEGARA SALAH LAYER DOANG ANJENG
        render_tiles.draw(g2);
        renderingObjects.draw(g2);

        for (BaseNPC npc : npcs) {
            npc.draw(g2);
        }

        player.drawTileOutline(g2, player.hoverRow, player.hoverCol);
        player.draw(g2);

        uiContainer.draw(g2);

        g2.setColor(Color.darkGray);
        String demo = "Made by hiyo, disclaimer: this is a demo";
        int helpWidth = g2.getFontMetrics().stringWidth(demo);
        g2.drawString(demo, (screenWidth - helpWidth) - 20, screenHeight - 10);
        g2.dispose();
    }

    private void drawMainMenu(Graphics2D g2) {
        // Background
        g2.setColor(new Color(10, 10, 30));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "RPG Farm";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (screenWidth - titleWidth) / 2, screenHeight / 3);

        // Subtitle / hint
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String hint = "Press ENTER or click a button";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (screenWidth - hintWidth) / 2, screenHeight / 3 + 40);

        // Menu buttons
        Rectangle[] buttons = getMainMenuButtonBounds();
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < buttons.length; i++) {
            Rectangle r = buttons[i];
            boolean selected = (i == mainMenuSelectedIndex);

            Color bg = selected ? new Color(80, 80, 140) : new Color(40, 40, 80);
            g2.setColor(bg);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            g2.setColor(Color.WHITE);
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            String label = mainMenuOptions[i];
            int textWidth = fm.stringWidth(label);
            int textX = r.x + (r.width - textWidth) / 2;
            int textY = r.y + (r.height + fm.getAscent()) / 2 - 3;
            g2.drawString(label, textX, textY);
        }
    }

    private Rectangle[] getMainMenuButtonBounds() {
        int buttonCount = mainMenuOptions.length;
        int buttonWidth = screenWidth / 3;
        int buttonHeight = tileSize + 8;
        int startX = (screenWidth - buttonWidth) / 2;
        int totalHeight = buttonCount * buttonHeight + (buttonCount - 1) * 10;
        int startY = screenHeight / 2 - totalHeight / 2;

        Rectangle[] rects = new Rectangle[buttonCount];
        for (int i = 0; i < buttonCount; i++) {
            int y = startY + i * (buttonHeight + 10);
            rects[i] = new Rectangle(startX, y, buttonWidth, buttonHeight);
        }
        return rects;
    }

    private void updateMainMenu() {
        boolean up = keyH.upPressed;
        boolean down = keyH.downPressed;
        boolean enter = keyH.enterPressed;

        int optionCount = mainMenuOptions.length;

        // Keyboard navigation
        if (up && !lastMenuUp) {
            mainMenuSelectedIndex = (mainMenuSelectedIndex - 1 + optionCount) % optionCount;
        }
        if (down && !lastMenuDown) {
            mainMenuSelectedIndex = (mainMenuSelectedIndex + 1) % optionCount;
        }

        // Mouse hover selection
        Rectangle[] buttons = getMainMenuButtonBounds();
        Point mouse = getMousePosition();
        if (mouse != null) {
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].contains(mouse)) {
                    mainMenuSelectedIndex = i;
                }
            }
        }

        // Activate selection via Enter or mouse click
        if ((enter && !lastMenuEnter) || mainMenuMouseClicked) {
            activateMainMenuSelection();
            keyH.enterPressed = false;
            mainMenuMouseClicked = false;
        }

        lastMenuUp = up;
        lastMenuDown = down;
        lastMenuEnter = enter;
    }

    private void activateMainMenuSelection() {
        switch (mainMenuSelectedIndex) {
            case 0 -> inMainMenu = false; // Play
            case 1 -> System.out.println("Credits: placeholder - edit drawMainMenu/activateMainMenuSelection later.");
            case 2 -> System.out.println("Random option selected (placeholder).");
        }
    }

    //! DON'T TOUCH THIS METHOD or YOU'LL BREAK THE GAME LOOP, I SWEAR PLSSSS DON'T TOUCH IT OR I WILL EXPLODE TO FIX IT AGAIN
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60; // 60 FPS
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            // 1 UPDATE: update information such as character positions
            update();

            // 2 DRAW: draw the screen with the updated information
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Expose key handler for UI components like ShopUI
    public KeyHandler getKeyHandler() {
        return keyH;
    }

    // Open merchant shop overlay
    public void openShop() {
        if (shopOpen) return;
        shopOpen = true;
        shopUI = new ShopUI(this, player, keyH);
        uiContainer.clear();
        uiContainer.add(shopUI);
        player.canMove = false;
    }

    // Close merchant shop overlay
    public void closeShop() {
        shopOpen = false;
        if (shopUI != null) {
            uiContainer.remove(shopUI);
            shopUI = null;
        }
        player.canMove = true;
    }
}
