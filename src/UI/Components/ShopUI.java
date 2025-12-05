package UI.Components;

import Inventory.Item;
import Player.Player;
import Screen.GamePanel;
import Screen.KeyHandler;
import UI.UIComponent;
import UI.UITheme;
import fileHandler.ItemData;
import fileHandler.ItemDatabase;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple merchant shop overlay.
 * <p>
 * Controls:
 * - W / Up:    move selection up
 * - S / Down:  move selection down
 * - A / Left:  switch to BUY tab
 * - D / Right: switch to SELL tab
 * - E:         buy/sell 1 item
 * - ESC:       close shop
 */
public class ShopUI extends UIComponent {

    private final GamePanel gp;
    private final Player player;
    private final KeyHandler keyH;

    // Item collections
    private final List<ItemData> buyItems = new ArrayList<>();
    private final List<Item> sellItems = new ArrayList<>();

    // Selection state
    private int selectedBuyIndex = 0;
    private int selectedSellIndex = 0;
    private boolean inBuyMode = true; // true = BUY tab, false = SELL tab

    // List/scroll config (can tweak later)
    private static final int VISIBLE_ROWS = 6;
    private static final int ROW_SPACING = 6;

    private int buyScrollOffset = 0;
    private int sellScrollOffset = 0;

    private boolean lastUp, lastDown, lastLeft, lastRight, lastInteract, lastEsc;

    public ShopUI(GamePanel gp, Player player, KeyHandler keyH) {
        this.gp = gp;
        this.player = player;
        this.keyH = keyH;

        // Cover the whole screen logically
        this.x = 0;
        this.y = 0;
        this.width = gp.screenWidth;
        this.height = gp.screenHeight;

        // Hard-coded merchant stock for now
        addBuyItem("WHEAT_SEED");
        addBuyItem("POTATO_SEED");
    }

    private void addBuyItem(String id) {
        ItemData data = ItemDatabase.get(id);
        if (data != null && data.price > 0) {
            buyItems.add(data);
        }
    }

    @Override
    public void update() {
        // Build sell list every frame from player's inventory
        sellItems.clear();
        for (Item i : player.inventory) {
            if (i == null || i.data == null) continue;
            if ("CROP".equals(i.data.type) && i.data.price > 0 && i.quantity > 0) {
                sellItems.add(i);
            }
        }
        if (selectedSellIndex >= sellItems.size()) {
            selectedSellIndex = sellItems.isEmpty() ? 0 : sellItems.size() - 1;
        }

        // Edge detection for keys
        boolean up = keyH.upPressed;
        boolean down = keyH.downPressed;
        boolean left = keyH.leftPressed;
        boolean right = keyH.rightPressed;
        boolean interact = keyH.interactPressed;
        boolean esc = keyH.escPressed;

        // Navigation (list-style, with scroll window)
        if (up && !lastUp) {
            if (inBuyMode && !buyItems.isEmpty()) {
                selectedBuyIndex = Math.max(0, selectedBuyIndex - 1);
            } else if (!inBuyMode && !sellItems.isEmpty()) {
                selectedSellIndex = Math.max(0, selectedSellIndex - 1);
            }
        }
        if (down && !lastDown) {
            if (inBuyMode && !buyItems.isEmpty()) {
                selectedBuyIndex = Math.min(buyItems.size() - 1, selectedBuyIndex + 1);
            } else if (!inBuyMode && !sellItems.isEmpty()) {
                selectedSellIndex = Math.min(sellItems.size() - 1, selectedSellIndex + 1);
            }
        }

        // Adjust scroll windows
        if (inBuyMode) {
            int maxOffset = Math.max(0, buyItems.size() - VISIBLE_ROWS);
            if (selectedBuyIndex < buyScrollOffset) {
                buyScrollOffset = selectedBuyIndex;
            }
            if (selectedBuyIndex >= buyScrollOffset + VISIBLE_ROWS) {
                buyScrollOffset = selectedBuyIndex - VISIBLE_ROWS + 1;
            }
            if (buyScrollOffset > maxOffset) buyScrollOffset = maxOffset;
        } else {
            int maxOffset = Math.max(0, sellItems.size() - VISIBLE_ROWS);
            if (selectedSellIndex < sellScrollOffset) {
                sellScrollOffset = selectedSellIndex;
            }
            if (selectedSellIndex >= sellScrollOffset + VISIBLE_ROWS) {
                sellScrollOffset = selectedSellIndex - VISIBLE_ROWS + 1;
            }
            if (sellScrollOffset > maxOffset) sellScrollOffset = maxOffset;
        }

        // Tab switching
        if (left && !lastLeft) {
            inBuyMode = true;
        }
        if (right && !lastRight) {
            inBuyMode = false;
        }

        // Confirm buy/sell
        if (interact && !lastInteract) {
            if (inBuyMode) {
                buySelectedItem();
            } else {
                sellSelectedItem();
            }
            keyH.interactPressed = false; // consume
        }

        // Close shop
        if (esc && !lastEsc) {
            gp.closeShop();
            keyH.escPressed = false;
        }

        lastUp = up;
        lastDown = down;
        lastLeft = left;
        lastRight = right;
        lastInteract = interact;
        lastEsc = esc;
    }

