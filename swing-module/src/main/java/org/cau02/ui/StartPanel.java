package org.cau02.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.cau02.controller.GameController;


public class StartPanel extends JPanel {

    private final JComboBox<String> gameBoardComboBox;
    private final JSpinner playerCountSpinner;
    private final JSpinner pieceCountSpinner;
    private final JButton startButton;

    public StartPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("윷 놀 이 게 임", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 64));
        title.setBorder(new EmptyBorder(130, 0, 0, 0));  // top padding
        add(title, BorderLayout.NORTH);

        // Bottom Box
        JPanel bottomBox = new JPanel();
        bottomBox.setLayout(new BoxLayout(bottomBox, BoxLayout.X_AXIS));
        bottomBox.setBorder(new EmptyBorder(50, 0, 0, 0));
        bottomBox.setOpaque(false);

        // Setting Box
        JPanel settingBox = new JPanel();
        settingBox.setLayout(new BoxLayout(settingBox, BoxLayout.Y_AXIS));
        settingBox.setOpaque(false);

        // [게임판 설정] - 기본값: 사각형
        gameBoardComboBox = new JComboBox<>(new String[]{"사각형", "오각형", "육각형"});
        gameBoardComboBox.setSelectedIndex(0); // 사각형 선택
        JPanel gameBoardLine = createSettingLine("게임판: ", gameBoardComboBox);

        // [플레이어 수 설정] - 기본값: 4명
        SpinnerNumberModel playerModel = new SpinnerNumberModel(4, 2, 4, 1);
        playerCountSpinner = new JSpinner(playerModel);
        JPanel playerCountLine = createSettingLine("플레이어 수: ", playerCountSpinner);

        // [인당 말 개수 설정] - 기본값: 2개
        SpinnerNumberModel pieceModel = new SpinnerNumberModel(2, 2, 5, 1);
        pieceCountSpinner = new JSpinner(pieceModel);
        JPanel pieceCountLine = createSettingLine("인당 말 개수: ", pieceCountSpinner);

        settingBox.add(gameBoardLine);
        settingBox.add(Box.createVerticalStrut(20));
        settingBox.add(playerCountLine);
        settingBox.add(Box.createVerticalStrut(20));
        settingBox.add(pieceCountLine);

        // Start Button
        startButton = new JButton("게임 시작!");
        startButton.setPreferredSize(new Dimension(300, 80));
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 35));
        startButton.setFocusPainted(false);
        startButton.setBackground(Color.WHITE);
        startButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        startButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 이벤트 핸들링은 컨트롤러가 필요할 경우 추가 가능

        bottomBox.add(Box.createHorizontalStrut(100));
        bottomBox.add(settingBox);
        bottomBox.add(Box.createHorizontalStrut(100));
        bottomBox.add(startButton);
        bottomBox.add(Box.createHorizontalStrut(100));

        add(bottomBox, BorderLayout.CENTER);

        startButton.addActionListener(e -> {
            int boardAngle = switch (getSelectedBoardType()) {
                case "사각형" -> 4;
                case "오각형" -> 5;
                case "육각형" -> 6;
                default -> -1;
            };
            int playerCount = getPlayerCount();
            int pieceCount = getPieceCount();

            GameController gameController = new GameController();
            gameController.initializeGame(boardAngle, playerCount, pieceCount);
            
            JPanel mainPanel = new MainPanel(gameController);

            // 부모 프레임 찾아서 contentPane 교체
            SwingUtilities.getWindowAncestor(this).setVisible(false);
            JFrame frame = new JFrame("윷놀이 - 게임 화면");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 750);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            gameController.startGame();
        });
    }

    private JPanel createSettingLine(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 40));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 30));
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 20));

        inputComponent.setPreferredSize(new Dimension(180, 30));
        panel.add(label);
        panel.add(inputComponent);

        return panel;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public String getSelectedBoardType() {
        return (String) gameBoardComboBox.getSelectedItem();
    }

    public int getPlayerCount() {
        return (Integer) playerCountSpinner.getValue();
    }

    public int getPieceCount() {
        return (Integer) pieceCountSpinner.getValue();
    }
}
