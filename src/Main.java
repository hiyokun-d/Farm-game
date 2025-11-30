import Screen.GamePanel;
import fileHandler.Filehandler;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Filehandler.load();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); // we'll turn it into true later, (when I remembered)
        frame.setTitle("RPG game");

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.startGameThread();
//        int health = Filehandler.getInt("playerHealth");
//        String name = Filehandler.getString("playerName");
//
//        System.out.println("Player.Player: " + name + " HP=" + health);

    }
}
