package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class MainGamePanel extends JPanel {

    private final GameManager gameManager;
    
    // 통합 보드 패널
    private BoardPanel boardPanel;
    private final ControlPanel controlPanel; // 게임 컨트롤러 패널
    private JPanel boardContainer; // 보드 UI를 담는 컨테이너 패널

    // 게임 정보 패널
    private final JPanel gameInfoPanel = new JPanel();
    private final JLabel playerCountLabel = new JLabel("Players: "); // 플레이어 수 레이블
    private final JLabel pieceCountLabel = new JLabel("Pieces per player: "); // 플레이어당 말의 수 레이블
    private final JLabel boardShapeLabel = new JLabel("Board shape: "); // 보드의 형태 레이블
    
    // 게임 설정 패널
    private final JPanel setupPanel = new JPanel();
    private final JComboBox<Integer> boardShapeCombo = new JComboBox<>(new Integer[]{4, 5, 6}); // 보드 형태 콤보박스
    private final JComboBox<Integer> playerCountCombo = new JComboBox<>(new Integer[]{2, 3, 4}); // 플레이어 수 콤보박스
    private final JComboBox<Integer> pieceCountCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5}); // 플레이어당 말의 수 콤보박스
    
    private final JButton startGameButton = new JButton("Start Game"); // 게임 시작 버튼
    private final JButton resetGameButton = new JButton("Reset Game"); // 게임 초기화 버튼
    
    public MainGamePanel(GameManager gameManager) {
        this.gameManager = gameManager;
        
        // 보드 패널 생성
        boardPanel = new BoardPanel(gameManager);
        controlPanel = new ControlPanel(gameManager);
        
        // 보드 UI를 담는 컨테이너 패널 생성
        boardContainer = new JPanel(new BorderLayout());
        boardContainer.add(boardPanel, BorderLayout.CENTER);
        
        // 메인 레이아웃 설정
        setLayout(new BorderLayout());
        
        // 게임 정보 패널 레이아웃 설정
        gameInfoPanel.setLayout(new GridLayout(1, 3));
        gameInfoPanel.add(playerCountLabel);
        gameInfoPanel.add(pieceCountLabel);
        gameInfoPanel.add(boardShapeLabel);
        
        // 설정 패널 레이아웃 설정
        setupPanel.setLayout(new FlowLayout());
        
        setupPanel.add(new JLabel("Board shape:"));
        setupPanel.add(boardShapeCombo);
        
        setupPanel.add(new JLabel("Players:"));
        setupPanel.add(playerCountCombo);
        
        setupPanel.add(new JLabel("Pieces:"));
        setupPanel.add(pieceCountCombo);
        
        setupPanel.add(startGameButton);
        setupPanel.add(resetGameButton);
        
        // 메인 패널에 컴포넌트 추가
        add(gameInfoPanel, BorderLayout.NORTH);
        add(boardContainer, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(setupPanel, BorderLayout.SOUTH);
        
        // 보드 패널에 말 선택 콜백 설정
        boardPanel.setOnPieceSelected(piece -> {
            controlPanel.setSelectedPiece(piece);
            Map<Yut, BoardSpace> possibleMoves = new HashMap<>();
            List<BoardSpace> locations = gameManager.getPossibleLocations(piece);
            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i) != null) {
                    possibleMoves.put(Yut.values()[i], locations.get(i));
                }
            }
            boardPanel.showPossibleMoves(possibleMoves);
        });
        
        // 버튼 클릭 이벤트 처리
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int boardShape = (Integer) boardShapeCombo.getSelectedItem();
                    int playerCount = (Integer) playerCountCombo.getSelectedItem();
                    int pieceCount = (Integer) pieceCountCombo.getSelectedItem();
                    
                    gameManager.setBoard(boardShape);
                    gameManager.setPlayerCount(playerCount);
                    gameManager.setPieceCount(pieceCount);
                    gameManager.startGame();
                    
                    // 보드 패널 업데이트 - 자동으로 N각형 보드를 그려줌
                    boardContainer.removeAll();
                    boardContainer.add(boardPanel, BorderLayout.CENTER);
                    boardContainer.revalidate();
                    boardContainer.repaint();
                    
                    updateGameInfo();
                    updateBoardPanel();
                    updateControlPanel();
                    controlPanel.addMessage("Game started! Board shape: " + boardShape + "-sided");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        MainGamePanel.this,
                        "Error starting game: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        
        resetGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameManager.resetGame();
                boardPanel.clearPossibleMoves();
                
                // 보드 뷰 초기화
                boardContainer.removeAll();
                boardContainer.add(boardPanel, BorderLayout.CENTER);
                boardContainer.revalidate();
                boardContainer.repaint();
                
                updateGameInfo();
                updateBoardPanel();
                updateControlPanel();
                controlPanel.addMessage("Game reset. Configure settings and start again.");
            }
        });
        
        // 초기 업데이트
        updateGameInfo();
        updateBoardPanel();
        updateControlPanel();
    }
    
    // 게임 정보 패널 업데이트
    public void updateGameInfo() {
        if (gameManager.getState() == GameState.PLAYING) {
            playerCountLabel.setText("Players: " + gameManager.getPlayerCount());
            pieceCountLabel.setText("Pieces per player: " + gameManager.getPieceCount());
            
            if (gameManager.getBoard() instanceof RegularBoard) {
                RegularBoard board = (RegularBoard) gameManager.getBoard();
                boardShapeLabel.setText("Board shape: " + board.getBoardAngle() + "-sided");
            } else {
                boardShapeLabel.setText("Board shape: Custom");
            }
            
            // 게임 진행 중 설정 컨트롤 비활성화
            boardShapeCombo.setEnabled(false);
            playerCountCombo.setEnabled(false);
            pieceCountCombo.setEnabled(false);
            startGameButton.setEnabled(false);
            resetGameButton.setEnabled(true);
        } else {
            playerCountLabel.setText("Players: Not started");
            pieceCountLabel.setText("Pieces per player: Not started");
            boardShapeLabel.setText("Board shape: Not started");
            
            // 게임 시작 전 설정 컨트롤 활성화
            boardShapeCombo.setEnabled(true);
            playerCountCombo.setEnabled(true);
            pieceCountCombo.setEnabled(true);
            startGameButton.setEnabled(true);
            resetGameButton.setEnabled(false);
        }
    }
    
    // 턴 정보 표시 업데이트
    public void updateTurnInfo() {
        if (gameManager.getState() == GameState.PLAYING) {
            controlPanel.addMessage("Turn changed to Player " + gameManager.getCurrentPlayer());
        }
        updateGameInfo();
    }
    
    // 윷 정보 표시 업데이트
    public void updateYutInfo() {
        if (gameManager.getState() == GameState.PLAYING) {
            StringBuilder sb = new StringBuilder("Yut state changed: ");
            sb.append("Throws: ").append(gameManager.getCurrentYutCount());
            sb.append(", Moves: ").append(gameManager.getCurrentMoveCount());
            sb.append(" [");
            
            List<Integer> yutResults = gameManager.getYutResult();
            for (int i = 0; i < yutResults.size(); i++) {
                if (yutResults.get(i) > 0) {
                    sb.append(Yut.values()[i].name()).append(":").append(yutResults.get(i)).append(" ");
                }
            }
            sb.append("]");
            
            controlPanel.addMessage(sb.toString());
        }
    }

    // 보드 패널 업데이트
    public void updateBoardPanel() {
        if (boardPanel != null) {
            boardPanel.repaint();
        }
        
        // 컨테이너도 다시 그리기
        if (boardContainer != null) {
            boardContainer.repaint();
        }
    }
    
    // 플레이어 정보 표시 업데이트
    public void updatePlayerInfo() {
        if (gameManager.getState() == GameState.PLAYING) {
            StringBuilder sb = new StringBuilder("Pieces updated: ");
            
            for (int i = 0; i < gameManager.getPlayerCount(); i++) {
                sb.append("P").append(i).append("(");
                sb.append("Ready:").append(gameManager.getReadyPiecesCount(i));
                sb.append(" Active:").append(gameManager.getActivePieces(i).size());
                sb.append(" Goal:").append(gameManager.getGoalPiecesCount(i));
                sb.append(") ");
            }
            
            controlPanel.addMessage(sb.toString());
        }
    }
    
    // 컨트롤 패널 업데이트
    public void updateControlPanel() {
        controlPanel.updateUI();
    }
    
    // 패널 getter 메소드
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
    
    public ControlPanel getControlPanel() {
        return controlPanel;
    }
}