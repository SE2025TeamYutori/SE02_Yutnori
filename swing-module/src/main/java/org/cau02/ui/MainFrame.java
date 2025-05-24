package org.cau02.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        // StartPanel(Swing 버전) 추가
        StartPanel panel = new StartPanel();
        setContentPane(panel);
        
        // 아이콘 설정
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/favicon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("아이콘 로드 실패: " + e.getMessage());
        }

        // 폰트 로드 예시 (참고만, 실제 적용은 컴포넌트마다 해야 함)
        try {
            Font fontLight = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/MaplestoryLight.ttf")).deriveFont(24f);
            Font fontBold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/MaplestoryBold.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(fontLight);
            ge.registerFont(fontBold);
        } catch (Exception e) {
            System.err.println("폰트 로드 실패: " + e.getMessage());
        }
    }
}
