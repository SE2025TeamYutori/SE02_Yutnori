package org.cau02.controller;

import org.cau02.model.Board;
import org.cau02.model.GameManager;
import org.cau02.model.RegularBoard;

public class BoardController {
    private GameController gameController;
    
    public BoardController(GameController gameController) {
        this.gameController = gameController;
    }
    
    public Board getBoard() {
        GameManager gm = gameController.getGameManager();
        return gm != null ? gm.getBoard() : null;
    }
    
    public int getBoardAngle() {
        Board board = getBoard();
        if (board instanceof RegularBoard) {
            return ((RegularBoard) board).getBoardAngle();
        }
        return 4; // default
    }
    
}