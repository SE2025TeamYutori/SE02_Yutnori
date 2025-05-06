package org.cau02;

import org.cau02.controller.SwingGameController;
import org.cau02.model.SwingGame;
import org.cau02.view.SwingGameView;

public class Main {
    public static void main(String[] args) {
        try {
            // Default game settings
            int boardAngle = 4; // Square board
            int playerCount = 2;
            int pieceCount = 2;
            
            // Create MVC components for the Swing implementation
            SwingGame gameModel = new SwingGame(boardAngle, playerCount, pieceCount);
            SwingGameView gameView = new SwingGameView(gameModel.getGameManager());
            SwingGameController gameController = new SwingGameController(gameModel, gameView);
            
            // Start the game
            gameController.startGame();
            
        } catch (Exception e) {
            System.err.println("애플리케이션 실행 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}