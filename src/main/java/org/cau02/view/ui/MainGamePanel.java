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
    private final YutNoriSwingObserver observer;
    
    /**
     * 일반적인 형태 : boardPanel
     * 정사각형 형태 : squareBoardUI
     * 정사각형 외 나머지 판은 아직 미구현 (현재 원형 판 형태로 나타남)
     */
    private BoardPanel boardPanel;
    private SquareBoardUI squareBoardUI;


    private final ControlPanel controlPanel; //게임 컨트롤러 패널
    private JPanel currentBoardContainer; //현재 활성화된 보드 UI를 담는 컨테이너 패널


    //게임 정보 패널
    private final JPanel gameInfoPanel = new JPanel();
    private final JLabel playerCountLabel = new JLabel("Players: "); //플레이어 수 레이블
    private final JLabel pieceCountLabel = new JLabel("Pieces per player: "); //플레이어당 말의 수 레이블
    private final JLabel boardShapeLabel = new JLabel("Board shape: "); //보드의 형태 레이블
    
    //게임 설정 패널
    private final JPanel setupPanel = new JPanel();
    private final JComboBox<Integer> boardShapeCombo = new JComboBox<>(new Integer[]{4, 5, 6}); //보드 형태 콤보박스
    private final JComboBox<Integer> playerCountCombo = new JComboBox<>(new Integer[]{2, 3, 4}); //플레이어 수 콤보박스
    private final JComboBox<Integer> pieceCountCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5}); //플레이어당 말의 수 콤보박스

    
    private final JButton startGameButton = new JButton("Start Game"); //게임 시작 버튼
    private final JButton resetGameButton = new JButton("Reset Game"); //게임 초기화 버튼
    

    public MainGamePanel(GameManager gameManager) {
        this.gameManager = gameManager;
        
        // 보드 패널 생성
        boardPanel = new BoardPanel(gameManager);
        squareBoardUI = new SquareBoardUI(gameManager);
        controlPanel = new ControlPanel(gameManager);
        
        // 보드 UI를 담는 컨테이너 패널 생성
        currentBoardContainer = new JPanel(new BorderLayout());
        currentBoardContainer.add(boardPanel, BorderLayout.CENTER);
        
        // 옵저버 등록
        observer = new YutNoriSwingObserver(gameManager, this);
        gameManager.registerObserver(observer);
        
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
        add(currentBoardContainer, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(setupPanel, BorderLayout.SOUTH);
        
        // 정사각형 보드 패널에 말 선택 콜백 설정
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
        
        // 정사각형 보드 패널에 말 선택 콜백 설정
        squareBoardUI.setOnPieceSelected(piece -> {
            controlPanel.setSelectedPiece(piece);
            Map<Yut, BoardSpace> possibleMoves = new HashMap<>();
            List<BoardSpace> locations = gameManager.getPossibleLocations(piece);
            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i) != null) {
                    possibleMoves.put(Yut.values()[i], locations.get(i));
                }
            }
            squareBoardUI.showPossibleMoves(possibleMoves);
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
                    
                    // 선택된 보드 형태에 따라 보드 UI 전환
                    currentBoardContainer.removeAll();
                    if (boardShape == 4) {
                        // 4각형 보드 UI 사용
                        currentBoardContainer.add(squareBoardUI, BorderLayout.CENTER);
                    } else {
                        // 기타 보드 형태 UI 사용
                        currentBoardContainer.add(boardPanel, BorderLayout.CENTER);
                    }
                    currentBoardContainer.revalidate();
                    currentBoardContainer.repaint();
                    
                    updateGameInfo();
                    updateBoardPanel();
                    updateControlPanel();
                    controlPanel.addMessage("Game started!");
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
                squareBoardUI.clearPossibleMoves();
                
                // 기본 보드 뷰로 초기화
                currentBoardContainer.removeAll();
                currentBoardContainer.add(boardPanel, BorderLayout.CENTER);
                currentBoardContainer.revalidate();
                currentBoardContainer.repaint();
                
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
    
    //게임 정보 패널 업데이트
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
    
    //턴 정보 표시 업데이트
    public void updateTurnInfo() {
        if (gameManager.getState() == GameState.PLAYING) {
            controlPanel.addMessage("Turn changed to Player " + gameManager.getCurrentPlayer());
        }
        updateGameInfo();
    }
    

    //윷 정보 표시 업데이트
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

    //보드 패널 업데이트
    public void updateBoardPanel() {
        // Check which board UI is currently active and repaint it
        if (gameManager.getState() == GameState.PLAYING && gameManager.getBoard() instanceof RegularBoard) {
            RegularBoard board = (RegularBoard) gameManager.getBoard();
            if (board.getBoardAngle() == 4 && squareBoardUI != null) {
                squareBoardUI.repaint();
            } else if (boardPanel != null) {
                boardPanel.repaint();
            }
        } else if (boardPanel != null) {
            boardPanel.repaint();
        }
        
        // 또한 컨테이너를 다시 그리기 위해 호출
        if (currentBoardContainer != null) {
            currentBoardContainer.repaint();
        }
    }
    

    //플레이어 정보 표시 업데이트
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
    
    //컨트롤 패널 업데이트
    public void updateControlPanel() {
        controlPanel.updateUI();
    }
}