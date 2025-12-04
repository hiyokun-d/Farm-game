package Entity;

import Screen.GamePanel;
import Tile.Tile;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        entity.collisionOn = false;

        // World positions of the entity's solid area
        int left = entity.worldX + entity.solidArea.x;
        int right = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int top = entity.worldY + entity.solidArea.y;
        int bottom = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Determine the tile the entity is currently standing on (center of hitbox)
        int centerX = entity.worldX + entity.solidArea.x + entity.solidArea.width / 2;
        int centerY = entity.worldY + entity.solidArea.y + entity.solidArea.height / 2;
        int currentCol = centerX / gp.tileSize;
        int currentRow = centerY / gp.tileSize;
        updateStandingOn(entity, currentCol, currentRow);

        // Tile boundaries around the entity for collision checks
        int leftCol = left / gp.tileSize;
        int rightCol = right / gp.tileSize;
        int topRow = top / gp.tileSize;
        int bottomRow = bottom / gp.tileSize;

        // Check based on movement direction
        switch (entity.direction) {

            case "up" -> {
                int newTopRow = (top - entity.speed) / gp.tileSize;
                checkCollisionAt(entity, leftCol, newTopRow);
                checkCollisionAt(entity, rightCol, newTopRow);
            }

            case "down" -> {
                int newBottomRow = (bottom + entity.speed) / gp.tileSize;
                checkCollisionAt(entity, leftCol, newBottomRow);
                checkCollisionAt(entity, rightCol, newBottomRow);
            }

            case "left" -> {
                int newLeftCol = (left - entity.speed) / gp.tileSize;
                checkCollisionAt(entity, newLeftCol, topRow);
                checkCollisionAt(entity, newLeftCol, bottomRow);
            }

            case "right" -> {
                int newRightCol = (right + entity.speed) / gp.tileSize;
                checkCollisionAt(entity, newRightCol, topRow);
                checkCollisionAt(entity, newRightCol, bottomRow);
            }
        }
    }

    /**
     * Updates the entity.standingOn value based on the tile under the entity's center.
     */
    private void updateStandingOn(Entity entity, int col, int row) {
        entity.standingOn = "none";

        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) return;

        int grassIndex = gp.render_tiles.grassTileNum[col][row];
        int dirtIndex = gp.render_tiles.dirtTileNum[col][row];
        int cropsIndex = gp.render_crops.cropsTileNum[col][row];
        int lakeIndex = gp.render_tiles.lakeTileNum[col][row];

        // Base ground
        if (grassIndex > 0) {
            Tile grassTile = gp.render_tiles.grassTiles[grassIndex];
            if (grassTile != null) entity.standingOn = grassTile.id;
        }

        if (dirtIndex > 0) {
            Tile dirtTile = gp.render_tiles.dirtTiles[dirtIndex];
            if (dirtTile != null) entity.standingOn = dirtTile.id;
        }

        // Crops override ground type
        if (cropsIndex > 0) {
            Tile cropsTile = gp.render_crops.cropsTiles[cropsIndex];
            if (cropsTile != null) entity.standingOn = cropsTile.id;
        }

        // Lake / water can also be treated as ground type
        if (lakeIndex > 0) {
            Tile lakeTile = gp.render_tiles.lakeTiles[lakeIndex];
            if (lakeTile != null) entity.standingOn = lakeTile.id;
        }
    }

    /**
     * Checks if a tile at (col, row) is solid for movement.
     */
    private void checkCollisionAt(Entity entity, int col, int row) {

        // Out of bounds  treat as solid
        if (col < 0 || col >= gp.maxWorldCol ||
                row < 0 || row >= gp.maxWorldRow) {
            entity.collisionOn = true;
            return;
        }

        // SOLID_COLLISION layer
        int tileIndex = gp.render_tiles.mapTileNum[col][row];
        if (tileIndex >= 0) {
            Tile tile = gp.render_tiles.tiles[tileIndex];
            if (tile != null && "SOLID_COLLISION".equals(tile.id)) {
                entity.collisionOn = true;
            }
        }

        // Lake tiles should also block movement
        int lakeIndex = gp.render_tiles.lakeTileNum[col][row];
        if (lakeIndex > 0) {
            Tile lakeTile = gp.render_tiles.lakeTiles[lakeIndex];
            if (lakeTile != null && lakeTile.collision) {
                entity.collisionOn = true;
            }
        }

        // Crops block movement so the player cannot walk through planted crops
        int cropsIndex = gp.render_crops.cropsTileNum[col][row];
        if (cropsIndex > 0) {
            Tile cropsTile = gp.render_crops.cropsTiles[cropsIndex];
            if (cropsTile != null && cropsTile.collision) {
                entity.collisionOn = false;
            }
        }
    }
}