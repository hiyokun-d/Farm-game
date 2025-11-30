package Tile;

import Entity.CropType;
import Entity.Crops;
import Screen.GamePanel;

public class CropsManager {
    GamePanel gp;
    public Crops[][] cropsMap;

    public CropsManager(GamePanel gp) {
        this.gp = gp;
        cropsMap = new Crops[gp.maxWorldCol][gp.maxWorldRow];
    }

    public Crops plantCrop(String type, int col, int row) {
        if (cropsMap[col][row] == null) {
            System.out.println("Planted crop at (" + col + ", " + row + ")");
            switch (type) {
                case "WHEAT" -> {
                    return cropsMap[col][row] = new Crops(CropType.WHEAT, col, row);
                }
                case "POTATO" -> {
                    return cropsMap[col][row] = new Crops(CropType.POTATO, col, row);
                }
            }
        }
        return null;
    }

    public void removeCrop(int col, int row) {
        cropsMap[col][row] = null;
    }

    public void update() {
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                Crops crop = cropsMap[col][row];
                if (crop != null) {
                    crop.updateGrowth();


                }
            }
        }
    }
}
