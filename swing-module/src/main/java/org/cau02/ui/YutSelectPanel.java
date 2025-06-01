package org.cau02.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.cau02.controller.YutController;
import org.cau02.model.Yut;

public class YutSelectPanel extends JPanel {
    private final YutController yutController;
    private final MainPanel mainPanel; // Swing MainPanel

    public YutSelectPanel(YutController yutController, MainPanel mainPanel) {
        this.yutController = yutController;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(640, 360));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel title = new JLabel("선택 윷 던지기", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 40, 20));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        for (Yut yut : Yut.values()) {
            JPanel yutCell = createYutChoice(yut);
            grid.add(yutCell);
        }

        add(grid, BorderLayout.CENTER);
    }

    private JPanel createYutChoice(Yut yut) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 100));

        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, 100, 100);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(0, 0, 100, 100);
                
                // 텍스트 중앙 그리기
                g2.setFont(new Font("맑은 고딕", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String text = yut.getKoreanName();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int x = (100 - textWidth) / 2;
                int y = (100 + textHeight) / 2;
                g2.setColor(Color.BLACK);
                g2.drawString(text, x, y);
            }
        };
        circlePanel.setPreferredSize(new Dimension(100, 100));
        circlePanel.setOpaque(false);
        circlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        circlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                yutController.throwSelectedYut(yut);
                
                // 다이얼로그 닫기
                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(YutSelectPanel.this);
                if (dialog != null) {
                    dialog.dispose();
                }
                
                mainPanel.showYutImage(yut);
            }
        });

        panel.add(circlePanel, BorderLayout.CENTER);
        return panel;
    }
} 
