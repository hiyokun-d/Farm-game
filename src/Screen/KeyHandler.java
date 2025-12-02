package Screen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed, escPressed, interactPressed;

    public boolean key1Pressed, key2Pressed, key3Pressed, key4Pressed, key5Pressed, key6Pressed, key7Pressed, key8Pressed, key9Pressed, key0Pressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_ESCAPE:
                escPressed = true;
                break;
            case KeyEvent.VK_E:
                interactPressed = true;
                break;
            case KeyEvent.VK_1:
                key1Pressed = true;
                break;
            case KeyEvent.VK_2:
                key2Pressed = true;
                break;
            case KeyEvent.VK_3:
                key3Pressed = true;
                break;
            case KeyEvent.VK_4:
                key4Pressed = true;
                break;
            case KeyEvent.VK_5:
                key5Pressed = true;
                break;
            case KeyEvent.VK_6:
                key6Pressed = true;
                break;
            case KeyEvent.VK_7:
                key7Pressed = true;
                break;
            case KeyEvent.VK_8:
                key8Pressed = true;
                break;
            case KeyEvent.VK_9:
                key9Pressed = true;
                break;
            case KeyEvent.VK_0:
                key0Pressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_ESCAPE:
                escPressed = false;
                break;
            case KeyEvent.VK_E:
                interactPressed = false;
                break;
            case KeyEvent.VK_1:
                key1Pressed = false;
                break;
            case KeyEvent.VK_2:
                key2Pressed = false;
                break;
            case KeyEvent.VK_3:
                key3Pressed = false;
                break;
            case KeyEvent.VK_4:
                key4Pressed = false;
                break;
            case KeyEvent.VK_5:
                key5Pressed = false;
                break;
            case KeyEvent.VK_6:
                key6Pressed = false;
                break;
            case KeyEvent.VK_7:
                key7Pressed = false;
                break;
            case KeyEvent.VK_8:
                key8Pressed = false;
                break;
            case KeyEvent.VK_9:
                key9Pressed = false;
                break;
            case KeyEvent.VK_0:
                key0Pressed = false;
                break;
        }
    }
}
