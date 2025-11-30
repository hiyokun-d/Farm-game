package Tile;

import Screen.GamePanel;
import types.CropType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class TileManager {
    private final GamePanel gp;
    private final int ORIGINAL_TILE_SIZE = 16;
    private final String ASSET_PATH = "assets/Tiled_files/";
    private final String dataPath = "data/tileData/level3/";

    public Tile[] tiles;
    public int[][] mapTileNum;

    public Tile[] grassTiles;
    public int[][] grassTileNum;

    public Tile[] waterTiles;
    public int[][] waterTileNum;

    public Tile[] dirtTiles;
    public int[][] dirtTileNum;

    public Crop[] cropsTiles;
    public int[][] cropsTileNum;

    public TileManager(GamePanel gp) throws IOException {
        this.gp = gp;

        tiles = new Tile[4500];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        grassTiles = new Tile[1500];
        grassTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        waterTiles = new Tile[1500];
        waterTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        dirtTiles = new Tile[1500];
        dirtTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        cropsTiles = new Crop[500];
        cropsTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                dirtTileNum[col][row] = 0; // start with no dirt
                cropsTileNum[col][row] = 0; // start with no crops
            }
        }

        setupTiles();
    }

    private void setupTiles() throws IOException {
        loadTileSet("Water.png", "WATER", false, waterTiles, 0);
        loadTileSet("Grass.png", "GRASS", true, grassTiles, 0);
        loadTileSet("Bitmask_references 1.png", "SOLID_COLLISION", true, tiles, 1);
        loadTileSet("Tilled_Dirt_Wide.png", "SOIL", true, dirtTiles, 0);

        loadCropsSet("Objects/Basic_Plants.png", cropsTiles, 0);

        loadCSV("map_grass.csv", grassTileNum);
        loadCSV("map_water.csv", waterTileNum);
        loadCSV("map_solidCollision.csv", mapTileNum);
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

    // UNCOMMENT THIS IF WE NEED TO LOAD CROPS LIKE THE NORMAL TILES WITH A COMPLETE PARAM
    //    private void loadCropsSet(String fileName, String ID, boolean isCollision, Tile[] targetArray, int startIndex) throws IOException {
//        BufferedImage tileset = ImageIO.read(new File(ASSET_PATH + fileName));
//
//        int tilesetCols = tileset.getWidth() / ORIGINAL_TILE_SIZE;
//        int tilesetRows = tileset.getHeight() / ORIGINAL_TILE_SIZE;
//        int index = startIndex;
//
//        for (int row = 0; row < tilesetRows; row++) {
//            for (int col = 0; col < tilesetCols; col++) {
//
//                BufferedImage tileImage = tileset.getSubimage(
//                        col * ORIGINAL_TILE_SIZE,
//                        row * ORIGINAL_TILE_SIZE,
//                        ORIGINAL_TILE_SIZE,
//                        ORIGINAL_TILE_SIZE
//                );
//
//                Tile tile = new Tile();
//                tile.image = tileImage;
//                tile.id = ID;
//                tile.collision = isCollision;
//                targetArray[index] = tile;
//
//                index++;
//            }
//        }
//    }

    private void loadTileSet(String fileName, boolean isCollision, Tile[] targetArray, int startIndex) throws IOException {
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

                Tile tile = new Tile();
                tile.image = tileImage;
                tile.collision = isCollision;
                targetArray[index] = tile;

                index++;
            }
        }
    }

    private void loadTileSet(String fileName, String ID, boolean isCollision, Tile[] targetArray, int startIndex) throws IOException {
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

                Tile tile = new Tile();
                tile.image = tileImage;
                tile.id = ID;
                tile.collision = isCollision;
                targetArray[index] = tile;

                index++;
            }
        }
    }

    private void loadCSV(String path, int[][] mapArray) {
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(dataPath + path))) {

            String line;
            int row = 0;

            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(",");
                for (int col = 0; col < numbers.length; col++) {
                    int id = Integer.parseInt(numbers[col].trim());
                    if (id < 0) continue;
                    mapArray[col][row] = id;
                    if (mapArray == mapTileNum)
                        mapTileNum[col][row] = id;
                }
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawTile(Graphics2D g2, int row, int col, int screenX, int screenY, Tile[] tileArr, int[][] tilenum, boolean drawZero) {
        int tileIndex = tilenum[col][row];
        g2.drawImage(tileArr[tileIndex].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    private void drawTile(Graphics2D g2, int row, int col, int screenX, int screenY, Tile[] tileArr, int[][] tilenum) {
        int tileIndex = tilenum[col][row];
        if (tileIndex == 0) return;
        g2.drawImage(tileArr[tileIndex].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
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

    public void convertGrassToDirtTile(int col, int row) {
        grassTileNum[col][row] = 0;  // remove grass
        dirtTileNum[col][row] = 12;   // add dirt
    }

    public void convertDirtToGrassTile(int col, int row) {
        dirtTileNum[col][row] = 0;   // remove dirt
        grassTileNum[col][row] = 12;// replace with grass tile index 1 (adjust if needed)
    }

    public void plantCrop(CropType type, int col, int row) {
        int tileIndex = type.rowIndex;

        Crop crop = cropsTiles[cropsTileNum[col][row]];
        crop.setID(type.id);

        cropsTileNum[col][row] = tileIndex;
        System.out.println(crop.getId() + " planted at (" + col + ", " + row + ")");

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

                //! DON'T DO ANYTHING IN THIS YOU'LL BROKE THE LAYER YOU STUPID SHIT MOTHERFUXKER, AM TIRED OF FIXING IT
                //! regards THIS IS HIYO!!
                drawTile(g2, row, col, screenX, screenY, waterTiles, waterTileNum, true);
                drawTile(g2, row, col, screenX, screenY, dirtTiles, dirtTileNum);
                drawTile(g2, row, col, screenX, screenY, cropsTiles, cropsTileNum);
                drawTile(g2, row, col, screenX, screenY, grassTiles, grassTileNum);
                //------------------------------------------------------------------------------\\

            }
        }
    }
}
