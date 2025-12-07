package UI;

import java.awt.*;
import java.util.Random;

public class TransitionManager {
    public enum Type {
        FADE_BLACK,
        FADE_WHITE,
        CIRCLE,
        WIPE_VERTICAL,
        WIPE_HORIZONTAL,
        PIXEL_DISSOLVE,
        SOFT_EDGE_WIPE
    }

    public enum Phase {
        NONE,
        TRANSITION_IN,
        TRANSITION_OUT
    }

    private Phase phase = Phase.NONE;
    private Type type = Type.FADE_BLACK;

    private float linearProgress = 0f;
    private float eased = 0f;
    private float speed = 0.02f;

    private Runnable callback;

    // dissolve data
    private boolean[][] dissolveMask;
    private final Random rng = new Random();

    // SOFT-EDGE WIPE feather size
    private final int feather = 80;

    //---------------------------------------------------
    // EASING
    //---------------------------------------------------
    private float easeInOutCubic(float t) {
        return (t < 0.5)
                ? 4 * t * t * t
                : 1 - (float) Math.pow(-2 * t + 2, 3) / 2f;
    }

    //---------------------------------------------------
    // START
    //---------------------------------------------------
    public void start(Type type, float speed, Runnable callback) {
        this.type = type;
        this.speed = speed;
        this.callback = callback;

        linearProgress = 0f;
        eased = 0f;
        phase = Phase.TRANSITION_IN;

        if (type == Type.PIXEL_DISSOLVE) {
            dissolveMask = new boolean[64][64];
            for (int x = 0; x < 64; x++)
                for (int y = 0; y < 64; y++)
                    dissolveMask[x][y] = rng.nextBoolean();
        }
    }

    //---------------------------------------------------
    // UPDATE  (FULLY PATCHED)
    //---------------------------------------------------
    public void update() {
        if (phase == Phase.NONE) return;

        linearProgress += speed;
        if (linearProgress > 1f) linearProgress = 1f;

        // ALL transitions use smooth "movie-like" easing
        eased = easeInOutCubic(linearProgress);

        if (linearProgress >= 1f) {
            if (phase == Phase.TRANSITION_IN) {
                if (callback != null) callback.run();

                // Start OUT phase
                phase = Phase.TRANSITION_OUT;
                linearProgress = 0f;
                eased = 0f;
            } else if (phase == Phase.TRANSITION_OUT) {
                // Fully done
                phase = Phase.NONE;
            }
        }
    }

    //---------------------------------------------------
    // DRAW
    //---------------------------------------------------
    public void draw(Graphics2D g2, int w, int h) {
        if (phase == Phase.NONE) return;

        boolean reverse = (phase == Phase.TRANSITION_OUT);
        float p = reverse ? (1f - eased) : eased;

        switch (type) {
            case FADE_BLACK -> drawFade(g2, w, h, Color.black, p);
            case FADE_WHITE -> drawFade(g2, w, h, Color.white, p);
            case CIRCLE -> drawCircle(g2, w, h, p);
            case WIPE_VERTICAL -> drawWipeVertical(g2, w, h, p);
            case WIPE_HORIZONTAL -> drawWipeHorizontal(g2, w, h, p);
            case PIXEL_DISSOLVE -> drawPixelDissolve(g2, w, h, p);
            case SOFT_EDGE_WIPE -> drawSoftEdgeWipe(g2, w, h, p);
        }
    }

    //---------------------------------------------------
    // RENDERERS
    //---------------------------------------------------
    private void drawFade(Graphics2D g2, int w, int h, Color color, float p) {
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, p));
        g2.setColor(color);
        g2.fillRect(0, 0, w, h);
        g2.setComposite(old);
    }

    private void drawCircle(Graphics2D g2, int w, int h, float p) {
        int maxR = (int) Math.hypot(w, h);
        int radius = (int) (p * maxR);

        g2.setColor(Color.black);
        g2.fillRect(0, 0, w, h);

        g2.setComposite(AlphaComposite.Clear);
        g2.fillOval((w - radius) / 2, (h - radius) / 2, radius, radius);
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private void drawWipeVertical(Graphics2D g2, int w, int h, float p) {
        int y = (int) (h * p);
        g2.setColor(Color.black);
        g2.fillRect(0, 0, w, y);
    }

    private void drawWipeHorizontal(Graphics2D g2, int w, int h, float p) {
        int x = (int) (w * p);
        g2.setColor(Color.black);
        g2.fillRect(0, 0, x, h);
    }

    private void drawPixelDissolve(Graphics2D g2, int w, int h, float p) {
        int cellW = w / 64;
        int cellH = h / 64;

        g2.setColor(Color.black);

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                if (dissolveMask[x][y] && rng.nextFloat() < p) {
                    g2.fillRect(x * cellW, y * cellH, cellW, cellH);
                }
            }
        }
    }

    //---------------------------------------------------
    // NEW — SOFT EDGE WIPE (Cinematic)
    //---------------------------------------------------
    private void drawSoftEdgeWipe(Graphics2D g2, int w, int h, float p) {
        int wipeWidth = (int) (w * p);

        // Solid region
        g2.setColor(Color.black);
        g2.fillRect(0, 0, wipeWidth, h);

        // Feathered edge
        int startFade = wipeWidth - feather;
        if (startFade < 0) startFade = 0;

        for (int i = 0; i < feather; i++) {
            float alpha = 1f - (i / (float) feather);
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fillRect(startFade + i, 0, 1, h);
            g2.setComposite(old);
        }
    }

    public boolean isTransitioning() {
        return phase != Phase.NONE;
    }
}