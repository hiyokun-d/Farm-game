# RPG Farm – Student Farming RPG Prototype

## 1. Overview

This project is a small 2D farming RPG prototype written in **Java** using **Swing** for rendering. You control a farmer in a tile-based world, till soil, plant and water crops, harvest them, and trade with a merchant.

> **Disclaimer**  
> This is a **non‑commercial student project**, created in about **three weeks** and **strongly inspired by my favourite farming games**. It is intended only for learning and demonstration purposes.
> and this one cause of the i'm to lazy to write the markdown i'll give the AI for this one, pls don't be mad at me 
---

## 2. Screenshots & Logo (placeholders)

Your lecturer will probably want to see what the game looks like. Replace the image paths below with your actual files.

### Game Logo

```text
TODO: place your logo file in e.g. `assets/docs/logo.png` and update the path below.
```

```markdown
![Game Logo](assets/docs/logo.png)
```

### Gameplay Screenshot

```text
TODO: take an in‑game screenshot (player, farm, crops, UI) and save it as e.g. `assets/docs/screenshot-gameplay.png`, then update the path below.
```

```markdown
![Gameplay Screenshot](assets/docs/screenshot-gameplay.png)
```

---

## 3. How to Install & Run the Game (IMPORTANT)

### 3.1. Requirements

- **Java Development Kit (JDK) 17+** (8+ should work, but 17 is recommended).
- Any OS that can run Java (tested with desktop Java on Mac/Windows).

Make sure `javac` and `java` commands work in your terminal:

```bash
javac -version
java -version
```

### 3.2. Getting the source code

1. Download or clone the project into a directory, for example:
   - `/Users/.../RPG-game`  (on macOS)  
   The existing sources live in the `src` folder.

2. The project is **plain Java** (no Maven/Gradle wrapper committed), so compilation is done directly with `javac` or via an IDE.

### 3.3. Running from the command line

From the project root (where `src` is located):

```bash
# 1. Compile all Java sources into the out/ folder
mkdir -p out
javac -d out $(find src -name "*.java")

# 2. Run the main class
java -cp out Main
```

- `Main` (in `src/Main.java`) creates a Swing `JFrame`, loads configuration and item data, and starts the main game loop.

### 3.4. Running from an IDE (IntelliJ / Eclipse / NetBeans)

1. Open/import the project as a **Java project**.
2. Mark `src` as the **source root** if needed.
3. Set the **run configuration** main class to `Main`.
4. Run the project.

This should open a window titled **"RPG game"** (or **"RPG Farm"** from the main menu), showing either the main menu or the game world.

---

## 4. How to Play

### 4.1. Main Menu

When you start the game you see a simple **main menu** with three buttons:

- **Play** – start the game and enter the world.
- **Credits** – currently prints a placeholder to the console (you can extend this later to show real credits).
- **???** – placeholder for any future feature you want.

You can control the menu in two ways:

- **Keyboard**
  - **W / Up Arrow** – move selection up.
  - **S / Down Arrow** – move selection down.
  - **Enter** – activate the selected button.

- **Mouse**
  - Move the mouse over a button to highlight it.
  - Click to activate it.

### 4.2. Basic Controls (in-game)

Once you press **Play**, you control the farmer in a scrolling world.

**Movement & interaction**

- **W / Up Arrow** – move up.
- **S / Down Arrow** – move down.
- **A / Left Arrow** – move left.
- **D / Right Arrow** – move right.
- **E** – interact (context-sensitive):
  - Till soil (with hoe).
  - Plant seeds (with seed item selected).
  - Water crops (with watering can).
  - Harvest crops (when ready).
  - Talk to/trigger nearby NPCs (e.g., merchant shop).
  - Refill watering can when standing in a lake tile.
- **ESC** – currently used for some debugging (inventory print) and closing the shop.
- **Number keys 1–0** – select hotbar slots 1–10.

### 4.3. UI Elements

- **Hotbar** (bottom of the screen): shows up to 10 items.
  - Selected slot is highlighted.
  - Crop items can have special icons.
  - The watering can shows **remaining water** as the number overlay.
