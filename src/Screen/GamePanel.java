package Screen;

import Entity.CollisionChecker;
import NPC.BaseNPC;
import NPC.MerchantNPC;
import Player.Player;
import Tile.Render_Objects;
import Tile.Render_tiles;
import UI.Components.ShopUI;
import UI.TransitionManager;
import UI.UIContainer;
import audio.MusicPlayer;

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
    public boolean inCreditMenu = false;

    public CollisionChecker collisionChecker = new CollisionChecker(this);
    KeyHandler keyH = new KeyHandler();
    public MusicPlayer musicPlayer = new MusicPlayer();

    Thread gameThread;

    // Initialize tile and crop renderers before the player so item icons
    // can be assigned from crop tiles before the player's inventory is built.
    public Render_tiles render_tiles = new Render_tiles(this);
    public Render_Objects renderingObjects = new Render_Objects(this);

    public Player player = new Player(this, keyH);
    public TransitionManager transition = new TransitionManager();
    public UIContainer uiContainer = new UIContainer();

    // List of all NPCs in the world (currently just the merchant)
    public List<BaseNPC> npcs = new ArrayList<>();

    // Merchant shop state
    public boolean shopOpen = false;
    private ShopUI shopUI;
    private MainMenu mainMenu = new MainMenu(this);

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
                    // Check which button (if any) was clicked and trigger that option.
                    Point mousePoint = e.getPoint();
                    Rectangle[] buttons = mainMenu.getMainMenuButtonBounds();
                    for (int i = 0; i < buttons.length; i++) {
                        if (buttons[i].contains(mousePoint)) {
                            mainMenu.mainMenuSelectedIndex = i;
                            mainMenu.mainMenuMouseClicked = true;
                            break;
                        }
                    }
                }
            }
        });

        // Create a single merchant NPC near the starting farm area
        npcs.add(new MerchantNPC(
                this,
                "Merchant",
                tileSize * 49,
                tileSize * 24,
                new String[]{
                        "Welcome!",
                        "Sell crops, buy seeds with me."
                }
        ));

        musicPlayer.playLoop("/resources/audio/soundtrack.wav");
    }

    public void update() {
        uiContainer.update();
        transition.update();

        // MAIN MENU STATE: handle navigation & selection
        if (inMainMenu) {
            mainMenu.updateMainMenu();
            return;
        } else if (inCreditMenu) {
            mainMenu.updateCreditsMenu();
            return;
        }

//        renderingObjects.updatePlantGrowth();

        for (BaseNPC npc : npcs) {
            npc.update();
        }

        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        if (inMainMenu) {
            mainMenu.drawMainMenu(g2);
            transition.draw(g2, screenWidth, screenHeight);
            g2.dispose();
            return;
        } else if (inCreditMenu) {
            mainMenu.drawCreditsMenu(g2);
            transition.draw(g2, screenWidth, screenHeight);
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

        player.drawDays(g2);
        player.drawTileOutline(g2, player.hoverRow, player.hoverCol);
        player.draw(g2);

        uiContainer.draw(g2);

        transition.draw(g2, screenWidth, screenHeight);

        g2.setColor(Color.darkGray);
        String demo = "Made by hiyo, disclaimer: this is a demo";
        int helpWidth = g2.getFontMetrics().stringWidth(demo);
        g2.drawString(demo, (screenWidth - helpWidth) - 20, screenHeight - 10);
        g2.dispose();

        musicPlayer.setVolume(-3f);
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
