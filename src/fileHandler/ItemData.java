package fileHandler;

import types.CropType;

import java.awt.image.BufferedImage;

public class ItemData {
    public CropType cropType;
    public String type;        // TOOL, CROP, SEED
    public String id;
    public int icon_id;
    public String name;
    public boolean stackable;

    public BufferedImage icon;
    public BufferedImage altIcon;

    // General economy data
    public int price;          // buy/sell price used by merchants

    // Watering-can specific
    public int waterCapacity;  // current water amount (for tools like watering can)
    public int maxCapacity;    // maximum water capacity

    // CROP-specific
    public int stages;
    public int[] growthTimes;
    public String seedId;
    public String harvestId;

    // SEED-specific
    public String plantId;
}
