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
//    public static final Font FONT_DEFAULT = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_DEFAULT() {
        try {
            InputStream is = GamePanel.class.getResourceAsStream("/resources/fonts/Press_Start_2P/PressStart2P-Regular.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(15f);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.out.println("Failed to load font. Using default.");
            return new Font("Arial", Font.PLAIN, (int) 16);
        }
    }

//    public static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 10);
    public static final Font FONT_SMALL() {
        try {
            InputStream is = GamePanel.class.getResourceAsStream("/resources/fonts/Cormorant_Garamond/CormorantGaramond-VariableFont_wght.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(15f);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.out.println("Failed to load font. Using default.");
            return new Font("Arial", Font.PLAIN, (int) 16);
        }
    }
//    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);

    public static Font FONT_TITLE() {
     try {
         InputStream is = GamePanel.class.getResourceAsStream("/resources/fonts/Press_Start_2P/PressStart2P-Regular.ttf");
         Font font = Font.createFont(Font.TRUETYPE_FONT, is);
         return font.deriveFont(15f);
     } catch (IOException | FontFormatException e) {
         e.printStackTrace();
         System.out.println("Failed to load font. Using default.");
         return new Font("Arial", Font.PLAIN, (int) 16);
     }
    }
}
