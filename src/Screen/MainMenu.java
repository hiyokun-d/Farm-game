package Screen;

import UI.TransitionManager;
import UI.UITheme;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainMenu {
    GamePanel gp;

    public final String[] mainMenuOptions = {"Play", "Credits", "???"};
    public int mainMenuSelectedIndex = 0;
    public boolean mainMenuMouseClicked = false;
    public boolean lastMenuUp = false, lastMenuDown = false, lastMenuEnter = false;

    public BufferedImage logo;

    public MainMenu(GamePanel gp) {
        this.gp = gp;

        try {
            logo = ImageIO.read(getClass().getResourceAsStream("/resources/logo.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("⚠ Failed to load logo image!");
        }
    }

    public void drawMainMenu(Graphics2D g2) {
        // Background
        GradientPaint gpnt = new GradientPaint(
                0, 0, new Color(26, 46, 58),      // top
                0, gp.screenHeight, new Color(12, 26, 36)  // bottom
        );
        g2.setPaint(gpnt);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        if (logo != null) {
            int logoWidth = logo.getWidth() * 3;   // scale ×3 (change if needed)
            int logoHeight = logo.getHeight() * 3;

            int logoX = (gp.screenWidth - logoWidth) / 2;
            int logoY = gp.screenHeight / 40 - 95;  // top area

            g2.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);

        } else {
            System.out.println("The game logo isn't found so can't be rendered correctly");
            g2.setColor(Color.WHITE);
            g2.setFont(UITheme.FONT_TITLE().deriveFont(25f));
            String title = "BUDGET HARVEST";
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (gp.screenWidth - titleWidth) / 2, gp.screenHeight / 3);
        }

        // Subtitle / hint
        g2.setFont(UITheme.FONT_SMALL().deriveFont(15f));
        String hint = "Press ENTER or click a button";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        g2.setColor(Color.white);
        g2.drawString(hint, (gp.screenWidth - hintWidth) / 2, gp.screenHeight / 2 + 135);

        // Menu buttons
        Rectangle[] buttons = getMainMenuButtonBounds();
        g2.setFont(UITheme.FONT_DEFAULT());
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < buttons.length; i++) {
            Rectangle r = buttons[i];
            boolean selected = (i == mainMenuSelectedIndex);

            Color bg = selected ? new Color(80, 80, 140) : new Color(40, 40, 80);
            g2.setColor(bg);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            g2.setColor(Color.WHITE);
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            String label = mainMenuOptions[i];
            int textWidth = fm.stringWidth(label);
            int textX = r.x + (r.width - textWidth) / 2;
            int textY = r.y + (r.height + fm.getAscent()) / 2 - 3;
            g2.drawString(label, textX, textY);
        }
    }

    public Rectangle[] getMainMenuButtonBounds() {
        int buttonCount = mainMenuOptions.length;
        int buttonWidth = gp.screenWidth / 3;
        int buttonHeight = gp.tileSize + 8;
        int startX = (gp.screenWidth - buttonWidth) / 2;
        int totalHeight = buttonCount * buttonHeight + (buttonCount - 1) * 10;
        int startY = gp.screenHeight / 2 - totalHeight / 2;

        Rectangle[] rects = new Rectangle[buttonCount];
        for (int i = 0; i < buttonCount; i++) {
            int y = startY + i * (buttonHeight + 10);
            rects[i] = new Rectangle(startX, y, buttonWidth, buttonHeight);
        }
        return rects;
    }

    public void updateMainMenu() {
        boolean up = gp.keyH.upPressed;
        boolean down = gp.keyH.downPressed;
        boolean enter = gp.keyH.enterPressed;

        int optionCount = mainMenuOptions.length;

        // Keyboard navigation
        if (up && !lastMenuUp) {
            mainMenuSelectedIndex = (mainMenuSelectedIndex - 1 + optionCount) % optionCount;
        }
        if (down && !lastMenuDown) {
            mainMenuSelectedIndex = (mainMenuSelectedIndex + 1) % optionCount;
        }

        // Mouse hover selection
        Rectangle[] buttons = getMainMenuButtonBounds();
        Point mouse = gp.getMousePosition();
        if (mouse != null) {
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].contains(mouse)) {
                    mainMenuSelectedIndex = i;
                }
            }
        }

        // Activate selection via Enter or mouse click
        if ((enter && !lastMenuEnter) || mainMenuMouseClicked) {
            activateMainMenuSelection();
            gp.keyH.enterPressed = false;
            mainMenuMouseClicked = false;
        }

        lastMenuUp = up;
        lastMenuDown = down;
        lastMenuEnter = enter;
    }

    public void updateCreditsMenu() {
        if (gp.keyH.enterPressed) {
            gp.transition.start(TransitionManager.Type.WIPE_HORIZONTAL, 0.05f, () -> {
                gp.keyH.enterPressed = false;
                gp.inCreditMenu = false;
                gp.inMainMenu = true;
            });
        }
    }

    public void drawCreditsMenu(Graphics2D g2) {
        // Background fade
        GradientPaint gpnt = new GradientPaint(
                0, 0, new Color(34, 131, 89),      // top
                0, gp.screenHeight, new Color(11, 105, 86)  // bottom
        );
        g2.setPaint(gpnt);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Glass panel
        g2.setColor(new Color(255, 255, 255, 30));
        g2.fillRoundRect(
                gp.screenWidth / 4,
                gp.screenHeight / 8,
                gp.screenWidth / 2,
                (int) (gp.screenHeight / 1.4f),
                20, 20
        );

        g2.setColor(Color.WHITE);
        g2.setFont(UITheme.FONT_TITLE());

        String title = "C R E D I T S";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title,
                (gp.screenWidth - tw) / 2,
                gp.screenHeight / 8 + 50
        );

        // Names
        String[] names = {
                "HK Studio",
                "",
                "Team Members:",
                " • Muh. Daffa Dwi Syahreza",
                " • Vincent Rivaldo",
                " • Louis Dominic",
                "",
                "Special Thanks:",
                " • Cup Nooble (Assets Pack)",
                " • And also toto (the toilet)"
        };

        g2.setFont(UITheme.FONT_DEFAULT());

        int y = gp.screenHeight / 8 + 120;
        for (String n : names) {
            int w = g2.getFontMetrics().stringWidth(n);
            g2.drawString(n, (gp.screenWidth - w) / 2, y);
            y += 28;
        }

        // Back hint
        String back = "Press ENTER to return";
        int bw = g2.getFontMetrics().stringWidth(back);
        g2.setColor(new Color(200, 200, 255));
        g2.drawString(back,
                (gp.screenWidth - bw) / 2,
                gp.screenHeight - 60
        );

    }

    public void activateMainMenuSelection() {
        switch (mainMenuSelectedIndex) {
            case 0 -> {
                gp.transition.start(
                        TransitionManager.Type.WIPE_HORIZONTAL,
                        0.03f,
                        () -> {
                            gp.inMainMenu = false; // Play
                        });
            }

            case 1 -> {
                gp.transition.start(
                        TransitionManager.Type.WIPE_HORIZONTAL,
                        0.03f,
                        () -> {
                            gp.inMainMenu = false;
                            gp.inCreditMenu = true;
                        }
                );

            }
            case 2 -> System.out.println("Random option selected (placeholder).");
        }
    }
}
