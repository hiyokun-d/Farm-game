package Screen;

import Entity.CollisionChecker;
import Player.Player;
import Tile.Render_Objects;
import Tile.Render_tiles;
import UI.UIContainer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
    // WINDOW SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 13;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    public final int maxWorldCol = 42;  // or read from CSV
    public final int maxWorldRow = 42;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    public int frameCounter;

    public CollisionChecker collisionChecker = new CollisionChecker(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    // Initialize tile and crop renderers before the player so item icons
    // can be assigned from crop tiles before the player's inventory is built.
    public Render_tiles render_tiles = new Render_tiles(this);
    public Render_Objects renderingObjects = new Render_Objects(this);

    public Player player = new Player(this, keyH);

    public UIContainer uiContainer = new UIContainer();

    public GamePanel() throws IOException {
//        Filehandler.load();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.white);
        this.setDoubleBuffered(true); // better rendering performance
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void update() {
        uiContainer.update();

        renderingObjects.updatePlantGrowth();
        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // PLS TAMBAHIN ELEMEN DISINI COK, KALO ADA ELEMEN YANG HILANG! CHECK DISINI DULU
        // GW CAPEK DEBUGGING GEGARA SALAH LAYER DOANG ANJENG
        render_tiles.draw(g2);
        renderingObjects.draw(g2);

        player.drawTileOutline(g2, player.hoverRow, player.hoverCol);
        player.draw(g2);

        uiContainer.draw(g2);

        g2.dispose();
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
}
