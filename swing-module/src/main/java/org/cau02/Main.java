package org.cau02;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setTitle("윷놀이 게임!");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);  // 임시 크기, 필요시 조절
            frame.setLocationRelativeTo(null); // 화면 중앙 정렬
            frame.setVisible(true);
        });
    }
}
