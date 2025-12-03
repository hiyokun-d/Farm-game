package fileHandler;

import types.CropType;

import java.awt.image.BufferedImage;

public class ItemData {
    public CropType cropType;
    public String type;        // TOOL, CROP, SEED
    public String id;
    public String name;
    public boolean stackable;
    public BufferedImage icon;
//    public int startIndex; // for sprite sheet start pointer

    // CROP-specific
    public int stages;
    public int[] growthTimes;
    public String seedId;
    public String harvestId;

    // SEED-specific
    public String plantId;
}
