package Tile;

import Screen.GamePanel;
import fileHandler.ItemData;
import fileHandler.ItemDatabase;
import types.CropType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Render_Objects {
    private final String ASSET_PATH = "assets/Tiled_files/";
    private final GamePanel gp;
    private final int ORIGINAL_TILE_SIZE = 16;

    public Crop[] cropsTiles;
    public Crop[] toolTiles; // tiles for tool icons
    public int[][] cropsTileNum;
    public Crop[][] cropsMap; // Track actual crop instances per tile

    public Render_Objects(GamePanel gp) throws IOException {
        this.gp = gp;

        cropsTiles = new Crop[500];
        // size will be adjusted automatically when loading; this is just an upper bound
        toolTiles = new Crop[50];
        cropsTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        cropsMap = new Crop[gp.maxWorldCol][gp.maxWorldRow];

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                cropsTileNum[col][row] = 0; // start with no crops
                cropsMap[col][row] = null;
            }
        }

        // Load crop tileset used both for world crops and item icons
        loadCropsSet("Objects/Basic_Plants.png", cropsTiles, 0);

        // Load tools tileset used for tool icons (HOE, WATERING_CAN, etc.)
        loadCropsSet("Objects/Basic_tools_and_meterials.png", toolTiles, 0);

        // After tiles are loaded, assign icons to items based on icon_id
        assignItemIconsFromCrops();
    }

    private void drawTile(Graphics2D g2, int row, int col, int screenX, int screenY) {
        Crop crop = cropsMap[col][row];
        if (crop == null) return;
        g2.drawImage(crop.getCurrentImage(), screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    private void loadCropsSet(String fileName, Crop[] targetArray, int startIndex) throws IOException {
        BufferedImage tileset = ImageIO.read(new File(ASSET_PATH + fileName));
        int tilesetCols = tileset.getWidth() / ORIGINAL_TILE_SIZE;
        int tilesetRows = tileset.getHeight() / ORIGINAL_TILE_SIZE;
        int index = startIndex;

        for (int row = 0; row < tilesetRows; row++) {
            for (int col = 0; col < tilesetCols; col++) {
                BufferedImage tileImage = tileset.getSubimage(
                        col * ORIGINAL_TILE_SIZE,
                        row * ORIGINAL_TILE_SIZE,
                        ORIGINAL_TILE_SIZE,
                        ORIGINAL_TILE_SIZE
                );

                Crop tile = new Crop();
                tile.image = tileImage;
                targetArray[index] = tile;
                index++;
            }
        }
    }

    private void assignItemIconsFromCrops() {
        for (ItemData data : ItemDatabase.items.values()) {
            if (data == null || data.type == null) continue;

            // Crops and seeds: icons come from the crop tileset via icon_id
            if ("CROP".equals(data.type) || "SEED".equals(data.type)) {
                if (data.icon_id < 0 || data.icon_id >= cropsTiles.length) continue;
                Crop tile = cropsTiles[data.icon_id];
                if (tile == null || tile.image == null) continue;
                data.icon = tile.image;
            }

            // Tools: icons come from the tools tileset via icon_id
            if ("TOOL".equals(data.type)) {
                if (toolTiles == null) continue;
                if (data.icon_id < 0 || data.icon_id >= toolTiles.length) continue;
                Crop tile = toolTiles[data.icon_id];
                if (tile == null || tile.image == null) continue;
                data.icon = tile.image;
            }
        }
    }

    public void plantCrop(CropType type, String plantItemId, int col, int row) {
        Crop crop = new Crop(type, type.startIndex);
        crop.data = ItemDatabase.get(plantItemId);
        crop.growthStage = 0; // Start from seed stage

        // Copy growth stage images from the loaded tileset
        for (int i = 0; i < crop.maxGrowthStage + 1; i++) {
            if (type.startIndex + i < cropsTiles.length && cropsTiles[type.startIndex + i] != null) {
                crop.growthImages[i] = cropsTiles[type.startIndex + i].image;
            }
        }

        cropsMap[col][row] = crop; // Store crop instance
        cropsTileNum[col][row] = crop.getCurrentTileIndex();
    }

    public void wateredCrop(int col, int row) {
        Crop crop = cropsMap[col][row];
        if (crop != null) {
            crop.isWatered = true;
        }
    }

    public void updatePlantGrowth() {
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                Crop crop = cropsMap[col][row];
                if (crop != null) {
                    crop.grow();
                    cropsTileNum[col][row] = crop.getCurrentTileIndex();
                }
            }
        }
    }

    public boolean isHarvestable(int col, int row) {
        Crop crop = cropsMap[col][row];
        return crop != null && crop.isHarvestable;
    }

    public boolean hasCropAt(int col, int row) {
        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) return false;
        return cropsMap[col][row] != null;
    }

    public String getSeedItemId(int col, int row) {
        Crop crop = cropsMap[col][row];
        return (crop != null) ? crop.data.seedId : null;
    }

    public String getHarvestItemId(int col, int row) {
        Crop crop = cropsMap[col][row];
        if (crop == null || crop.data == null) {
            return null;
        }

        // Prefer explicit harvestId if set; otherwise fall back to the crop's own ID
        if (crop.data.harvestId != null && !crop.data.harvestId.isEmpty()) {
            return crop.data.harvestId;
        }

        return crop.data.id;
    }

    // Backwards-compat wrapper (old name used by Player)
    public String getSeedPlantId(int col, int row) {
        return getSeedItemId(col, row);
    }

    public void harvestCrop(int col, int row) {
        Crop crop = cropsMap[col][row];
        if (crop == null || !crop.isHarvestable) return;
//        gp.player.addToInventory(getSeedPlantId(col, row));
//        gp.player.addToInventory();

        cropsMap[col][row] = null;
        cropsTileNum[col][row] = 0;
    }

    public void draw(Graphics2D g2) {
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                if (worldX + gp.tileSize < gp.player.worldX - gp.player.screenX ||
                        worldX - gp.tileSize > gp.player.worldX + gp.player.screenX ||
                        worldY + gp.tileSize < gp.player.worldY - gp.player.screenY ||
                        worldY - gp.tileSize > gp.player.worldY + gp.player.screenY)
                    continue;

                drawTile(g2, row, col, screenX, screenY);
            }
        }
    }
}