    private void buySelectedItem() {
        if (buyItems.isEmpty()) return;
        ItemData data = buyItems.get(selectedBuyIndex);
        if (data == null) return;

        int price = data.price;
        if (price <= 0) return;

        if (player.gold < price) {
            System.out.println("Not enough gold to buy " + data.id);
            return;
        }

        player.gold -= price;
        player.addToInventory(data.id);
        System.out.println("Bought 1 " + data.id + " for " + price + " gold. Remaining gold: " + player.gold);
    }

    private void sellSelectedItem() {
        if (sellItems.isEmpty()) return;
        Item item = sellItems.get(selectedSellIndex);
        if (item == null || item.data == null) return;

        int price = item.data.price;
        if (price <= 0) return;

        player.gold += price;
        item.quantity--;

        if (item.quantity <= 0) {
            player.inventory.remove(item);
            // remove from any hotbar slot referencing this item
            for (int i = 0; i < player.hotbar.size(); i++) {
                if (player.hotbar.get(i) == item) {
                    player.hotbar.set(i, null);
                }
            }
        }

        System.out.println("Sold 1 " + item.data.id + " for " + price + " gold. Total gold: " + player.gold);
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!visible) return;

        // Darken background
        g2.setColor(UITheme.OVERLAY_DARK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int panelWidth = gp.screenWidth - gp.tileSize * 8;
        int panelHeight = gp.screenHeight - gp.tileSize * 4;
        int panelX = (gp.screenWidth - panelWidth) / 2;
        int panelY = (gp.screenHeight - panelHeight) / 2;

        // Main box
        g2.setColor(UITheme.PANEL_BG);
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);

