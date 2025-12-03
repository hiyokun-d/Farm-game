package Player;

import Entity.Entity;
import Inventory.Item;
import Screen.GamePanel;
import Screen.KeyHandler;
import UI.Components.UIBox;
import UI.Components.UIImage;
import UI.Components.UILabel;
import UI.UIComponent;
import fileHandler.Filehandler;
import fileHandler.ItemData;
import fileHandler.ItemDatabase;
import types.CropType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH = new KeyHandler();
    private boolean isMoving = false;

    //    private final BufferedImage[] runFrames = new BufferedImage[4];
    private final BufferedImage[] walkLeft = new BufferedImage[4]; // MAX FRAMES IS 4
    private final BufferedImage[] walkRight = new BufferedImage[4];
    private final BufferedImage[] walkUp = new BufferedImage[4];
    private final BufferedImage[] walkDown = new BufferedImage[4];

    public final int screenX;
    public final int screenY;
    public boolean isInteracting = false;
    public boolean canMove = true, canMoveUp = true, canMoveDown = true, canMoveleft = true, canMoveRight = true;

    public int hoverCol, hoverRow;

    public ArrayList<Item> inventory = new ArrayList<>();
    public int selectedSlot = 1;
    public Item selectedItemByCursor = null; // for mouse selection on UI inventory
    public Item selectedItemOnHotbar = null;

    public ArrayList<Item> hotbar = new ArrayList<>(10); // max capacity on hotbat we'll be here