- **Gold label** (top-left): shows current gold.
- **Water label** (top-left, when watering can selected): 
  - Example: `Water: 15/20` (current / max capacity).
- **Interaction hint** (bottom-left, above hotbar): context text like:
  - `Press E to till soil`
  - `Press E to plant Wheat Seed`
  - `Press E to water crop`
  - `Press E to harvest`
  - `Press E to refill watering can`
  - `Go stand in a lake tile to refill`

### 4.4. Farming Loop Mechanics

1. **Tilling soil**
   - Equip the **hoe** (HOE) in your hotbar (one of the first slots).
   - Stand on a **grass tile**.
   - You will see a hint: `Press E to till soil`.
   - Press **E**: grass converts to **soil** (tilled dirt) using `Render_tiles.convertGrassToDirtTile`.

2. **Planting seeds**
   - Select a **seed item** (`WHEAT_SEED`, `POTATO_SEED`, etc.) on the hotbar.
   - Stand on a **tilled soil** tile without an existing crop.
   - Hint: `Press E to plant <Seed Name>`.
   - Press **E**: a `Crop` object is created on that tile via `Render_Objects.plantCrop`.
   - One seed is consumed from your inventory.

3. **Watering crops**
   - Select the **watering can** (WATERING_CAN) on the hotbar.
   - Stand on a tile with a crop that is not yet harvestable.
   - If it’s dry, hint: `Press E to water crop`.
   - Press **E**:
     - The crop’s `isWatered` flag is set to `true` and its next growth timer is updated.
     - The watering can’s `waterCapacity` is reduced by a fixed amount (e.g., 5 units).
     - A dark box overlay appears on the tile (soil looks **soaked/darker**), and the tile outline becomes **blue**.

4. **Refilling the watering can**
   - Move to a **lake tile** (`standingOn == "LAKE"`).
   - With watering can selected, if it’s not full, hint: `Press E to refill watering can`.
   - Press **E**: `waterCapacity` is reset to `maxCapacity`.

5. **Crop growth & harvest**
   - Each crop type has a **growth curve** defined in `data/itemData.dat` (field `growthTimes`).
   - Growth only advances if the crop has been **watered** for the current stage.
   - After enough time and stages, `isHarvestable` becomes `true`:
     - The tile outline changes to **yellow**.
   - Stand on the crop and press **E**:
     - You harvest it using `Player.harvestCrop()` / `Render_Objects.harvestCrop()`.
     - One or more crop items (e.g., `WHEAT`) are added to your inventory.
     - The soil is optionally restored to grass (depending on logic) when you harvest.

### 4.5. Merchant & Shop

- There is a **Merchant NPC** in the world.
- When you are close to the merchant and press **E**:
  - The **shop UI** opens (`ShopUI`).
  - Player movement is temporarily disabled while the shop is open.
- In the shop:
  - Use **W/S or Up/Down** to move the selection.
  - Use **A/D or Left/Right** to switch between **BUY** and **SELL** tabs.
  - Press **E** to buy/sell the selected item.
  - Press **ESC** to close the shop and return to the game.
- Buying seeds **costs gold**; selling crops **gives gold**. Prices are defined in `itemData.dat`.

### 4.6. Inventory & Hotbar

- Internal inventory is an `ArrayList<Item>`.
- Hotbar is a fixed-size list (10 slots) that references items in the inventory.
- When adding items, the code tries to **stack** them if possible (`stackable == true`).
- On planting seeds, their quantity decreases and they are removed when quantity reaches 0.

---

## 5. Project Structure & Important Classes

The code is organized into several packages under `src/`.

### 5.1. Entry point & main loop

- **`Main`** (`src/Main.java`)
  - Loads player and item data using `Filehandler.load()`.
  - Creates the main Swing window (`JFrame`), adds a `GamePanel`, and starts the game loop.

