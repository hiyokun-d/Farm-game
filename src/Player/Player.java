package Player;

import Entity.Entity;
import Screen.GamePanel;
import Screen.KeyHandler;
import fileHandler.Filehandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH = new KeyHandler();
    private boolean isMoving = false;

    //    private final BufferedImage[] runFrames = new BufferedImage[4];
    private final BufferedImage[] walkLeft = new BufferedImage[4];
    private final BufferedImage[] walkRight = new BufferedImage[4];
    private final BufferedImage[] walkUp = new BufferedImage[4];
    private final BufferedImage[] walkDown = new BufferedImage[4];

    public BufferedImage run, dustRun, dustJump, jump, idle;

    public final int screenX;
    public final int screenY;
    public boolean isInteracting = false;
    public boolean canMove = true, canMoveUp = true, canMoveDown = true, canMoveleft = true, canMoveRight = true;

    public int hoverCol, hoverRow;

    public Player(GamePanel gp, KeyHandler keyH) throws IOException {
        this.gp = gp;
        this.keyH = keyH;

        this.screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        this.screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

//        solidArea = new Rectangle(, 15, 28, 32);
        solidArea = new Rectangle(0, 0, 15, 15);
        setDefaultValues();
        getPlayerImage();

    }

    public void setDefaultValues() throws IOException {
//        this.worldX = Filehandler.getInt("playerX");
//        this.worldY = Filehandler.getInt("playerY");

        this.worldX = gp.tileSize * 18; // col 5
        this.worldY = gp.tileSize * 19; // row 7

        this.speed = Filehandler.getInt("playerSpeed");
        this.width = 35;
        this.height = 35;
        direction = "right";
    }

    public void getPlayerImage() {
        try {
            String ASSET_PATH = "assets/Characters/";
            int frameSize = 16;
            int padding = 16;

            BufferedImage runSheet = ImageIO.read(new File(ASSET_PATH + "character_Spritesheet.png"));
            run = runSheet.getSubimage(32, 32, 16, 16);

            // player Movement loader
            for (int i = 0; i < walkRight.length; i++)
                walkRight[i] = runSheet.getSubimage(i * 32, 96, frameSize, frameSize);
            for (int i = 0; i < walkLeft.length; i++)
                walkLeft[i] = runSheet.getSubimage(i * 32, 64, frameSize, frameSize);
            for (int i = 0; i < walkUp.length; i++)
                walkUp[i] = runSheet.getSubimage(i * 32, 32, frameSize, frameSize);
            for (int i = 0; i < walkDown.length; i++)
                walkDown[i] = runSheet.getSubimage(i * 32, 0, frameSize, frameSize);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void movement() {
        if (keyH.downPressed && canMoveDown) {
            direction = "down";
            isMoving = true;
            updateAnimation(walkDown);
        } else if (keyH.upPressed && canMoveUp) {
            direction = "up";
            isMoving = true;
            updateAnimation(walkUp);
        } else if (keyH.leftPressed && canMoveleft) {
            direction = "left";
            isMoving = true;
            updateAnimation(walkLeft);
        } else if (keyH.rightPressed && canMoveRight) {
            direction = "right";
            isMoving = true;
            updateAnimation(walkRight);
        } else {
            spriteNum = 0;
            isMoving = false;
        }
    }

    public void hoeTile() {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;
        gp.tileM.convertGrassToDirtTile(tileCol, tileRow);
        System.out.println("Hoed tile at (" + tileCol + ", " + tileRow + ")");
    }

    public void unHoeTile() {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;
        gp.tileM.convertDirtToGrassTile(tileCol, tileRow);
        System.out.println("Hoed tile at (" + tileCol + ", " + tileRow + ")");
    }

    public void plantCrop() {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;
        gp.tileM.plantCropOnSoil("POTATO", tileCol, tileRow);
    }


    public void update() {
        hoverCol = (worldX + solidArea.x) / gp.tileSize;
        hoverRow = (worldY + solidArea.y) / gp.tileSize;

        if (keyH.interactPressed) {
            keyH.interactPressed = false;
            isInteracting = true;

            if (standingOn.equals("GRASS"))
                hoeTile();
            if (standingOn.equals("SOIL") && !standingOn.equals("CROPS"))
                plantCrop();

            // TODO: when it's triggered check the player hands, and collide where the player standing on
            // TODO: if the player is interacting with hoe, then change the TILE below to FARMED_SOIL

        } else {
            isInteracting = false;
        }

        if (!canMove) {
            isMoving = false;
            return;
        }
        movement();

        collisionBottomSide = collisionLeftSide = collisionRightSide = collisionTopSide = collisionOn = false;
        gp.collisionChecker.checkTile(this);

        if (isMoving && !collisionOn) {
            switch (direction) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                case "right" -> worldX += speed;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage imageToDraw = null;

        switch (direction) {
            case "up" -> imageToDraw = walkUp[spriteNum];
            case "down" -> imageToDraw = walkDown[spriteNum];
            case "left" -> imageToDraw = walkLeft[spriteNum];
            case "right" -> imageToDraw = walkRight[spriteNum];
        }
        //? DEBUGGING: draw hitbox
//        g2.setBackground(new Color(0, 0, 0, 0.5f));
//        g2.fillRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);

        g2.drawImage(imageToDraw, this.screenX, this.screenY, this.width, this.height, null);
    }
}

// oh hey there, you found my secret comment!