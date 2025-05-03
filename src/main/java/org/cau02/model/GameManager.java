package org.cau02.model;

import java.util.*;

/**
 * 윷놀이 게임 하나를 관리하는 클래스입니다.<br>
 * 한 인스턴스가 하나의 게임을 관리합니다.<br>
 */
public class GameManager {
    // constants
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PIECES = 2;
    private static final int MAX_PIECES = 5;

    // utils
    private final Random random = new Random();

    // observers
    private final List<YutNoriObserver> observers = new ArrayList<>();

    // properties
    private Board board;
    private int playerCount;
    private int pieceCount;

    // states
    private GameState state = GameState.READY;
    private Integer winner = null;
    private Integer currentPlayer = null;
    private int currentYutCount = 0;
    private final int[] yutResult = new int[Yut.values().length];
    @SuppressWarnings("unchecked")
    private final Queue<Piece>[] readyPieces = (Queue<Piece>[]) new Queue[MAX_PIECES];
    @SuppressWarnings("unchecked")
    private final Set<Piece>[] activePieces = (HashSet<Piece>[]) new HashSet[MAX_PIECES];
    @SuppressWarnings("unchecked")
    private final Queue<Piece>[] goalPieces = (Queue<Piece>[]) new Queue[MAX_PIECES];

    // getters & setters
    public Board getBoard() {
        return board;
    }