- **`Screen.GamePanel`** (`src/Screen/GamePanel.java`)
  - Central game panel and main loop (`Runnable`).
  - Defines tile size, world size, screen size.
  - Holds references to:
    - `Render_tiles` (background world tiles).
    - `Render_Objects` (crops and object tiles).
    - `Player`.
    - `CollisionChecker`.
    - NPC list (e.g., `MerchantNPC`).
    - `UIContainer` and UI elements like `ShopUI`.
  - Manages states:
    - **Main menu** vs **In-game**.
    - **Shop open** vs normal gameplay.
  - `update()` and `paintComponent()` implement the fixed-timestep loop.

- **`Screen.KeyHandler`** (`src/Screen/KeyHandler.java`)
  - Implements `KeyListener`.
  - Tracks keys like movement (WASD/arrows), interaction (E), hotbar number keys, ESC, TAB, ENTER.
  - Game code reads these booleans every frame.

### 5.2. Entity system

- **`Entity.Entity`** (`src/Entity/Entity.java`)
  - Base class for movable entities.
  - Fields: `worldX`, `worldY`, `speed`, `width`, `height`, `direction`, `solidArea`, `standingOn`, and sprite animation counters.
  - Provides `updateAnimation(...)` helpers.

- **`Entity.CollisionChecker`** (`src/Entity/CollisionChecker.java`)
  - Uses tile arrays from `Render_tiles` and `Render_Objects` to check collisions.
  - Updates `entity.standingOn` based on the tile under the entity (grass, soil, lake, path, floor, etc.).
  - Prevents movement through solid collision tiles and some water tiles.

### 5.3. Player & NPCs

- **`Player.Player`** (`src/Player/Player.java`)
  - Extends `Entity`.
  - Handles:
    - Movement (reading `KeyHandler` flags).
    - Inventory and hotbar management.
    - Hoeing tiles, planting seeds, watering crops, harvesting.
    - Interacting with NPCs and opening the shop.
    - Drawing the player and overlay UI (hotbar, gold, water, tile outline).
  - Computes `hoverCol`, `hoverRow` to draw the tile outline under the player.
  - Uses `interactionHint` string to display context prompts.

- **`NPC.BaseNPC` / `NPC.MerchantNPC`**
  - BaseNPC: abstract base for NPCs (position, talking state, drawing dialogue, etc.).
  - MerchantNPC: concrete merchant, opens the shop when interacted with.

### 5.4. Tiles & Rendering

- **`Tile.Tile`**
  - Basic tile with image, collision flag, and ID.

- **`Tile.Render_tiles`** (`src/Tile/Render_tiles.java`)
  - Loads tilesets from `assets/Tiled_files/`.
  - Builds arrays for different layers: grass, water, dirt/soil, lake, boat, bridge, paths, furniture, walls, floor.
  - Reads map layout from CSV files under `data/tileData/level3/`.
  - Draws visible tiles in the correct order for layering.
  - Provides helper methods:
    - `convertGrassToDirtTile(int col, int row)`.
    - `convertDirtToGrassTile(int col, int row)`.

- **`Tile.Crop`** (`src/Tile/Crop.java`)
  - Extends `Tile` to represent a crop instance on a tile.
  - Fields: `type` (enum `CropType`), `data` (`ItemData`), `isHarvestable`, `isWatered`, `growthStage`, `growthImages[]`, `lastGrowthTime`.
  - `grow()` only advances if `isWatered == true`, then sets `isWatered = false` again.

- **`Tile.Render_Objects`** (`src/Tile/Render_Objects.java`)
  - Manages crop and tool tilesets.
  - Arrays:
    - `cropsTiles[]` – tile images for crops.
    - `toolTiles[]` – tile images for tool icons.
    - `cropsMap[col][row]` – actual `Crop` instances in the world.
  - Responsibilities:
    - `plantCrop(...)` – create a new `Crop` on a tile.
    - `wateredCrop(...)` – mark a crop as watered.
    - `updatePlantGrowth()` – call `grow()` for each existing crop.
    - `isHarvestable(...)`, `hasCropAt(...)`, `getSeedItemId(...)`, `getHarvestItemId(...)`.
    - `harvestCrop(...)` – remove a crop from the map.
  - Visuals:
    - Draws a **dark overlay box** on watered crops (soil looks wet).

