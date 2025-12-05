package NPC;

import Entity.Entity;
import Player.Player;
import Screen.GamePanel;

import java.awt.*;

public abstract class BaseNPC extends Entity {
    protected String name;
    protected String[] dialogue;
    protected int dialogueIndex = 0;
    protected boolean isTalking = false;

    public BaseNPC(GamePanel gp, String name, int worldX, int worldY, String[] dialogue) {
        super(gp);

        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.dialogue = (dialogue != null) ? dialogue : new String[]{"..."};
        this.speed = 0;

        this.width = gp.tileSize;
        this.height = gp.tileSize;
        this.direction = "down";
        this.solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
    }

    public void startTalking() {
        isTalking = true;
        dialogueIndex = 0;
    }

    public void nextDialogue() {
        if (dialogue == null || dialogue.length == 0) return;

        if (dialogueIndex < dialogue.length - 1) {
            dialogueIndex++;
        } else {
            endDialogue();
        }
    }

    public void endDialogue() {
        isTalking = false;
        dialogueIndex = 0;
    }

    public boolean getIsTalking() {
        return isTalking;
    }

    public abstract void update();

    /**
     * Called when the player presses the interact key near this NPC.
     */
    public abstract void onInteract(Player player);

    /**
     * Draw the NPC and optional dialogue.
     */
    public abstract void draw(Graphics2D g2);

    protected void drawDialogue(Graphics2D g2, int x, int y) {
        if (dialogue == null || dialogue.length == 0) return;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x - 10, y - 30, 220, 40, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString(dialogue[dialogueIndex], x, y - 10);
    }
}
