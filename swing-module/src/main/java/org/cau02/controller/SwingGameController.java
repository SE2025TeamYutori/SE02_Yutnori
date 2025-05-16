package org.cau02.controller;

import org.cau02.model.GameManager;
import org.cau02.view.SwingGameView;

public class SwingGameController {
    private final GameManager gameManager;
    private final SwingGameView view;
    
    // 기본 게임 설정
    private static final int DEFAULT_BOARD_SHAPE = 4; // 정사각형 보드
    private static final int DEFAULT_PLAYER_COUNT = 2; // 2명의 플레이어
    private static final int DEFAULT_PIECE_COUNT = 2; // 플레이어당 2개의 말

    public SwingGameController(GameManager gameManager, SwingGameView view) {
        this.gameManager = gameManager;
        this.view = view;
    }

    
    //게임 시작
    public void startGame() {
        // 먼저 뷰를 초기화
        view.initialize();
        
        // 기본 설정으로 게임 초기화
        setDefaultGameSettings();
    }

    //기본 게임 설정
    private void setDefaultGameSettings() {
        try {
            // 기본 게임 매개변수 설정
            gameManager.setBoard(DEFAULT_BOARD_SHAPE);
            gameManager.setPlayerCount(DEFAULT_PLAYER_COUNT);
            gameManager.setPieceCount(DEFAULT_PIECE_COUNT);
            
            // 참고: 실제 게임 시작은 MainGamePanel의 UI 컨트롤에서 처리됩니다
            // 사용자가 "게임 시작" 버튼을 클릭하면 이러한 설정으로 게임이 시작됩니다
        } catch (Exception e) {
            System.err.println("기본 게임 매개변수 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //게임 리셋
    public void resetGame() {
        try {
            view.resetGame();
        } catch (Exception e) {
            System.err.println("게임 리셋 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //게임 모델 반환
    public GameManager getGameManager() {
        return gameManager;
    }

    //게임 뷰 반환
    public SwingGameView getView() {
        return view;
    }
}