### 5.5. Inventory & Items

- **`Inventory.Item`** (`src/Inventory/Item.java`)
  - Represents an item instance in the player’s inventory.
  - Contains reference to `ItemData`, quantity, stackable flag, icons.

- **`fileHandler.ItemData`** (`src/fileHandler/ItemData.java`)
  - Data model for items loaded from file.
  - Fields include:
    - `type` – `TOOL`, `CROP`, `SEED`.
    - `id`, `name`, `icon_id`, `stackable`.
    - `price` – used in shop.
    - `waterCapacity`, `maxCapacity` – for watering can.
    - `stages`, `growthTimes[]`, `seedId`, `harvestId`, `plantId`, `cropType`.

- **`fileHandler.ItemDatabase`** (`src/fileHandler/ItemDatabase.java`)
  - Static registry `HashMap<String, ItemData>`.
  - Items are registered during start-up from `itemData.dat`.

- **`fileHandler.Filehandler`** (`src/fileHandler/Filehandler.java`)
  - Loads **player config** from `data/config_PLAYER.txt`.
  - Parses **item data** from `data/itemData.dat` using a simple INI-like format:

    ```
    [TOOL]
    id=WATERING_CAN
    waterCapacity=20
    maxCapacity=20
    price=1000
    ...
    ```

  - Provides `getInt`, `getString`, `getBoolean` for configuration.

### 5.6. Types & Enums

- **`types.CropType`** (`src/types/CropType.java`)
  - Enum for crop types (e.g., `WHEAT`, `POTATO`).
  - Contains `startIndex` used to pick the correct row in the crop tileset.

### 5.7. UI System

- **`UI.UIComponent`**
  - Abstract base for UI elements (position, size, `draw`, `update`, visibility).

- **`UI.UIContainer`**
  - Manages a collection of `UIComponent`s.
  - Handles safe add/remove during update/draw.

- **`UI.UITheme`**
  - Centralized colors and fonts (panel backgrounds, button colors, text colors, fonts for labels, titles, etc.).

- **UI Components (`src/UI/Components/`):**
  - `UIButton` – basic clickable button.
  - `UIBox` – colored box.
  - `UILabel` – text label with alignment.
  - `UIItemSlot` – inventory/hotbar slot; can draw icon + quantity overlay.
  - `ShopUI` – full-screen shop overlay with BUY/SELL lists using keyboard navigation.

---

## 6. Data Files & Assets

- **Tile data CSVs** under `data/tileData/level3/`
  - Define which tile index appears at each world coordinate for grass, water, lake, paths, etc.

- **Item data** under `data/itemData.dat`
  - Text file describing tools, crops, and seeds.
  - Example blocks:

    ```text
    [TOOL]
    id=HOE
    icon_id=2
    name=Wooden Hoe
    stackable=false
    price=1000

    [CROP]
    id=WHEAT
    icon_id=5
    name=wheat
    seedId=WHEAT_SEED
    harvestId=WHEAT
    growthTimes=1000,2000,3000,4000
    stackable=true
    price=500

    [SEED]
    id=WHEAT_SEED
    icon_id=0
    name=wheat seed
    plantId=WHEAT
    stackable=true
    price=1000
    ```

- **Sprite assets** under `assets/`
  - Characters (player, merchant sprite sheet).
  - Tilesets for grass, dirt, water, house, furniture, etc.
  - Crop and tool tiles used both in-world and as item icons.

---

## 7. Notes for the Lecturer

- The project demonstrates:
  - Basic **game loop** and rendering using Java Swing.
  - A simple **entity system** with collision detection.
  - **Tile-based** world rendering with multiple layers.
  - A basic **farming mechanic** (tilling, planting, watering, refilling, harvesting).
  - A simple **economy system** (gold, shop, item prices).
  - A small **UI framework** built from scratch (labels, buttons, item slots, overlays).
  - External **data-driven design** using text files (`itemData.dat`, CSV tile maps).

You can run the game following the commands in section **3.3** or via any Java IDE, then use this README as a guide to the mechanics and code structure during your evaluation or presentation.