package Inventory;

import fileHandler.ItemData;

import java.awt.image.BufferedImage;

public class Item {
    public String itemID;
    public String itemName;
    public boolean stackable;
    public int quantity = 1;

    // link to the item data
    public ItemData data;

    // OPTIONAL: for future UI
    public BufferedImage icon; //! YOU STUPID SHIT DON'T CHANGE THE NAME OF THIS
    public BufferedImage altIcon;

    public Item(ItemData data) {
        this.data = data;
        this.itemID = data.id;
        this.itemName = data.name;
        this.stackable = data.stackable;
        // Copy the icon reference from the shared ItemData so UI can render it
        this.icon = data.icon;
        this.altIcon = data.altIcon;
    }
}
