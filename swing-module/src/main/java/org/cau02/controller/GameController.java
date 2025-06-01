package org.cau02.controller;

import org.cau02.model.GameManager;
import org.cau02.ui.MainPanel;

public class GameController {
    private GameManager gameManager;
    private MainPanel mainPanel;
    
    public GameController() {
    }
    
    public void initializeGame(int boardAngle, int playerCount, int pieceCount) {
        this.gameManager = new GameManager(boardAngle, playerCount, pieceCount);
    }
    
    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        if (gameManager != null) {
            gameManager.registerObserver(mainPanel);
        }
    }
    
    public void startGame() {
        if (gameManager != null) {
            gameManager.startGame();
        }
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    public int getPlayerCount() {
        return gameManager != null ? gameManager.getPlayerCount() : 0;
    }
    
    public int getCurrentPlayer() {
        return gameManager != null ? gameManager.getCurrentPlayer() : 0;
    }
    
    public int getWinner() {
        return gameManager != null ? gameManager.getWinner() : -1;
    }
    
    public int getCurrentYutCount() {
        return gameManager != null ? gameManager.getCurrentYutCount() : 0;
    }
    
    public int getCurrentMoveCount() {
        return gameManager != null ? gameManager.getCurrentMoveCount() : 0;
    }
    
    public java.util.List<Integer> getYutResult() {
        return gameManager != null ? gameManager.getYutResult() : new java.util.ArrayList<>();
    }
}