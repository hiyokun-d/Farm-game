package Tile;

import fileHandler.ItemData;
import types.CropType;

import java.awt.image.BufferedImage;

public class Crop extends Tile {
    public CropType type;
    public ItemData data;

    public boolean isHarvestable = false, isWatered = true;
    public int growthStage = 1, maxGrowthStage = 4;
    public BufferedImage[] growthImages = new BufferedImage[maxGrowthStage + 1];

    public String harvestStatus = "CROP";
    public int startIndex = 0;

    public long lastGrowthTime = 0;
    public long timePerStage = 2000;

    public Crop() {
        this.id = "CROP";
        this.collision = true;
    }

    public Crop(CropType type, int startIndex) {
        this.type = type;
        this.startIndex = startIndex;
        this.id = "CROP";
        this.collision = true;
    }

    public void grow() {
        // Only grow if the crop was watered since the last stage
        if (!isWatered) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGrowthTime < timePerStage) return;

        lastGrowthTime = currentTime;
        if (growthStage < maxGrowthStage - 1) {
            growthStage++;
            if (growthStage == maxGrowthStage - 1) {
                isHarvestable = true;
                setHarvestStatus("HARVESTABLE");
            }
        }

        // Require watering again for the next growth step
//        isWatered = false;
    }

    public BufferedImage getCurrentImage() {
        return growthImages[growthStage];
    }

    public String getId() {
        return this.id;
    }

    public void setID(String id) {
        this.id = harvestStatus + "_" + id;
    }

    public void setHarvestStatus(String status) {
        this.harvestStatus = status;
    }

    public int getCurrentTileIndex() {
        return startIndex + growthStage;
    }
}
