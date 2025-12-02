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

        // Tile boundaries
        int leftCol = left / gp.tileSize;
        int rightCol = right / gp.tileSize;
        int topRow = top / gp.tileSize;
        int bottomRow = bottom / gp.tileSize;

        // Check based on movement direction
        switch (entity.direction) {

            case "up" -> {
                int newTopRow = (top - entity.speed) / gp.tileSize;
                checkTileAt(entity, leftCol, newTopRow);
                checkTileAt(entity, rightCol, newTopRow);
            }

            case "down" -> {
                int newBottomRow = (bottom + entity.speed) / gp.tileSize;
                checkTileAt(entity, leftCol, newBottomRow);
                checkTileAt(entity, rightCol, newBottomRow);
            }

            case "left" -> {
                int newLeftCol = (left - entity.speed) / gp.tileSize;
                checkTileAt(entity, newLeftCol, topRow);
                checkTileAt(entity, newLeftCol, bottomRow);
            }

            case "right" -> {
                int newRightCol = (right + entity.speed) / gp.tileSize;
                checkTileAt(entity, newRightCol, topRow);
                checkTileAt(entity, newRightCol, bottomRow);
            }
        }
    }

    /**
     * Checks if a tile at (col, row) is solid.
     */
    private void checkTileAt(Entity entity, int col, int row) {

        // Out of bounds → treat as solid
        if (col < 0 || col >= gp.maxWorldCol ||
                row < 0 || row >= gp.maxWorldRow) {
            entity.collisionOn = true;
            return;
        }

        entity.standingOn = "none";

        int tileIndex = gp.render_tiles.mapTileNum[col][row];
        int grassIndex = gp.render_tiles.grassTileNum[col][row];
        int dirtIndex = gp.render_tiles.dirtTileNum[col][row];
        int cropsIndex = gp.render_crops.cropsTileNum[col][row];

        if (grassIndex > 0) {
            Tile grassTile = gp.render_tiles.grassTiles[grassIndex];
            if (grassTile != null)
                entity.standingOn = grassTile.id;
        }

        if (dirtIndex > 0) {
            Tile dirtTile = gp.render_tiles.dirtTiles[dirtIndex];
            if (dirtTile != null)
                entity.standingOn = dirtTile.id;
        }

        if (cropsIndex > 0) {
            Tile cropsTile = gp.render_crops.cropsTiles[cropsIndex];

            if (cropsTile != null)
                entity.standingOn = cropsTile.id;
        }

        // 0 or -1 means "no tile"
        if (tileIndex < 0) return;

        Tile tile = gp.render_tiles.tiles[tileIndex];
        if (tile == null) return;

        // COLLISION CHECK
        if ("SOLID_COLLISION".equals(tile.id)) {
            entity.collisionOn = true;
        }
    }
}