package org.cau02.model;

import org.cau02.model.interfaces.IBoardConfiguration;
import org.cau02.model.interfaces.YutStickBehavior;
import org.cau02.view.GameView;

import java.util.*;

/** 윷놀이 게임의 전체 상태와 진행 로직 관리 (Model의 메인 클래스) */
public class Game {
    private Board board;
    private List<Player> players = new ArrayList<>();
    private Player currentPlayer;
    private GameStatus status = GameStatus.NOT_STARTED;
    private int turnCount = 0;

    // 핵심 로직을 수행하는 서비스/행동 객체 (인터페이스 타입 사용)
    private YutStickBehavior yutStickBehavior;
    private MovementService movementService;
    private TurnService turnService;

    /** 기본 생성자: 표준 의존성(RandomYutStick, TurnService) 사용 */
    public Game() {
        this(new RandomYutStick(), new TurnService());
    }

    /** 의존성 주입 생성자: 테스트 또는 커스텀 로직 사용 시 */
    public Game(YutStickBehavior yutStickBehavior, TurnService turnService) {
        this.yutStickBehavior = Objects.requireNonNull(yutStickBehavior);
        this.turnService = Objects.requireNonNull(turnService);
    }

    /**
     * 게임을 초기화합니다.
     * @param playerNames 플레이어 이름 목록 (2-4명)
     * @param numberOfPieces 플레이어당 말 개수 (2-5개)
     * @param boardConfig 사용할 게임판 설정
     */
    public void initializeGame(List<String> playerNames, int numberOfPieces, IBoardConfiguration boardConfig) {
        // 입력값 검증
        if (playerNames == null || playerNames.size() < 2 || playerNames.size() > 4) {
            throw new IllegalArgumentException("플레이어는 2명에서 4명까지 가능합니다.");
        }
        // 말 개수 검증은 Player 생성자에서 수행됨
        Objects.requireNonNull(boardConfig, "Board 설정 객체는 null일 수 없습니다.");

        // 플레이어 생성
        this.players.clear();
        Color[] colors = Color.values();
        for (int i = 0; i < playerNames.size(); i++) {
            players.add(new Player(playerNames.get(i), colors[i % colors.length], numberOfPieces));
        }

        // 게임판 및 서비스 초기화
        this.board = new Board(boardConfig);
        this.movementService = new MovementService(this.board, this.players); // Board와 Player 목록 필요

        // 게임 상태 설정
        this.currentPlayer = players.get(0); // 첫 번째 플레이어부터 시작
        this.status = GameStatus.IN_PROGRESS;
        this.turnCount = 1;
    }

    /** 현재 플레이어가 윷을 던지고 결과를 반환합니다. */
    public YutResult playerThrowsYut() {
        if (status != GameStatus.IN_PROGRESS) return null;
        return yutStickBehavior.throwSticks(); // 설정된 윷 던지기 방식 사용
    }

    /** 특정 말과 윷 결과에 대한 이동 가능 목록을 반환합니다. */
    public List<Move> getAvailableMovesForPiece(Piece piece, YutResult result) {
        if (status != GameStatus.IN_PROGRESS || movementService == null) return Collections.emptyList();
        return movementService.calculateValidMoves(piece, result);
    }

    /** 현재 플레이어가 가능한 이동 목록 중 하나를 선택합니다. */
    public Move playerChoosesMove(List<Move> possibleMoves, GameView view) {
        if (status != GameStatus.IN_PROGRESS || currentPlayer == null) return null;
        // 실제 선택 로직은 Player 객체(Human/AI)에 위임
        return currentPlayer.chooseMove(possibleMoves, view);
    }

    /** 선택된 이동을 게임 상태에 반영합니다. */
    public void executePlayerMove(Move move) {
        if (status != GameStatus.IN_PROGRESS || movementService == null || move == null) return;
        movementService.executeMove(move);
        // 이동 후 게임 상태 변경(점수 등)은 MovementService -> Player 에서 처리됨
    }

    /** 게임 승리 조건을 확인합니다. (현재 플레이어 기준) */
    public boolean checkWinCondition() {
        if (status != GameStatus.IN_PROGRESS || currentPlayer == null) return false;
        // 현재 턴을 마친 플레이어가 모든 말을 완주했는지 확인
        if (currentPlayer.hasFinishedAllPieces()) {
            status = GameStatus.FINISHED; // 게임 상태 변경
            return true;
        }
        return false;
    }

    /** 보너스 턴 여부를 확인합니다. */
    public boolean checkForBonusTurn(YutResult result, Move lastMove) {
        if (status != GameStatus.IN_PROGRESS || turnService == null) return false;
        return turnService.shouldGetBonusTurn(result, lastMove);
    }

    /** 다음 플레이어로 턴을 넘깁니다. */
    public void advanceToNextTurn() {
        if (status != GameStatus.IN_PROGRESS || turnService == null) return;
        turnService.advanceTurn(this);
    }

    // --- 내부 상태 변경용 메서드 (protected) ---
    protected void setCurrentPlayerInternal(Player player) {
        this.currentPlayer = player;
    }

    protected void incrementTurnCountInternal() {
        this.turnCount++;
    }

    // --- Getters (View/Controller 용) ---
    public Board getBoard() { return board; }
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
    public Player getCurrentPlayer() { return currentPlayer; }
    public GameStatus getStatus() { return status; }
    public int getTurnCount() { return turnCount; }
}