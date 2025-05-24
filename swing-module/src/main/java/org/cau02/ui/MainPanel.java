package org.cau02.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.cau02.model.GameManager;
import org.cau02.model.RegularBoard;
import org.cau02.model.Yut;
import org.cau02.model.YutNoriObserver;
import org.cau02.ui.board.RegularBoardPanel;

public class MainPanel extends JPanel implements YutNoriObserver {
    private final GameManager gm;

    private JLabel yutCountLabel;
    private JButton throwRandomYutButton, throwSelectYutButton;
    private JPanel yutResultPane;
    private JLabel yutResultImageLabel, yutResultTextLabel;
    private final List<JPanel> yutResultPanels = new ArrayList<>();
    private final List<JLabel> yutResultLabels = new ArrayList<>();
    private final List<JLabel> yutCountLabels = new ArrayList<>();
    private final List<PlayerPanel> playerPanels = new ArrayList<>();
    private RegularBoardPanel boardPanel;

    public MainPanel(GameManager gm) {
        this.gm = gm;
        gm.registerObserver(this);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1280, 850));

        JPanel mainBox = new JPanel();
        mainBox.setLayout(new BorderLayout());

        // 좌측 플레이어 박스
        JPanel playerBoxLeft = new JPanel();
        playerBoxLeft.setLayout(new BoxLayout(playerBoxLeft, BoxLayout.Y_AXIS));
        playerBoxLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 우측 플레이어 박스
        JPanel playerBoxRight = new JPanel();
        playerBoxRight.setLayout(new BoxLayout(playerBoxRight, BoxLayout.Y_AXIS));
        playerBoxRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 중앙 박스
        JPanel middleBox = new JPanel();
        middleBox.setLayout(new BoxLayout(middleBox, BoxLayout.Y_AXIS));
        middleBox.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 게임판 영역
        boardPanel = new RegularBoardPanel(gm, ((RegularBoard)gm.getBoard()).getBoardAngle());
        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.add(boardPanel.getPanel());
        boardContainer.setPreferredSize(new Dimension(600, 600));
        middleBox.add(boardContainer);

        // 윷 결과 패널
        JPanel yutBox = new JPanel();
        yutBox.setLayout(new FlowLayout());
        initializeYutResults();
        for (JPanel yutResult : yutResultPanels) {
            yutBox.add(yutResult);
        }

        // 버튼 영역
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        yutCountLabel = new JLabel("남은 횟수: 1", SwingConstants.CENTER);
        yutCountLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        yutCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        throwRandomYutButton = new JButton("랜덤 윷 던지기");
        throwRandomYutButton.setPreferredSize(new Dimension(130, 28));
        throwRandomYutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        throwSelectYutButton = new JButton("선택 윷 던지기");
        throwSelectYutButton.setPreferredSize(new Dimension(130, 28));
        throwSelectYutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        throwRandomYutButton.addActionListener(e -> {
            Yut yut = gm.throwRandomYut();
            showYutImage(yut);
        });

        throwSelectYutButton.addActionListener(e -> {
            openYutSelectPanel();
        });

        buttonPanel.add(yutCountLabel);
        buttonPanel.add(Box.createVerticalStrut(8));
        buttonPanel.add(throwRandomYutButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(throwSelectYutButton);

        middleBox.add(Box.createVerticalStrut(10));
        middleBox.add(yutBox);
        middleBox.add(Box.createVerticalStrut(8));
        middleBox.add(buttonPanel);

        // 플레이어 추가
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            PlayerPanel panel = new PlayerPanel(gm, i, boardPanel);
            panel.updateReadyPieces(); // 초기 대기 말 표시
            playerPanels.add(panel);
            if (i % 2 == 0) {
                if (i > 0) playerBoxLeft.add(Box.createVerticalStrut(10));
                playerBoxLeft.add(panel);
            } else {
                if (i > 1) playerBoxRight.add(Box.createVerticalStrut(10));
                playerBoxRight.add(panel);
            }
        }

        mainBox.add(playerBoxLeft, BorderLayout.WEST);
        mainBox.add(middleBox, BorderLayout.CENTER);
        mainBox.add(playerBoxRight, BorderLayout.EAST);

        // 레이어드 패널 생성 (mainBox와 yutResultPane 오버레이용)
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1280, 720));
        
        // mainBox를 레이어드 패널에 추가
        mainBox.setBounds(0, 0, 1280, 720);
        layeredPane.add(mainBox, JLayeredPane.DEFAULT_LAYER);
        
        // 윷 결과 출력용 패널
        yutResultPane = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        yutResultPane.setOpaque(false);
        
        // 중앙 컨테이너
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);
        
        yutResultImageLabel = new JLabel();
        yutResultImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        yutResultImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        yutResultTextLabel = new JLabel("", SwingConstants.CENTER);
        yutResultTextLabel.setFont(new Font("맑은 고딕", Font.BOLD, 48));
        yutResultTextLabel.setForeground(Color.WHITE);
        yutResultTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerContainer.add(Box.createVerticalGlue());
        centerContainer.add(yutResultImageLabel);
        centerContainer.add(Box.createVerticalStrut(10));
        centerContainer.add(yutResultTextLabel);
        centerContainer.add(Box.createVerticalGlue());
        
        yutResultPane.add(centerContainer, BorderLayout.CENTER);
        yutResultPane.setBounds(0, 0, 1280, 720);
        yutResultPane.setVisible(false);
        
        layeredPane.add(yutResultPane, JLayeredPane.MODAL_LAYER);
        
        add(layeredPane, BorderLayout.CENTER);
    }

    private void initializeYutResults() {
        for (int i = 0; i < 6; i++) {
            JPanel yutResult = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(3, 3, 44, 44);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(3, 3, 44, 44);
                }
            };
            yutResult.setPreferredSize(new Dimension(50, 50));
            yutResult.setLayout(null);
            yutResult.setOpaque(false);

            JLabel nameLabel = new JLabel("", SwingConstants.CENTER);
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 10));
            nameLabel.setBounds(3, 18, 44, 16);
            yutResult.add(nameLabel);

            JLabel countLabel = new JLabel("", SwingConstants.CENTER);
            countLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            countLabel.setForeground(Color.RED);
            countLabel.setBounds(3, 0, 44, 16); // 윷 원 위쪽에 배치
            yutResult.add(countLabel);

            yutResultPanels.add(yutResult);
            yutResultLabels.add(nameLabel);
            yutCountLabels.add(countLabel);
        }
    }

    private void openYutSelectPanel() {
        YutSelectPanel yutSelectPanel = new YutSelectPanel(gm, this);
        
        // 모달 다이얼로그로 표시
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "윷 선택", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(yutSelectPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void showYutImage(Yut yut) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/yut_images/" + yut.name() + ".png"));
        yutResultImageLabel.setIcon(icon);
        yutResultTextLabel.setText(yut.getKoreanName() + "!");
        yutResultPane.setVisible(true);

        Timer timer = new Timer(1000, e -> {
            yutResultPane.setVisible(false);
            yutResultImageLabel.setIcon(null);
            yutResultTextLabel.setText("");
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void onGameEnded() {
        // 승리 메시지 표시
        int result = JOptionPane.showConfirmDialog(
            this, 
            "플레이어 " + (gm.getWinner() + 1) + " 승리!\n\n메인화면으로 돌아가시겠습니까?", 
            "게임 종료", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // 확인 버튼을 누르면 메인화면으로 이동
        if (result == JOptionPane.OK_OPTION) {
            returnToMainScreen();
        }
    }
    
    private void returnToMainScreen() {
        // 현재 프레임 가져오기
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        // 새로운 메인화면 프레임 생성
        JFrame mainFrame = new JFrame("윷놀이");
        mainFrame.setContentPane(new StartPanel());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1280, 750);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        
        // 현재 프레임 종료
        currentFrame.dispose();
    }

    @Override
    public void onTurnChanged() {
        for (int i = 0; i < playerPanels.size(); i++) {
            boolean isCurrentPlayer = gm.getCurrentPlayer() == i;
            playerPanels.get(i).updateTurn(isCurrentPlayer);
            
            // 새 말 출발 버튼 표시/숨김
            if (isCurrentPlayer && gm.getReadyPiecesCount(i) > 0 && gm.getCurrentMoveCount() > 0) {
                playerPanels.get(i).enableMoveNewPieceButton();
            } else {
                playerPanels.get(i).disableMoveNewPieceButton();
            }
        }
    }

    @Override
    public void onYutStateChanged() {
        yutCountLabel.setText("남은 횟수: " + gm.getCurrentYutCount());
        throwRandomYutButton.setEnabled(gm.getCurrentYutCount() > 0);
        throwSelectYutButton.setEnabled(gm.getCurrentYutCount() > 0);
        
        // 윷 족보 업데이트
        int index = 0;
        java.util.List<Integer> yutResult = gm.getYutResult();
        for (int i = 0; i < Yut.values().length; i++) {
            if (yutResult.get(i) > 0) {
                yutResultLabels.get(index).setText(Yut.values()[i].getKoreanName());
                if (yutResult.get(i) > 1) {
                    yutCountLabels.get(index).setText("x" + yutResult.get(i));
                } else {
                    yutCountLabels.get(index).setText("");
                }
                index++;
            }
        }
        
        // 나머지 칸 비우기
        for (int i = index; i < 6; i++) {
            yutResultLabels.get(i).setText("");
            yutCountLabels.get(i).setText("");
        }
        
        // 새 말 출발 버튼 업데이트
        int currentPlayer = gm.getCurrentPlayer();
        for (int i = 0; i < playerPanels.size(); i++) {
            if (i == currentPlayer && gm.getReadyPiecesCount(i) > 0 && gm.getCurrentMoveCount() > 0) {
                playerPanels.get(i).enableMoveNewPieceButton();
            } else {
                playerPanels.get(i).disableMoveNewPieceButton();
            }
        }
    }

    @Override
    public void onPieceMoved() {
        for (PlayerPanel panel : playerPanels) {
            panel.updateReadyPieces();
        }
        // 보드 업데이트
        boardPanel.getBoardPanel().updateBoard();
    }
}
