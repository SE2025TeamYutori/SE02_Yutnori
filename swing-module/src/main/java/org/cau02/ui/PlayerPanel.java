package org.cau02.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.cau02.model.GameManager;
import org.cau02.ui.board.RegularBoardPanel;

public class PlayerPanel extends JPanel {
    private final GameManager gm;
    private final int playerId;


    private final JLabel playerLabel;
    private final JPanel readyPieceBox;
    private final JButton moveNewPieceButton;
    private final List<JPanel> readyPieces = new ArrayList<>();

    public PlayerPanel(GameManager gm, int playerId, RegularBoardPanel boardPanel) {
        this.gm = gm;
        this.playerId = playerId;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(240, 135));
        setBackground(Color.WHITE);

        // 상단 - 플레이어 정보
        JPanel playerBox = new JPanel();
        playerBox.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(getColorForPlayer(playerId));

        playerLabel = new JLabel("플레이어 " + (playerId + 1));
        playerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        playerBox.add(colorBox);
        playerBox.add(playerLabel);
        add(playerBox, BorderLayout.NORTH);

        // 하단 - 대기 말 + 버튼
        JPanel readyBox = new JPanel();
        readyBox.setLayout(new BoxLayout(readyBox, BoxLayout.Y_AXIS));
        readyBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel readyLabel = new JLabel("대기 말: ");
        readyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        readyBox.add(readyLabel);

        JPanel readyPieceBoxBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 7));

        // 절대 위치로 말들을 겹치게 배치
        readyPieceBox = new JPanel();
        readyPieceBox.setLayout(null); // 절대 위치 사용
        readyPieceBox.setPreferredSize(new Dimension(130, 30));  
        readyPieceBox.setOpaque(false);

        moveNewPieceButton = new JButton("새 말 출발");
        moveNewPieceButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        moveNewPieceButton.setPreferredSize(new Dimension(80, 25));
        moveNewPieceButton.setVisible(false);
        moveNewPieceButton.addActionListener(e -> {
            boardPanel.getBoardPanel().showPossibleLocationsForNewPiece();
        });

        readyPieceBoxBox.add(readyPieceBox);
        readyPieceBoxBox.add(moveNewPieceButton);

        readyBox.add(readyPieceBoxBox);
        add(readyBox, BorderLayout.CENTER);

        initializePieces();
    }

    private void initializePieces() {
        readyPieces.clear();
        for (int i = 0; i < gm.getPieceCount(); i++) {
            JPanel piece = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getColorForPlayer(playerId));
                    g2.fillOval(4, 2, 20, 20);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(4, 2, 20, 20);
                }
            };
            piece.setSize(new Dimension(28, 24));  // 말의 크기 설정
            piece.setOpaque(false);
            readyPieces.add(piece);
        }
    }

    public void updateReadyPieces() {
        readyPieceBox.removeAll();
        
        int count;
        try {
            count = gm.getReadyPiecesCount(playerId);
        } catch (IllegalStateException e) {
            count = gm.getPieceCount(); // 게임 시작 전이면 전체 말 개수
        }
        
        // 말들을 절반씩 겹치면서 배치
        int pieceWidth = 28;
        int overlapOffset = pieceWidth / 2; // 절반씩 겹치게
        int startX = 5; // 시작 X 위치
        int y = 3; // Y 위치
        
        for (int i = 0; i < count; i++) {
            JPanel piece = readyPieces.get(i);
            int x = startX + (i * overlapOffset);
            piece.setBounds(x, y, pieceWidth, 24);
            readyPieceBox.add(piece);
        }
        
        readyPieceBox.revalidate();
        readyPieceBox.repaint();
    }

    public void enableMoveNewPieceButton() {
        moveNewPieceButton.setVisible(true);
    }

    public void disableMoveNewPieceButton() {
        moveNewPieceButton.setVisible(false);
    }

    public void updateTurn(boolean isNowTurn) {
        setBackground(isNowTurn ? new Color(255, 255, 200) : Color.WHITE);
        setBorder(BorderFactory.createLineBorder(isNowTurn ? new Color(100, 149, 237) : Color.BLACK, isNowTurn ? 3 : 1));
    }

    private Color getColorForPlayer(int id) {
        return switch (id) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.MAGENTA;
            default -> Color.GRAY;
        };
    }
}
