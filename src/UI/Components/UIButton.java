package UI.Components;

import UI.UIComponent;
import UI.UITheme;

import java.awt.*;

/**
 * A basic button UI component.
 */

public class UIButton extends UIComponent {

    public String label;
    public Color background = UITheme.BUTTON_BG;
    public Color hoverColor = UITheme.BUTTON_HOVER;
    public Color textColor = UITheme.TEXT_PRIMARY;

    private boolean hovered = false;

    public UIButton(int x, int y, int width, int height, String label) {
        super(x, y, width, height);
        this.label = label;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(hovered ? hoverColor : background);
        g2.fillRect(x, y, width, height);

        g2.setColor(textColor);
        g2.setFont(UITheme.FONT_DEFAULT);
        FontMetrics metrics = g2.getFontMetrics();
        int textWidth = metrics.stringWidth(label);
        int textHeight = metrics.getHeight();
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + textHeight) / 2 - metrics.getDescent();
        g2.drawString(label, textX, textY);
    }

    @Override
    public void update() {
        if (!visible) return;
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        hovered = isMouseInside(mousePos.x, mousePos.y);
    }

    @Override
    public void onClick() {
        System.out.println("Button '" + label + "' clicked!");
    }

    @Override
    public void onHover() {
        System.out.println("Button '" + label + "' hovered!");
    }
}