    public void setBoard(int boardAngle) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.READY) {
            throw new IllegalStateException();
        }
        this.board = BoardFactory.createBoard(boardAngle);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.READY) {
            throw new IllegalStateException();
        }
        if (playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS) {
            throw new IllegalArgumentException();
        }
        this.playerCount = playerCount;
    }

    public int getPieceCount() {
        return pieceCount;
    }

    public void setPieceCount(int pieceCount) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.READY) {
            throw new IllegalStateException();
        }
        if (pieceCount < MIN_PIECES || pieceCount > MAX_PIECES) {
            throw new IllegalArgumentException();
        }
        this.pieceCount = pieceCount;
    }

    public GameState getState() {
        return state;
    }

    public Integer getWinner() {
        if (state != GameState.FINISHED) {
            return null;
        }
        return winner;
    }

    public Integer getCurrentPlayer() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return currentPlayer;
    }

    public Integer getCurrentYutCount() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return currentYutCount;
    }

    public Integer getCurrentMoveCount() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return Arrays.stream(yutResult).sum();
    }

    public List<Integer> getYutResult() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return Arrays.stream(yutResult).boxed().toList();
    }

    public int getReadyPiecesCount(int owner) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException();
        }
        if (owner < 0 || owner >= playerCount) {
            throw new IllegalArgumentException();
        }
        return readyPieces[owner].size();
    }

    public Set<Piece> getActivePieces(int owner) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException();
        }
        if (owner < 0 || owner >= playerCount) {
            throw new IllegalArgumentException();
        }
        return Set.copyOf(activePieces[owner]);
    }

    public int getGoalPiecesCount(int owner) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException();
        }
        if (owner < 0 || owner >= playerCount) {
            throw new IllegalArgumentException();
        }
        return goalPieces[owner].size();
    }

    // constructor
    /**
     * @param boardAngle 게임판 모양(n각형의 n); 현재 4, 5, 6만 지원
     * @param playerCount 게임에 참여하는 플레이어 수 {@link GameManager#MIN_PLAYERS}~{@link GameManager#MAX_PLAYERS}
     * @param pieceCount 플레이어당 말의 개수 {@link GameManager#MIN_PIECES}~{@link GameManager#MAX_PIECES}
     */
    public GameManager(int boardAngle, int playerCount, int pieceCount) throws IllegalArgumentException {
        setBoard(boardAngle);
        setPlayerCount(playerCount);
        setPieceCount(pieceCount);
    }

    // methods
    public void resetGame() {
        state = GameState.READY;
        winner = null;
        currentPlayer = null;
    }

    public void startGame() {
        resetGame();

        state = GameState.PLAYING;
        currentPlayer = 0;
        currentYutCount = 1;
        Arrays.fill(yutResult, 0);
        for (int i = 0; i < playerCount; i++) {
            readyPieces[i] = new LinkedList<>();
            for (int j = 0; j < pieceCount; j++) {
                readyPieces[i].add(new Piece(board, i));
            }
            activePieces[i] = new HashSet<>();
            goalPieces[i] = new LinkedList<>();
        }
    }

    public Yut throwRandomYut() throws IllegalStateException {
        Yut randomYut = Yut.values()[random.nextInt(Yut.values().length)];
        return throwSelectedYut(randomYut);
    }

    public Yut throwSelectedYut(Yut selectedYut) throws IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException();
        }
        if (currentYutCount <= 0) {
            throw new IllegalStateException();
        }

        yutResult[selectedYut.ordinal()]++;
        if (selectedYut.ordinal() < Yut.YUT.ordinal()) {
            currentYutCount--;
        }

        notifyYutStateChanged();

        checkState();

        return selectedYut;
    }

    public List<BoardSpace> getPossibleLocationsOfNewPiece() {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException();
        }

        if (getReadyPiecesCount(currentPlayer) == 0) {
            return null;
        }

        return getPossibleLocations(readyPieces[currentPlayer].peek());
    }

    public List<BoardSpace> getPossibleLocations(Piece piece) {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException();
        }

        List<BoardSpace> locations = new ArrayList<>();
        if (piece.getState() == PieceState.ACTIVE && yutResult[0] > 0) {
            locations.add(piece.getPossibleLocation(Yut.BACKDO));
        }
        for (int i = 1; i < yutResult.length; i++) {
            if (yutResult[i] > 0) {
                locations.add(piece.getPossibleLocation(Yut.values()[i]));
            }
        }
        return List.copyOf(locations);
    }

    public void moveNewPiece(Yut yut) throws IllegalStateException {
        Piece piece = readyPieces[currentPlayer].remove();
        activePieces[currentPlayer].add(piece);
        try {
            movePiece(piece, yut);
        } catch (IllegalStateException e) {
            activePieces[currentPlayer].remove(piece);
            readyPieces[currentPlayer].add(piece);
            throw e;
        }
    }

    public void movePiece(Piece piece, Yut yut) throws IllegalArgumentException, IllegalStateException {
        if (piece.getOwner() != currentPlayer) {
            throw new IllegalArgumentException();
        }
        if (state != GameState.PLAYING) {
            throw new IllegalStateException();
        }
        if (yutResult[yut.ordinal()] <= 0) {
            throw new IllegalStateException();
        }

        piece.move(yut);
        MoveResult movedResult = board.setPieceOnBoardSpace(piece, piece.getLocation());
        yutResult[yut.ordinal()]--;

        if (movedResult == MoveResult.CATCH && yut != Yut.YUT && yut != Yut.MO) {
            currentYutCount++;
        }
        notifyYutStateChanged();

        // 말들 풀 설정
        for (int i = 0; i < playerCount; i++) {
            Set<Piece> activePiecesForLoop = new HashSet<>(activePieces[i]);
            for (Piece p : activePiecesForLoop) {
                switch (p.getState()) {
                    case READY:
                        activePieces[i].remove(p);
                        readyPieces[i].add(p);
                        break;
                    case GOAL:
                        activePieces[i].remove(p);
                        goalPieces[i].add(p);
                        break;
                }
            }
        }
        notifyPieceMoved();

        checkState();
    }

    private void checkState() {
        // 말이 다 도착하면 종료
        if (getGoalPiecesCount(currentPlayer) == pieceCount) {
            winner = currentPlayer;
            state = GameState.FINISHED;

            notifyGameEnded();
            return;
        }

        // 행동 모두 소모하면 턴 넘어감
        if (getCurrentMoveCount() == 0 && getCurrentYutCount() == 0){
            currentPlayer = (currentPlayer + 1) % playerCount;
            currentYutCount = 1;
            Arrays.fill(yutResult, 0);

            notifyTurnChanged();
            return;
        }

        // 대기 말밖에 없는데, 빽도 횟수만 남으면 턴 넘어감
        if (getCurrentYutCount() == 0 && getActivePieces(currentPlayer).isEmpty()) {
            for (int i = 1; i < yutResult.length; i++) {
                if (yutResult[i] > 0) {
                    return;
                }
            }
            currentPlayer = (currentPlayer + 1) % playerCount;
            currentYutCount = 1;
            Arrays.fill(yutResult, 0);

            notifyTurnChanged();
        }
    }

    //Observer notify methods
    public void registerObserver(YutNoriObserver o) {
        observers.add(o);
    }

    public void unregisterObserver(YutNoriObserver o) {
        observers.remove(o);
    }

    private void notifyGameEnded() {
        for (YutNoriObserver o : observers) {
            o.onGameEnded();
        }
    }

    private void notifyTurnChanged() {
        for (YutNoriObserver o : observers) {
            o.onTurnChanged();
            o.onYutStateChanged();
        }
    }

    private void notifyYutStateChanged() {
        for (YutNoriObserver o : observers) {
            o.onYutStateChanged();
        }
    }

    private void notifyPieceMoved() {
        for (YutNoriObserver o : observers) {
            o.onPieceMoved();
        }
    }

}
