package org.cau02.controller;

import org.cau02.model.GameManager;

public class PlayerController {
    private GameController gameController;
    
    public PlayerController(GameController gameController) {
        this.gameController = gameController;
    }
    
    public int getReadyPiecesCount(int playerId) {
        GameManager gm = gameController.getGameManager();
        return gm != null ? gm.getReadyPiecesCount(playerId) : 0;
    }
    
    public int getPieceCount() {
        GameManager gm = gameController.getGameManager();
        return gm != null ? gm.getPieceCount() : 0;
    }
    
    public boolean isCurrentPlayer(int playerId) {
        return gameController.getCurrentPlayer() == playerId;
    }
    
    public boolean canMoveNewPiece(int playerId) {
        return isCurrentPlayer(playerId) && 
                getReadyPiecesCount(playerId) > 0 && 
                gameController.getCurrentMoveCount() > 0;
    }
}