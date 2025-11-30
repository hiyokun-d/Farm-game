package Tile;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image;
    public BufferedImage[] animatedImages;

    public String id;

    public boolean collision = false;

    public int spriteCounter = 0;
    public int spriteNum = 0; // starting from frame 0
    public int frameSpeed = 15;

    public boolean isAnimated = false; // check if it still on animated state, if not, set to false
    public boolean isActive = true; // when the animation is not play yet

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
