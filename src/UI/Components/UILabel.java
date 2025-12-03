package UI.Components;

import UI.UIComponent;

import java.awt.*;

/**
 * to make text in the UI
 * HIDUP BLONDE!!!!
 */
public class UILabel extends UIComponent {

    public String text;
    public Color color;
    public Font font = new Font("Arial", Font.PLAIN, 12);
    public String setAlign = "TOP-LEFT"; // CENTER, TOP-LEFT, TOP-CENTER, TOP-RIGHT, BOTTOM-LEFT, BOTTOM-CENTER, BOTTOM-RIGHT

    public UILabel(int x, int y, String text, Color color) {
        super(x, y, 0, 0);
        this.text = text;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setFont(font);
        g2.setColor(color);
        FontMetrics metrics = g2.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        int drawX = x;
        int drawY = y;

        switch (setAlign) {
            case "CENTER" -> {
                drawX = x - textWidth / 2;
                drawY = y + textHeight / 2;
            }
            case "TOP-LEFT" -> {
                drawX = x;
                drawY = y + textHeight;
            }
            case "TOP-CENTER" -> {
                drawX = x - textWidth / 2;
                drawY = y + textHeight;
            }
            case "TOP-RIGHT" -> {
                drawX = x - textWidth;
                drawY = y + textHeight;
            }
            case "BOTTOM-LEFT" -> {
                drawX = x;
                drawY = y;
            }
            case "BOTTOM-CENTER" -> {
                drawX = x - textWidth / 2;
                drawY = y;
            }
            case "BOTTOM-RIGHT" -> {
                drawX = x - textWidth;
                drawY = y;
            }
        }

        g2.drawString(text, drawX, drawY);
    }
}