        g2.setColor(UITheme.PANEL_BORDER);
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);

        // Header
        g2.setFont(UITheme.FONT_TITLE);
        String title = "Merchant Shop";
        String goldText = "Gold: " + player.gold;
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int goldWidth = fm.stringWidth(goldText);

        int headerY = panelY + fm.getAscent() + 10;
        g2.drawString(title, panelX + 20, headerY);
        g2.drawString(goldText, panelX + panelWidth - goldWidth - 20, headerY);

        g2.setFont(UITheme.FONT_DEFAULT);
        fm = g2.getFontMetrics();

        // Tabs (BUY / SELL)
        int tabsY = headerY + 20;
        int tabWidth = 80;
        int tabHeight = 22;
        int tabSpacing = 10;
        int buyTabX = panelX + 20;
        int sellTabX = buyTabX + tabWidth + tabSpacing;

        // BUY tab
        g2.setColor(inBuyMode ? UITheme.HIGHLIGHT_SOFT : UITheme.SLOT_BG);
        g2.fillRoundRect(buyTabX, tabsY, tabWidth, tabHeight, 8, 8);
        g2.setColor(UITheme.TEXT_PRIMARY);
        String buyText = "BUY";
        int buyTextWidth = fm.stringWidth(buyText);
        g2.drawString(buyText,
                buyTabX + (tabWidth - buyTextWidth) / 2,
                tabsY + (tabHeight + fm.getAscent()) / 2 - 2);

        // SELL tab
        g2.setColor(!inBuyMode ? UITheme.HIGHLIGHT_SOFT : UITheme.SLOT_BG);
        g2.fillRoundRect(sellTabX, tabsY, tabWidth, tabHeight, 8, 8);
        g2.setColor(UITheme.TEXT_PRIMARY);
        String sellText = "SELL";
        int sellTextWidth = fm.stringWidth(sellText);
        g2.drawString(sellText,
                sellTabX + (tabWidth - sellTextWidth) / 2,
                tabsY + (tabHeight + fm.getAscent()) / 2 - 2);

        // List area
        int listX = panelX + 40;
        int listY = tabsY + tabHeight + 25;
        int listWidth = panelWidth - 80;
        int rowHeight = gp.tileSize + ROW_SPACING;

        if (inBuyMode) {
            int start = buyScrollOffset;
            int end = Math.min(buyItems.size(), buyScrollOffset + VISIBLE_ROWS);

            for (int i = start; i < end; i++) {
                ItemData data = buyItems.get(i);
                if (data == null) continue;

                int row = i - start;
                int y = listY + row * rowHeight;

                boolean selected = (i == selectedBuyIndex);

                // Row background
                g2.setColor(selected ? UITheme.HIGHLIGHT_SOFT : UITheme.SLOT_BG);
                g2.fillRoundRect(listX, y, listWidth, gp.tileSize, 8, 8);

                // Icon slot
                int slotSize = gp.tileSize - 6;
                int slotX = listX + 6;
                int slotY = y + 3;

                java.awt.image.BufferedImage icon = data.icon;
                String label = (icon == null) ? data.id : "";

                UIItemSlot slot = new UIItemSlot(
                        slotX,
                        slotY,
                        slotSize,
                        icon,
                        label,
                        0,
                        false
                );
                slot.draw(g2);

                // Text: name and price
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.FONT_DEFAULT);
                String nameText = data.name != null ? data.name : data.id;
                String priceText = data.price + "g";

                int textBaseY = y + gp.tileSize / 2 + fm.getAscent() / 2 - 4;

                // Name on the left (after icon)
                int nameX = slotX + slotSize + 10;
                g2.drawString(nameText, nameX, textBaseY);

                // Price on the right
                int priceWidth = fm.stringWidth(priceText);
                int priceX = listX + listWidth - priceWidth - 10;
                g2.drawString(priceText, priceX, textBaseY);
            }

            // Scroll indicators
            if (buyScrollOffset > 0) {
                g2.setColor(UITheme.TEXT_MUTED);
                g2.drawString("^", listX + listWidth / 2, listY - 4);
            }
            if (buyScrollOffset + VISIBLE_ROWS < buyItems.size()) {
                g2.setColor(UITheme.TEXT_MUTED);
                g2.drawString("v", listX + listWidth / 2, listY + VISIBLE_ROWS * rowHeight);
            }
        } else {
            if (sellItems.isEmpty()) {
                // Centered message
                g2.setColor(UITheme.TEXT_MUTED);
                String msg = "YOU GOT NO ITEM";
                int msgWidth = fm.stringWidth(msg);
                int msgX = panelX + (panelWidth - msgWidth) / 2;
                int msgY = panelY + panelHeight / 2;
                g2.drawString(msg, msgX, msgY);
            } else {
                int start = sellScrollOffset;
                int end = Math.min(sellItems.size(), sellScrollOffset + VISIBLE_ROWS);

                for (int i = start; i < end; i++) {
                    Item item = sellItems.get(i);
                    if (item == null || item.data == null) continue;

                    int row = i - start;
                    int y = listY + row * rowHeight;

                    boolean selected = (i == selectedSellIndex);

                    // Row background
                    g2.setColor(selected ? UITheme.HIGHLIGHT_SOFT : UITheme.SLOT_BG);
                    g2.fillRoundRect(listX, y, listWidth, gp.tileSize, 8, 8);

                    // Icon slot
                    int slotSize = gp.tileSize - 6;
                    int slotX = listX + 6;
                    int slotY = y + 3;

                    java.awt.image.BufferedImage icon = item.data.icon;
                    String label = (icon == null) ? item.data.id : "";

                    UIItemSlot slot = new UIItemSlot(
                            slotX,
                            slotY,
                            slotSize,
                            icon,
                            label,
                            item.quantity,
                            false
                    );
                    slot.draw(g2);

                    // Text: name and price
                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(UITheme.FONT_DEFAULT);
                    String nameText = item.data.name != null ? item.data.name : item.data.id;
                    String priceText = item.data.price + "g";

                    int textBaseY = y + gp.tileSize / 2 + fm.getAscent() / 2 - 4;

                    int nameX = slotX + slotSize + 10;
                    g2.drawString(nameText + " x" + item.quantity, nameX, textBaseY);

                    int priceWidth = fm.stringWidth(priceText);
                    int priceX = listX + listWidth - priceWidth - 10;
                    g2.drawString(priceText, priceX, textBaseY);
                }

                // Scroll indicators
                if (sellScrollOffset > 0) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.drawString("^", listX + listWidth / 2, listY - 4);
                }
                if (sellScrollOffset + VISIBLE_ROWS < sellItems.size()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.drawString("v", listX + listWidth / 2, listY + VISIBLE_ROWS * rowHeight);
                }
            }
        }

        // Footer help text
        g2.setColor(UITheme.TEXT_MUTED);
        String help = "W/S: Move  A/D: Switch tab  E: Buy/Sell  ESC: Close";
        int helpWidth = fm.stringWidth(help);
        g2.drawString(help, panelX + (panelWidth - helpWidth) / 2, panelY + panelHeight - 20);
    }
}
