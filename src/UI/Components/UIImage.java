package UI.Components;

import UI.UIComponent;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * to make image in the UI
 */
public class UIImage extends UIComponent {
    private BufferedImage img;

    public UIImage(int x, int y, BufferedImage img) {
        super(x, y, img.getWidth(), img.getHeight());
        this.img = img;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!visible) return;
        g2.drawImage(img, x, y, width, height, null);
    }
}
