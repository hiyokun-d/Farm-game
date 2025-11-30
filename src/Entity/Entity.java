package Entity;

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

    public void updateAnimation(BufferedImage[] frames, boolean looping) {
        spriteCounter++;
        if (spriteCounter > frameSpeed) {
            spriteNum++;
            if (spriteNum >= frames.length) {
                spriteNum = 0;
            }
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
