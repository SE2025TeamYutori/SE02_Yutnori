package org.cau02.ui;

import javax.swing.*;
import java.awt.*;

public class WinPanel extends JPanel {
    public WinPanel(int winnerId, Runnable onRestart, Runnable onExit) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(640, 360));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel winnerTitleLabel = new JLabel("플레이어 " + (winnerId + 1) + " 승리!", SwingConstants.CENTER);
        winnerTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 50));
        add(winnerTitleLabel, BorderLayout.NORTH);

        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));

        JButton restartButton = new JButton("한 판 더!");
        restartButton.setFont(new Font("맑은 고딕", Font.PLAIN, 30));
        restartButton.setPreferredSize(new Dimension(200, 60));
        restartButton.addActionListener(e -> onRestart.run());

        JButton exitButton = new JButton("종료...");
        exitButton.setFont(new Font("맑은 고딕", Font.PLAIN, 30));
        exitButton.setPreferredSize(new Dimension(200, 60));
        exitButton.addActionListener(e -> onExit.run());

        buttonBox.add(restartButton);
        buttonBox.add(exitButton);

        add(buttonBox, BorderLayout.CENTER);
    }
}
