package UI;

import java.awt.*;
import java.util.ArrayList;

public class UIContainer {
    private final ArrayList<UIComponent> components = new ArrayList<>();
    private final ArrayList<UIComponent> toAdd = new ArrayList<>();
    private final ArrayList<UIComponent> toRemove = new ArrayList<>();

    public void add(UIComponent c) {
        if (c != null) {
            toAdd.add(c);
        }
    }

    public void remove(UIComponent c) {
        if (c != null) {
            toRemove.add(c);
        }
    }

    public void clear() {
        components.clear();
        toAdd.clear();
        toRemove.clear();
    }

    private void applyPendingChanges() {
        if (!toAdd.isEmpty()) {
            for (UIComponent c : toAdd) {
                if (!components.contains(c)) {
                    components.add(c);
                }
            }
            toAdd.clear();
        }

        if (!toRemove.isEmpty()) {
            components.removeAll(toRemove);
            toRemove.clear();
        }
    }

    public void update() {
        applyPendingChanges();

        ArrayList<UIComponent> copy = new ArrayList<>(components);

        for (UIComponent c : copy) {
            if (c.visible) {
                c.update();
            }
        }
    }

    public void draw(Graphics2D g2) {
        applyPendingChanges();

        ArrayList<UIComponent> copy = new ArrayList<>(components);

        for (UIComponent c : copy) {
            if (c.visible) {
                c.draw(g2);
            }
        }
    }

    public ArrayList<UIComponent> getComponents() {
        return new ArrayList<>(components);
    }

}
