package Entity;

import Tile.Tile;

public class Crops extends Tile {
    private final int ORIGINAL_SIZE = 16;

    public CropType type;

    public int col, row;
    public int growthStage, maxGrowthStage = 4;

    public boolean isHarvestable = false;
    public boolean isWatered = true;

    public Crops(CropType type, int col, int row) {
        this.type = type;
        this.col = col;
        this.row = row;
        this.growthStage = 2;
    }

    public void updateGrowth() {
        if (isWatered && growthStage < maxGrowthStage + 1) {
            growthStage++;
            System.out.println("Crop at (" + col + ", " + row + ") grew to stage " + growthStage);
//            isWatered = false; // reset watering status after growth

            if (growthStage == maxGrowthStage) {
                isHarvestable = true;
            }
        }
    }
}
