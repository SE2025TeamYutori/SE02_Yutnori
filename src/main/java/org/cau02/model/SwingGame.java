package org.cau02.model;


public class SwingGame {
    private final GameManager gameManager;
    
    /**
     * @param boardAngle 보드의 모양 (예: 4는 정사각형)
     * @param playerCount 게임의 플레이어 수
     * @param pieceCount 플레이어당 말의 수
     */
    public SwingGame(int boardAngle, int playerCount, int pieceCount) {
        this.gameManager = new GameManager(boardAngle, playerCount, pieceCount);
    }
    

    public GameManager getGameManager() {
        return gameManager;
    }
    
    //새 게임 시작
    public void startGame() {
        gameManager.startGame();
    }
    
    //현재 게임 리셋
    public void resetGame() {
        gameManager.resetGame();
    }
    
    //보드 모양 설정
    public void setBoard(int boardAngle) {
        gameManager.setBoard(boardAngle);
    }
    
    //플레이어 수 설정
    public void setPlayerCount(int playerCount) {
        gameManager.setPlayerCount(playerCount);
    }
    
    //플레이어당 말의 수 설정
    public void setPieceCount(int pieceCount) {
        gameManager.setPieceCount(pieceCount);
    }
    

    //옵저버 등록
    public void registerObserver(YutNoriObserver observer) {
        gameManager.registerObserver(observer);
    }
    

    //옵저버 제거
    public void unregisterObserver(YutNoriObserver observer) {
        gameManager.unregisterObserver(observer);
    }
    


    //랜덤 윷 던지기
    public Yut throwRandomYut() {
        return gameManager.throwRandomYut();
    }
    
    /**
     * 선택한 윷을 던집니다.
     * 
     * @param selectedYut 던질 윷
     * @return 윷 던지기의 결과
     */
    public Yut throwSelectedYut(Yut selectedYut) {
        return gameManager.throwSelectedYut(selectedYut);
    }
    
    /**
     * 새로운 말을 이동시킵니다.
     * 
     * @param yut 이동에 사용할 윷
     */
    public void moveNewPiece(Yut yut) {
        gameManager.moveNewPiece(yut);
    }
    
    /**
     * 기존 말을 이동시킵니다.
     * 
     * @param piece 이동할 말
     * @param yut 이동에 사용할 윷
     */
    public void movePiece(Piece piece, Yut yut) {
        gameManager.movePiece(piece, yut);
    }
    

    //현재 게임 상태 반환
    public GameState getState() {
        return gameManager.getState();
    }
    
    //현재 플레이어 반환
    public Integer getCurrentPlayer() {
        return gameManager.getCurrentPlayer();
    }
}