package org.cau02.ui;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class MainFrame extends JFrame {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    public MainFrame() {
        setTitle("윷놀이 게임!");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 아이콘 설정
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/favicon.png");
            if (iconStream != null) {
                ImageIcon icon = new ImageIcon(iconStream.readAllBytes());
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 폰트 로딩
        try {
            InputStream lightFontStream = getClass().getResourceAsStream("/fonts/MaplestoryLight.ttf");
            InputStream boldFontStream = getClass().getResourceAsStream("/fonts/MaplestoryBold.ttf");
            if (lightFontStream != null && boldFontStream != null) {
                Font lightFont = Font.createFont(Font.TRUETYPE_FONT, lightFontStream);
                Font boldFont = Font.createFont(Font.TRUETYPE_FONT, boldFontStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(lightFont);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(boldFont);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 시작 패널 표시
        showStartPanel();
    }

    private void showStartPanel() {
        StartPanel startPanel = new StartPanel();
        setContentPane(startPanel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 