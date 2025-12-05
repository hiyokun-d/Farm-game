package NPC;

import Player.Player;
import Screen.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MerchantNPC extends BaseNPC {

    private BufferedImage sprite;

    public MerchantNPC(GamePanel gp, String name, int worldX, int worldY, String[] dialogue) {
        super(gp, name, worldX, worldY, dialogue);
        loadSprite();
    }

    private void loadSprite() {
        try {
            String ASSET_PATH = "assets/Characters/";
            int frameSize = 16;

            BufferedImage sheet = ImageIO.read(new File(ASSET_PATH + "character_Spritesheet.png"));
            // Use the first frame of the down-facing row as a simple standing sprite
            sprite = sheet.getSubimage(0, 0, frameSize, frameSize);
        } catch (IOException e) {
            e.printStackTrace();
            sprite = null;
        }
    }

    @Override
    public void update() {
        // Static NPC for now (no movement/AI)
    }

    @Override
    public void onInteract(Player player) {
        // Open the shop UI instead of trading directly via console
        gp.openShop();
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Culling: don't draw if off-screen
        if (screenX + gp.tileSize < 0 || screenX - gp.tileSize > gp.screenWidth ||
                screenY + gp.tileSize < 0 || screenY - gp.tileSize > gp.screenHeight) {
            return;
        }

        if (sprite != null) {
            g2.drawImage(sprite, screenX, screenY, gp.tileSize, gp.tileSize, null);
        } else {
            g2.setColor(Color.ORANGE);
            g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
        }

        if (isTalking) {
            drawDialogue(g2, screenX + gp.tileSize / 2, screenY - 4);
        }
    }
}
