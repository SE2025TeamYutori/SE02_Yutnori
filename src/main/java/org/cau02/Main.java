package org.cau02;

import org.cau02.controller.SwingGameController;
import org.cau02.model.GameManager;
import org.cau02.view.SwingGameView;

public class Main {
    public static void main(String[] args) {
        try {
            // 게임 매니저 생성
            GameManager gameManager = new GameManager(4, 2, 2);
            
            // 뷰 생성 
            SwingGameView gameView = new SwingGameView(gameManager);
            
            // 컨트롤러 생성
            SwingGameController gameController = new SwingGameController(gameManager, gameView);
            
            // 게임 시작
            gameController.startGame();
            
        } catch (Exception e) {
            System.err.println("애플리케이션 실행 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}