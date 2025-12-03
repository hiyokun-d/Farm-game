package UI.Components;

import UI.UIComponent;

import java.awt.*;

/**
 * to make a colored box in the UI
 */

public class UIBox extends UIComponent {
    public Color color;

    public UIBox(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fillRect(x, y, width, height);
    }
}
