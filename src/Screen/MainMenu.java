package Screen;

import java.awt.*;

public class MainMenu {
    GamePanel gp;

    public final String[] mainMenuOptions = {"Play", "Credits", "???"};
    public int mainMenuSelectedIndex = 0;
    public boolean mainMenuMouseClicked = false;
    public boolean lastMenuUp = false, lastMenuDown = false, lastMenuEnter = false;

    public MainMenu(GamePanel gp) {
        this.gp = gp;
    }

    public void drawMainMenu(Graphics2D g2) {
        // Background
        g2.setColor(new Color(10, 10, 30));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "LIL GUY FARM!";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - titleWidth) / 2, gp.screenHeight / 3);

        // Subtitle / hint
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String hint = "Press ENTER or click a button";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (gp.screenWidth - hintWidth) / 2, gp.screenHeight / 3 + 40);

        // Menu buttons
        Rectangle[] buttons = getMainMenuButtonBounds();
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
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
            gp.keyH.enterPressed = false;
            gp.inCreditMenu = false;
            gp.inMainMenu = true;
        }
    }

    public void drawCreditsMenu(Graphics2D g2) {
        g2.setColor(new Color(15, 15, 40));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        String title = "CREDITS";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - titleWidth) / 2, 80);

        // List of names (edit this freely)
        String[] names = {
                "HK Studio",
                "Team Members:",
                " - Muh. Daffa Dwi Syahreza (Hiyo)",
                " - Vincent Rivaldo",
                " - Louis Dominic",
                "",
                "Special thanks to:",
                " - Cup Nooble (we use their assets)",
        };

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        int y = 140;
        for (String name : names) {
            int w = g2.getFontMetrics().stringWidth(name);
            g2.drawString(name, (gp.screenWidth - w) / 2, y);
            y += 30;
        }

        // Back button
        String backText = "Press ENTER to go back";
        int bw = g2.getFontMetrics().stringWidth(backText);
        g2.drawString(backText, (gp.screenWidth - bw) / 2, gp.screenHeight - 60);
    }

    public void activateMainMenuSelection() {
        switch (mainMenuSelectedIndex) {
            case 0 -> gp.inMainMenu = false; // Play
            case 1 -> {
                gp.inMainMenu = false;
                gp.inCreditMenu = true;
            }
            case 2 -> System.out.println("Random option selected (placeholder).");
        }
    }
}
