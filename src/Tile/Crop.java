package Tile;

import types.CropType;

import java.awt.image.BufferedImage;

public class Crop extends Tile {
    public CropType type;

    public boolean isHarvestable = false, isWatered = false;
    public int growthStage = 1, maxGrowthStage = 4;
    public BufferedImage[] growthImages = new BufferedImage[maxGrowthStage + 1];

    public String harvestStatus = "CROP";
    public int startIndex = 0;

    public Crop() {
        this.id = "CROPS";
        this.collision = true;
    }

    public Crop(CropType type, int startIndex) {
        this.type = type;
        this.startIndex = startIndex;
        this.id = "CROPS";
        this.collision = true;
    }

    public void grow() {
        if(!isWatered) return;
        if (growthStage < maxGrowthStage) {
            growthStage++;
            if (growthStage == maxGrowthStage) {
                isHarvestable = true;
                harvestStatus = "HARVESTED";
            }
        }
    }

    public BufferedImage getCurrentImage() {
        return growthImages[growthStage];
    }

    public String getId() {
        return this.id;
    }

    public void setID(String id) {
        this.id = harvestStatus+"_"+id;
    }

    public int getCurrentTileIndex() {
        return startIndex + growthStage;
    }
}
