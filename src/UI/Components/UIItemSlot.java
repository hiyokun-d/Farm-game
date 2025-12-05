package UI.Components;

import UI.UIComponent;
import UI.UITheme;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Reusable item slot UI element.
 * Can show an icon or text label and an optional quantity overlay.
 */
public class UIItemSlot extends UIComponent {

    private final int size;
    private final boolean selected;

    private BufferedImage icon;
    private String label;
    private int quantity;

    public UIItemSlot(int x, int y, int size,
                      BufferedImage icon,
                      String label,
                      int quantity,
                      boolean selected) {
        super(x, y, size, size);
        this.size = size;
        this.icon = icon;
        this.label = label;
        this.quantity = quantity;
        this.selected = selected;
    }

    public void setIcon(BufferedImage icon) {
        this.icon = icon;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!visible) return;

        // Slot background
        g2.setColor(selected ? UITheme.SLOT_BG_SELECTED : UITheme.SLOT_BG);
        g2.fillRoundRect(x - 4, y - 4, size + 8, size + 8, 8, 8);

        // Inner item area
        g2.setColor(UITheme.SLOT_ITEM_BG);
        g2.fillRect(x, y, size, size);

        // Icon or label
        if (icon != null) {
            g2.drawImage(icon, x + 4, y + 4, size - 8, size - 8, null);
        } else if (label != null && !label.isEmpty()) {
            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(UITheme.TEXT_PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textX = x + (size - textWidth) / 2;
            int textY = y + (size + fm.getAscent()) / 2 - 2;
            g2.drawString(label, textX, textY);
        }

        // Quantity overlay (bottom-right)
        if (quantity > 1) {
            String q = String.valueOf(quantity);
            g2.setFont(UITheme.FONT_SMALL);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(q);
            int textHeight = fm.getHeight();

            int padding = 2;
            int bgWidth = textWidth + padding * 2;
            int bgHeight = textHeight;

            int bgX = x + size - bgWidth;
            int bgY = y + size - bgHeight;

            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(bgX, bgY, bgWidth, bgHeight, 6, 6);

            g2.setColor(UITheme.TEXT_PRIMARY);
            int textX = bgX + padding;
            int textY = bgY + fm.getAscent();
            g2.drawString(q, textX, textY);
        }
    }
}
