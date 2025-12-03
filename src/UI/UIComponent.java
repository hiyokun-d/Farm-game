package UI;

import java.awt.*;

public abstract class UIComponent {
    public int x, y, width, height;
    public boolean visible = true;
    public boolean active = true;

    public UIComponent() {
    }

    public UIComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update() {
    }

    public abstract void draw(Graphics2D g2);

    public boolean isMouseInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void onClick() {
    }

    public void onHover() {
    }

    public void onRelease() {
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void toggleVisibility() {
        this.visible = !this.visible;
    }
}
