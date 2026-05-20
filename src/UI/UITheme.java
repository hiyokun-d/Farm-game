package UI;

import Screen.GamePanel;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Central place for UI colors, fonts, and spacing.
 *
 * Update values here to restyle the whole UI.
 */
public class UITheme {

    // Colors
    public static final Color OVERLAY_DARK = new Color(0, 0, 0, 150);

    public static final Color PANEL_BG = new Color(40, 40, 40, 230);
    public static final Color PANEL_BORDER = Color.WHITE;

    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_MUTED = Color.LIGHT_GRAY;

    public static final Color HIGHLIGHT = new Color(255, 255, 120);
    public static final Color HIGHLIGHT_SOFT = new Color(80, 80, 30);

    public static final Color SLOT_BG = new Color(70, 70, 70);
    public static final Color SLOT_BG_SELECTED = new Color(255, 255, 120);
    public static final Color SLOT_ITEM_BG = Color.DARK_GRAY;

    public static final Color BUTTON_BG = new Color(60, 60, 60);
    public static final Color BUTTON_HOVER = new Color(90, 90, 90);

    // Fonts
    private static Font loadFont(String path, float size) {
        try {
            InputStream is = GamePanel.class.getResourceAsStream(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 16);
        }
    }

    public static Font FONT_DEFAULT() {
        return loadFont("/resources/fonts/Press_Start_2P/PressStart2P-Regular.ttf", 15f);
    }

    public static Font FONT_SMALL() {
        return loadFont("/resources/fonts/Cormorant_Garamond/CormorantGaramond-VariableFont_wght.ttf", 15f);
    }

    public static Font FONT_TITLE() {
        return loadFont("/resources/fonts/Press_Start_2P/PressStart2P-Regular.ttf", 15f);
    }
}