//    public Item equippedItem = null;

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

        for (int i = 0; i < 10; i++)
            hotbar.add(null);

        addToInventory("HOE");
        addToInventory("WATERING_CAN");
        addToInventory("WHEAT_SEED", 5);
        addToInventory("POTATO_SEED", 15);

        equipStarterTools();
    }

    public void getPlayerImage() {
        try {
            String ASSET_PATH = "assets/Characters/";
            int frameSize = 16;
            int padding = 16;

            BufferedImage runSheet = ImageIO.read(new File(ASSET_PATH + "character_Spritesheet.png"));

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
        if (selectedItemOnHotbar.data.type.equals("TOOL") && selectedItemOnHotbar.data.id.equals("HOE"))
            gp.render_tiles.convertGrassToDirtTile(tileCol, tileRow);
    }

    public void harvestCrop() {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;

        addToInventory(gp.render_crops.getSeedPlantId(tileCol, tileRow), 5);
//        addToInventory(gp.render_crops.getPlantItemId(tileCol, tileRow), 3);

        gp.render_crops.harvestCrop(tileCol, tileRow);
        gp.render_tiles.convertDirtToGrassTile(tileCol, tileRow);
    }

    public void plantCrop(CropType type) {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;

        ItemData seedData = selectedItemOnHotbar.data;   // THIS ITEM is the SEED
        String plantItemId = seedData.plantId;           // THIS is the crop's item ID

        gp.render_crops.plantCrop(type, plantItemId, tileCol, tileRow);
    }

    public void wateredCrop() {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;
        gp.render_crops.wateredCrop(tileCol, tileRow);
    }

    public boolean isHarvestable() {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;
        return gp.render_crops.isHarvestable(tileCol, tileRow);
    }

    public String getSeedPlantId() {
        int tileCol = (worldX + solidArea.x) / gp.tileSize;
        int tileRow = (worldY + solidArea.y) / gp.tileSize;
        return gp.render_crops.getSeedPlantId(tileCol, tileRow);
    }

    public void addToInventory(String itemID, int amount) {
        //? instead of looping through database each time, we can just pass the ItemData object
        ItemData data = ItemDatabase.get(itemID);
        Item newItem = new Item(data);

        if (data == null) return;

        for (Item i : inventory) {
            if (i.itemID.equals(itemID)) {
                for (int z = 0; z < amount - 1; z++)
                    i.quantity++;
                updateHotbarForItem(i);

                System.out.println("Added " + itemID + " to inventory. qty: " + i.quantity);
                //TODO: SAVE HERE
                return;
            }
        }
        updateHotbarForItem(newItem);

        inventory.add(newItem);
        for (Item i : inventory) {
            if (i.itemID.equals(itemID)) {
                for (int z = 0; z < amount - 1; z++)
                    i.quantity++;
                updateHotbarForItem(i);

                System.out.println("Added " + itemID + " to inventory. qty: " + i.quantity);
                //TODO: SAVE HERE
                return;
            }
        }
    }

    public void addToInventory(String itemID) {
        //? instead of looping through database each time, we can just pass the ItemData object
        ItemData data = ItemDatabase.get(itemID);
        Item newItem = new Item(data);
        if (data == null) return;

        if (data.stackable) {
            for (Item i : inventory) {
                if (i.itemID.equals(itemID)) {
                    i.quantity++;
                    updateHotbarForItem(i);
                    return;
                }
            }
        }

        System.out.println("Added " + itemID + " to inventory.");
        updateHotbarForItem(newItem);
        inventory.add(newItem);
    }

    // TODO: REMOVE THIS LATER!
    private void equipStarterTools() {
        int slotIndex = 0;

        for (Item i : inventory) {
            if (slotIndex >= 10) break;
            System.out.println(i.itemID + " added to hotbar." + " x" + i.quantity);
            if (i.data.type == "SEED")
                System.out.println(i.data.plantId);

            hotbar.set(slotIndex, i);
            slotIndex++;
        }
    }

    public void updateHotbarForItem(Item item) {

        for (int i = 0; i < hotbar.size(); i++) {
            Item existing = hotbar.get(i);
            if (existing != null && existing.itemID.equals(item.itemID)) {
                return;
            }
        }

        for (int i = 0; i < hotbar.size(); i++) {
            if (hotbar.get(i) == null) {
                hotbar.set(i, item);
                return;
            }
        }

        System.out.println("Hotbar full. Cannot add " + item.itemID);
    }

    // TODO: CHANGE THIS LATER PLSSSSS!!!!
    // implement UI to this
    public void showInventory() {
        System.out.println("----- INVENTORY -----");
        for (Item i : inventory) {
            System.out.println(i.itemID + " x" + i.quantity);
        }
        System.out.println("---------------------");
    }

    public void inventoryKeys() {
        if (keyH.key1Pressed) selectedSlot = 0;
        if (keyH.key2Pressed) selectedSlot = 1;
        if (keyH.key3Pressed) selectedSlot = 2;
        if (keyH.key4Pressed) selectedSlot = 3;
        if (keyH.key5Pressed) selectedSlot = 4;
        if (keyH.key6Pressed) selectedSlot = 5;
        if (keyH.key7Pressed) selectedSlot = 6;
        if (keyH.key8Pressed) selectedSlot = 7;
        if (keyH.key9Pressed) selectedSlot = 8;
        if (keyH.key0Pressed) selectedSlot = 9;
    }

    public void pauseMenu() {
        UIComponent testBox = new UIComponent(20, 20, 150, 80) {
            @Override
            public void draw(Graphics2D g2) {
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(x, y, width, height, 12, 12);

                g2.setColor(Color.WHITE);
                g2.drawString("Test UI", x + 10, y + 30);
            }
        };

        gp.uiContainer.add(testBox); // disabled for now to prevent modification during render
        System.out.println(gp.uiContainer.getComponents().size());
    }

    public void showHotbar() {
        gp.uiContainer.clear();

        int baseX = gp.tileSize / 2;
        int baseY = gp.screenHeight - gp.tileSize - 10;

        for (int i = 0; i < hotbar.size(); i++) {

            int x = baseX + i * (gp.tileSize + 8);
            int y = baseY;

            Color slotColor = (i == selectedSlot)
                    ? new Color(255, 255, 120)
                    : new Color(70, 70, 70);

            UIBox slotBox = new UIBox(
                    x - 4, y - 4,
                    gp.tileSize + 8, gp.tileSize + 8,
                    slotColor
            );
            gp.uiContainer.add(slotBox);

            Item item = hotbar.get(i);
            if (item != null) {

                UIBox itemBox = new UIBox(x, y, gp.tileSize, gp.tileSize, Color.DARK_GRAY);
                gp.uiContainer.add(itemBox);

                if (item.icon != null) {
//                    System.out.println("Adding item to hotbar: " + item.icon);
                    UIImage itemImage = new UIImage(
                            x + 4,
                            y + 4,
                            item.icon
                    );

                    itemImage.width = 42;
                    itemImage.height = 42;

                    gp.uiContainer.add(itemImage);
                } else {
                    UILabel itemLabel = new UILabel(
                            x + gp.tileSize / 2,
                            y + gp.tileSize / 2,
                            item.data.id,
                            Color.WHITE
                    );
                    itemLabel.setAlign = "CENTER";
                    gp.uiContainer.add(itemLabel);
                }
            }
        }
    }

    public void update() {
        hoverCol = (worldX + solidArea.x) / gp.tileSize;
        hoverRow = (worldY + solidArea.y) / gp.tileSize;

//        showInventory();


        inventoryKeys();

        selectedItemOnHotbar = hotbar.get(selectedSlot);

//        if (selectedItemOnHotbar != null)
//            System.out.println("Selected item: " + selectedItemOnHotbar.itemID + " x" + selectedItemOnHotbar.quantity);
//        else System.out.println("No item selected.");

        if (keyH.escPressed) {
            showInventory();
        }

        if (keyH.interactPressed) {
            keyH.interactPressed = false;
            isInteracting = true;

//            ItemData wheat = ItemDatabase.get("WHEAT");
//            System.out.println(wheat.id);

            if (selectedItemOnHotbar != null)
                switch (standingOn) {
                    case "GRASS" -> hoeTile();
                    case "SOIL" -> {

                        // Prevent planting a new crop if the player is already standing on one
                        if (!standingOn.startsWith("CROP") && selectedItemOnHotbar.data.type.equals("SEED")) {

                            plantCrop(selectedItemOnHotbar.data.cropType);

                            selectedItemOnHotbar.quantity--;
                            if (selectedItemOnHotbar.quantity <= 0) {
                                System.out.println("Removed " + selectedItemOnHotbar.itemID + " from inventory.");
                                inventory.remove(selectedItemOnHotbar);
                                hotbar.set(selectedSlot, null);
                            }
                        } else {
                            System.out.println("There's already a crop planted here!");
                        }
                    }
                }

            // Interact with crops on the current tile
            if (standingOn.startsWith("CROP") && !isHarvestable()) {
                wateredCrop();
            } else if (isHarvestable()) {
                System.out.println("Harvested " + getSeedPlantId());
                harvestCrop();
            }

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

    public void drawTileOutline(Graphics2D g2, int row, int col) {
        int worldX = col * gp.tileSize;
        int worldY = row * gp.tileSize;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2.setColor(new Color(0, 177, 255, 255)); // yellow highlight
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(screenX, screenY, gp.tileSize, gp.tileSize);
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
        showHotbar();
    }
}

// oh hey there, you found my secret comment!