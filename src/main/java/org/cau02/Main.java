package org.cau02;

import org.cau02.controller.GameController;
import org.cau02.model.old.Game;
import org.cau02.view.GameView;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Model, View, Controller 객체 생성 및 의존성 주입
            //    - Game: 기본 생성자 사용 (RandomYutStick, TurnService 자동 사용)
            //    - GameView: ConsoleView 사용
            Game gameModel = new Game();
            GameView gameView = null; // TODO: 여기서 UI 구현을 작성해야함!(임시적 TextView이든, 실제 FXView이든..)
            GameController gameController = new GameController(gameModel, gameView);

            // 2. 게임 시작
            gameController.startGame();

        } catch (Exception e) {
            System.err.println("애플리케이션 실행 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}