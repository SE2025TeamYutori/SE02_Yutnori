package org.cau02.view;

import org.cau02.model.GameManager;
import org.cau02.model.Piece;
import org.cau02.model.Yut;
import org.cau02.model.YutNoriObserver;
import org.cau02.view.ui.MainGamePanel;

import javax.swing.*;
import java.awt.*;


public class SwingGameView implements YutNoriObserver {
    private JFrame frame;
    private MainGamePanel mainPanel;
    private GameManager gameManager;
    
    public SwingGameView(GameManager gameManager) {
        this.gameManager = gameManager;
        // Register as observer
        gameManager.registerObserver(this);
    }
    
    //Swing UI 초기화 및 표시
    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            createAndShowGUI();
        });
    }

    //메인 GUI 컴포넌트들을 생성하고 구성
    private void createAndShowGUI() {
        // 게임 매니저를 사용하여 메인 패널 생성
        mainPanel = new MainGamePanel(gameManager);
        
        // 메인 프레임 생성 및 구성
        frame = new JFrame("YutNori Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 700));
        
        // 메인 패널을 프레임에 추가
        frame.getContentPane().add(mainPanel);
        
        // 프레임 패키징 및 위치 설정
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    //메인 게임 패널 반환
    public MainGamePanel getMainPanel() {
        return mainPanel;
    }

    public JFrame getFrame() {
        return frame;
    }
    
    //프레임 해제 및 뷰 닫기
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }
    
    // YutNoriObserver 구현 메소드
    @Override
    public void onGameEnded() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                frame,
                "Game Over! Player " + gameManager.getWinner() + " wins!",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    @Override
    public void onTurnChanged() {
        SwingUtilities.invokeLater(() -> {
            if (mainPanel != null) {
                mainPanel.updateTurnInfo();
                mainPanel.updateControlPanel();
            }
        });
    }

    @Override
    public void onYutStateChanged() {
        SwingUtilities.invokeLater(() -> {
            if (mainPanel != null) {
                mainPanel.updateYutInfo();
                mainPanel.updateControlPanel();
            }
        });
    }

    @Override
    public void onPieceMoved() {
        SwingUtilities.invokeLater(() -> {
            if (mainPanel != null) {
                mainPanel.updateBoardPanel();
                mainPanel.updatePlayerInfo();
            }
        });
    }
    
    // 게임 상호작용을 위한 추가 메소드
    public void randomThrowYut() {
        try {
            Yut result = gameManager.throwRandomYut();
            addMessage("Threw a random yut: " + result.name());
        } catch (IllegalStateException ex) {
            addMessage("Error: " + ex.getMessage());
        }
    }
    
    public void selectedThrowYut(Yut selectedYut) {
        try {
            Yut result = gameManager.throwSelectedYut(selectedYut);
            addMessage("Threw a selected yut: " + result.name());
        } catch (IllegalStateException ex) {
            addMessage("Error: " + ex.getMessage());
        }
    }
    
    public void moveNewPiece(Yut yut) {
        try {
            if (gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer()) > 0) {
                gameManager.moveNewPiece(yut);
                addMessage("Moved new piece using " + yut.name());
            } else {
                addMessage("No ready pieces available");
            }
        } catch (IllegalStateException ex) {
            addMessage("Error: " + ex.getMessage());
        }
    }
    
    public void movePiece(Piece piece, Yut yut) {
        try {
            gameManager.movePiece(piece, yut);
            addMessage("Moved piece using " + yut.name());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            addMessage("Error: " + ex.getMessage());
        }
    }
    
    public void addMessage(String message) {
        if (mainPanel != null) {
            mainPanel.getControlPanel().addMessage(message);
        }
    }
    
    // 게임 상태 관리
    public void startGame(int boardShape, int playerCount, int pieceCount) {
        try {
            gameManager.setBoard(boardShape);
            gameManager.setPlayerCount(playerCount);
            gameManager.setPieceCount(pieceCount);
            gameManager.startGame();
            
            // UI 업데이트
            mainPanel.updateGameInfo();
            mainPanel.updateBoardPanel();
            mainPanel.updateControlPanel();
            addMessage("Game started! Board shape: " + boardShape + "-sided");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                frame,
                "Error starting game: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    public void resetGame() {
        gameManager.resetGame();
        if (mainPanel != null) {
            mainPanel.getBoardPanel().clearPossibleMoves();
            mainPanel.updateGameInfo();
            mainPanel.updateBoardPanel();
            mainPanel.updateControlPanel();
            addMessage("Game reset. Configure settings and start again.");
        }
    }
    
    // 게임 매니저 반환
    public GameManager getGameManager() {
        return gameManager;
    }
}