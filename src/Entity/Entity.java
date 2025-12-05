package Entity;

import Screen.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    public int worldX;
    public int worldY;

    public int speed;
    public int width;
    public int height;

    public String direction;
    public Rectangle solidArea; // Collision

    public String standingOn = "none"; // to check type of tile the player standing on
    public boolean collisionOn = false, collisionLeftSide = false, collisionRightSide = false, collisionTopSide = false, collisionBottomSide = false, onGrass = false;

    public int spriteCounter = 0;
    public int spriteNum = 0; // starting from frame 0
    public int frameSpeed = 5;

    public GamePanel gp;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void updateAnimation(BufferedImage[] frames, boolean looping) {
        spriteCounter++;
        if (spriteCounter > frameSpeed) {
            spriteNum++;

            if (spriteNum >= frames.length) {
                // If looping, wrap back to the first frame, otherwise
                // clamp to the last valid frame index.
                spriteNum = looping ? 0 : frames.length - 1;
            }

            spriteCounter = 0;
        }
    }

    public void updateAnimation(BufferedImage[] frames) {
        spriteCounter++;
        if (spriteCounter > frameSpeed) {
            spriteNum++;
            if (spriteNum >= frames.length) {
                spriteNum = 0;
            }

            spriteCounter = 0;
        }
    }
}
