package org.cau02;

import javax.swing.*;
import org.cau02.ui.StartPanel;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("윷놀이 게임!");
            frame.setContentPane(new StartPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 750); 
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}
