package org.cau02.model;

import java.util.*;

/**
 * 윷놀이 게임 하나를 관리하는 클래스입니다.<br>
 * 한 인스턴스가 하나의 게임을 관리합니다.<br>
 */
public class GameManager {
    // constants
    private static final int MIN_PLAYERS = 2; // 최소 플레이 인원
    private static final int MAX_PLAYERS = 4; // 최대 플레이 인원
    private static final int MIN_PIECES = 2; // 플레이어당 최소 말 수
    private static final int MAX_PIECES = 5; // 플레이어당 최대 말 수

    // utils
    private final Random random = new Random();

    // observers
    private final List<YutNoriObserver> observers = new ArrayList<>();

    // properties
    private Board board; // 게임판 인스턴스
    private int playerCount; // 게임에 참여하는 플레이어 수
    private int pieceCount; // 플레이어 당 말 수

    // states
    private GameState state = GameState.READY; // 게임 상태
    private Integer winner = null; // 승자
    private Integer currentPlayer = null; // 현재 턴 플레이어; 플레이어는 숫자로 관리
    private int currentYutCount = 0; // 현재 턴 플레이어가 던질 수 있는 윷 횟수
    private final int[] yutResult = new int[Yut.values().length]; // 현재 턴 플레이어가 움직일 수 있는 윷 족보별 횟수

    // 말들 관리 컬렉션들
    // 말은 기본적으로 readPieces <-> activePieces -> goalPieces 로 관리됨
    // 인덱스는 플레이어 번호
    @SuppressWarnings("unchecked")
    private final Queue<Piece>[] readyPieces = (Queue<Piece>[]) new Queue[MAX_PIECES]; // 출발 전의 말들 큐
    @SuppressWarnings("unchecked")
    private final Set<Piece>[] activePieces = (HashSet<Piece>[]) new HashSet[MAX_PIECES]; // 게임판 위의 말들 셋
    @SuppressWarnings("unchecked")
    private final Queue<Piece>[] goalPieces = (Queue<Piece>[]) new Queue[MAX_PIECES]; // 골인한 말들 큐

    // getters & setters
    /**
     * 보드 인스턴스를 반환합니다
     * @return 보드 인스턴스
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 게임판의 모양을 설정합니다.
     * @param boardAngle n다각형 게임판의 n
     * @throws IllegalArgumentException 정의되지 않은 판 모양을 생성하려 시도 할 경우
     * @throws IllegalStateException 게임의 상태가 {@link GameState#READY}가 아닐 경우
     */
    public void setBoard(int boardAngle) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.READY) {
            throw new IllegalStateException("게임 시작 전에만 게임판을 설정 가능합니다.");
        }
        this.board = BoardFactory.createBoard(boardAngle); // 팩토리로 생성
    }

    /**
     * 게임에 참여하는 플레이어 수를 반환합니다.
     * @return 게임에 참여하는 플레이어 수
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * 게임에 참여하는 플레이어 수를 설정합니다.
     * @param playerCount 게임에 참여할 플레이어 수
     * @throws IllegalArgumentException 허용된 범위의 플레이어 수(2~4)가 아닐 경우
     * @throws IllegalStateException 게임의 상태가 {@link GameState#READY}가 아닐 경우
     */
    public void setPlayerCount(int playerCount) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.READY) {
            throw new IllegalStateException("게임 시작 전에만 플레이어 수를 설정 가능합니다.");
        }
        if (playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS) {
            throw new IllegalArgumentException("허용된 플레이어 수 범위가 아닙니다.");
        }
        this.playerCount = playerCount;
    }

    /**
     * 플레이어당 말의 개수를 반환합니다.
     * @return 플레이어당 말의 개수
     */
    public int getPieceCount() {
        return pieceCount;
    }

    /**
     * 플레이어당 말의 개수를 설정합니다.
     * @param pieceCount 설정할 플레이어당 말의 개수
     * @throws IllegalArgumentException 허용된 범위의 말의 수(2~5)가 아닐 경우
     * @throws IllegalStateException 게임의 상태가 {@link GameState#READY}가 아닐 경우
     */
    public void setPieceCount(int pieceCount) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.READY) {
            throw new IllegalStateException("게임 시작 전에만 플레이어당 말의 개수를 설정 가능합니다.");
        }
        if (pieceCount < MIN_PIECES || pieceCount > MAX_PIECES) {
            throw new IllegalArgumentException("허용된 플레이어당 말 개수 범위가 아닙니다.");
        }
        this.pieceCount = pieceCount;
    }

    /**
     * 현재 게임의 상태를 반환합니다
     * @return 현재 게임의 상태
     * <ul>
        * <li>{@link GameState#READY}: 게임 시작 전</li>
        * <li>{@link GameState#PLAYING}: 게임 중</li>
        * <li>{@link GameState#FINISHED}: 게임 종료</li>
     * </ul>
     */
    public GameState getState() {
        return state;
    }

    /**
     * 게임에서 승리한 플레이어의 번호를 반환합니다.
     * @return 승리한 플레이어의 번호.
     * 단, 게임 상태가 {@link GameState#FINISHED}가 아닐 경우 null 반환
     */
    public Integer getWinner() {
        if (state != GameState.FINISHED) {
            return null;
        }
        return winner;
    }

    /**
     * 현재 턴의 플레이어 번호를 반환합니다.
     * @return 현재 턴의 플레이어 번호.
     * 단, 게임 상태가 {@link GameState#PLAYING}이 아닐 경우 null 반환
     */
    public Integer getCurrentPlayer() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return currentPlayer;
    }

    /**
     * 현재 턴의 플레이어가 던질 수 있는 윷의 남은 횟수를 반환합니다.
     * @return 현재 턴의 플레이어가 던질 수 있는 윷의 남은 횟수.
     * 단, 게임 상태가 {@link GameState#PLAYING}이 아닐 경우 null 반환
     */
    public Integer getCurrentYutCount() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return currentYutCount;
    }
    
    /**
     * 현재 턴의 플레이어가 말을 이동할 수 있는 남은 횟수를 반환합니다.
     * @return 현재 턴의 플레이어가 말을 이동할 수 있는 남은 횟수.
     * 단, 게임 상태가 {@link GameState#PLAYING}이 아닐 경우 null 반환
     */
    public Integer getCurrentMoveCount() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return Arrays.stream(yutResult).sum(); // 족보별 남은 이동 가능 횟수들의 총합으로 구함
    }

    /**
     * 현재 턴의 플레이어의 족보별 남은 이동 가능 횟수를 반환합니다.
     * @return 현재 턴의 플레이어의 족보별 남은 이동 가능 횟수 List.
     * 인덱스는 {@link Yut}의 ordinal
     * 단, 게임 상태가 {@link GameState#PLAYING}이 아닐 경우 null 반환
     */
    public List<Integer> getYutResult() {
        if (state != GameState.PLAYING) {
            return null;
        }
        return Arrays.stream(yutResult).boxed().toList(); // yutResult를 불변 리스트로 반환
    }

    /**
     * 해당하는 플레이어의 출발 전 말의 개수를 반환합니다.
     * @param owner 확인할 플레이어 번호
     * @return 해당 플레이어의 출발 전 말 개수
     * @throws IllegalArgumentException 잘못된 플레이어 번호를 입력할 경우
     * @throws IllegalStateException 게임 상태가 {@link GameState#PLAYING}이 아닐 경우
     */
    public int getReadyPiecesCount(int owner) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("게임 중에만 호출 가능합니다.");
        }
        if (owner < 0 || owner >= playerCount) {
            throw new IllegalArgumentException("존재하는 플레이어 번호를 입력해야 합니다.");
        }
        return readyPieces[owner].size();
    }

    /**
     * 해당하는 플레이어의 게임판 위에 존재하는 말들의 Set을 반환합니다.
     * @param owner 확인할 플레이어 번호
     * @return 해당하는 플레이어의 게임판 위에 존재하는 말들의 Set
     * @throws IllegalArgumentException 잘못된 플레이어 번호를 입력할 경우
     * @throws IllegalStateException 게임 상태가 {@link GameState#PLAYING}이 아닐 경우
     */
    public Set<Piece> getActivePieces(int owner) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("게임 중에만 호출 가능합니다.");
        }
        if (owner < 0 || owner >= playerCount) {
            throw new IllegalArgumentException("존재하는 플레이어 번호를 입력해야 합니다.");
        }
        return Set.copyOf(activePieces[owner]); // 불변 Set으로 반환
    }

    /**
     * 해당하는 플레이어의 골인한 말의 개수를 반환합니다.
     * @param owner 확인할 플레이어 번호
     * @return 해당 플레이어의 골인한 말 개수
     * @throws IllegalArgumentException 잘못된 플레이어 번호를 입력할 경우
     * @throws IllegalStateException 게임 상태가 {@link GameState#PLAYING}이 아닐 경우
     */
    public int getGoalPiecesCount(int owner) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("게임 중에만 호출 가능합니다.");
        }
        if (owner < 0 || owner >= playerCount) {
            throw new IllegalArgumentException("존재하는 플레이어 번호를 입력해야 합니다.");
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
    /**
     * 게임의 상태를 {@link GameState::READY}로 바꿉니다.
     */
    public void resetGame() {
        state = GameState.READY;
        winner = null;
        currentPlayer = null;
    }

    /**
     * 게임을 시작합니다.
     */
    public void startGame() {
        resetGame();

        state = GameState.PLAYING;

        // 0번 플레이어 턴 시작
        currentPlayer = 0;
        currentYutCount = 1;
        Arrays.fill(yutResult, 0);

        // 말들과 그 풀들 초기화
        for (int i = 0; i < playerCount; i++) {
            readyPieces[i] = new LinkedList<>();
            for (int j = 0; j < pieceCount; j++) {
                readyPieces[i].add(new Piece(board.getReadySpace(), board.getDefaultPath(), i));
            }
            activePieces[i] = new HashSet<>();
            goalPieces[i] = new LinkedList<>();
        }
    }

    /**
     * 무작위 윷을 던집니다.
     * @return 던진 윷의 결과
     * @throws IllegalStateException 윷을 던질 수 있는 횟수가 없을 경우 or 게임 상태가 {@link GameState#PLAYING}이 아닐 경우
     */
    public Yut throwRandomYut() throws IllegalStateException {
        // 랜덤 Yut 값을 생성하고, 선택 윷 던지기를 호출
        Yut randomYut = Yut.values()[random.nextInt(Yut.values().length)];
        return throwSelectedYut(randomYut);
    }

    /**
     * 선택한 윷을 던집니다.
     * @param selectedYut 던질 윷
     * @return 던진 윷의 결과
     * @throws IllegalStateException 윷을 던질 수 있는 횟수가 없을 경우 or 게임 상태가 {@link GameState#PLAYING}이 아닐 경우
     */
    public Yut throwSelectedYut(Yut selectedYut) throws IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("게임 중에만 윷을 던질 수 있습니다.");
        }
        if (currentYutCount <= 0) {
            throw new IllegalStateException("윷을 던질 수 있는 횟수가 남지 않았습니다.");
        }

        // 해당 족보의 이동 가능 횟수 추가
        yutResult[selectedYut.ordinal()]++;
        
        // 윷이나 모가 아닐 경우 윷 던지기 가능 횟수 차감
        if (selectedYut.ordinal() < Yut.YUT.ordinal()) {
            currentYutCount--;
        }

        notifyYutStateChanged();

        // 윷 던지기만으로 종료 가능하므로 체크
        checkState();

        return selectedYut;
    }

    /**
     * 현재 족보별 이동 가능 횟수를 바탕으로, 새 말이 이동 가능한 위치들을 List로 반환합니다.+
     * 리스트의 각 index는 Yut의 ordinal 입니다. (움직임 불가한 족보의 경우 null)
     * @return 새 말이 이동 가능한 위치들의 List , 움직임 불가한 족보의 위치는 null
     * 출발 전 말이 없을 경우 null을 반환합니다.
     * @throws IllegalStateException 게임 상태가 {@link GameState#PLAYING}이 아닐 경우
     */
    public List<BoardSpace> getPossibleLocationsOfNewPiece() throws IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("게임 중에만 말을 이동할 수 있습니다.");
        }

        // 출발 전 말이 없을 경우 null 리턴
        if (getReadyPiecesCount(currentPlayer) == 0) {
            return null;
        }

        // 출발 전 말 큐에서 하나를 peek하고, 그것으로 getPossibleLocations 호출
        return getPossibleLocations(readyPieces[currentPlayer].peek());
    }

    /**
     * 현재 족보별 이동 가능 횟수를 바탕으로, 해당 말이 이동 가능한 위치들을 List로 반환합니다.
     * 리스트의 각 index는 Yut의 ordinal 입니다. (움직임 불가한 족보의 경우 null)
     * @param piece 이동 가능 위치들을 확인할 말
     * @return 해당 말이 이동 가능한 위치들의 List, 움직임 불가한 족보의 위치는 null
     * 출발 전 말이 없을 경우 null을 반환합니다.
     * @throws IllegalArgumentException 말의 상태가 {@link PieceState#GOAL}일 경우
     * @throws IllegalStateException 게임 상태가 {@link GameState#PLAYING}이 아닐 경우
     */
    public List<BoardSpace> getPossibleLocations(Piece piece) throws IllegalArgumentException, IllegalStateException {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("게임 중에만 말을 이동할 수 있습니다.");
        }

        BoardSpace[] locations = new BoardSpace[Yut.values().length];
        // 빽도는 ACTIVE 상태나 CARRIED 상태일때만 계산 가능
        if ((piece.getState() == PieceState.ACTIVE || piece.getState() == PieceState.CARRIED) && yutResult[0] > 0) {
            locations[Yut.BACKDO.ordinal()] = board.getPossibleLocation(piece, Yut.BACKDO);
        }
        // 빽도 제외 족보별 확인
        for (int i = 1; i < yutResult.length; i++) {
            if (yutResult[i] > 0) {
                locations[i] = board.getPossibleLocation(piece, Yut.values()[i]); // 말의 상태가 GOAL일 경우 여기서 예외 발생
            }
        }
        return Arrays.stream(locations).toList(); // 불변 List로 리턴
    }

    /**
     * 새 말 하나를 이동합니다.
     * @param yut 윷 족보
     * @throws IllegalStateException 해당 족보의 이동 가능 횟수가 없거나 게임의 상태가 {@link GameState::PLAYING}이 아닐 경우
     */
    public void moveNewPiece(Yut yut) throws IllegalStateException {
        // 레디 큐에서 액티브 셋으로 말 하나 이동
        Piece piece = readyPieces[currentPlayer].remove();
        activePieces[currentPlayer].add(piece);
        try {
            // 해당 말을 이동시킴
            movePiece(piece, yut);
        } catch (IllegalStateException e) {
            // 예외가 발생했을 경우 아까 이동한 말 원복
            activePieces[currentPlayer].remove(piece);
            readyPieces[currentPlayer].add(piece);
            throw e;
        }
    }

    /**
     * 해당 말을 이동합니다.
     * @param piece 이동할 말
     * @param yut 윷 족보
     * @throws IllegalArgumentException 해당 말의 소유자가 현재 턴의 플레이어가 아닐 경우
     * @throws IllegalStateException 해당 족보의 이동 가능 횟수가 없거나 게임의 상태가 {@link GameState::PLAYING}이 아닐 경우
     */
    public void movePiece(Piece piece, Yut yut) throws IllegalArgumentException, IllegalStateException {
        if (piece.getOwner() != currentPlayer) {
            throw new IllegalArgumentException("움직이려는 말의 소유자가 현재 턴의 플레이어가 아닙니다.");
        }
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("게임 중에만 말을 이동할 수 있습니다.");
        }
        if (yutResult[yut.ordinal()] <= 0) {
            throw new IllegalStateException("해당 족보의 이동 가능 횟수가 없습니다.");
        }

        MoveResult movedResult = board.move(piece, yut); // 말 이동
        yutResult[yut.ordinal()]--; // 해당 족보의 이동 가능 횟수 차감

        // 윷이나 모를 사용하지 않고 말을 잡았을 경우 윷 던질수 있는 횟
        // 수 추가
        if (movedResult == MoveResult.CATCH && yut != Yut.YUT && yut != Yut.MO) {
            currentYutCount++;
        }
        notifyYutStateChanged();

        // 말들 풀 갱신
        for (int i = 0; i < playerCount; i++) {
            Set<Piece> activePiecesForLoop = new HashSet<>(activePieces[i]); // loop를 사용하기 위한 복사본 생성
            for (Piece p : activePiecesForLoop) {
                switch (p.getState()) {
                    case READY: // 아마 잡힌 경우
                        activePieces[i].remove(p);
                        readyPieces[i].add(p);
                        break;
                    case GOAL: // 도착한 경우
                        activePieces[i].remove(p);
                        goalPieces[i].add(p);
                        break;
                }
            }
        }
        notifyPieceMoved();

        // 상태 체크
        checkState();
    }

    /*
     * 상태를 체크하는 메소드
     * 윷 던지거나 말 이동 후 호출
     * 게임 종료나 턴 넘어가기 여부 판단
     */
    private void checkState() {
        // 말이 다 도착하면 게임 종료
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

        // 대기 말밖에 없는데, 윷도 더 못던지고 빽도 횟수만 있으면 턴 넘어감
        if (getCurrentYutCount() == 0 && getActivePieces(currentPlayer).isEmpty()) {
            // 빽도만 횟수만 있는지 체크
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

    /**
     * 옵저버를 등록합니다
     * @param o 등록할 옵저버
     */
    public void registerObserver(YutNoriObserver o) {
        observers.add(o);
    }

    /**
     * 등록한 옵저버를 등록 해제합니다
     * @param o 등록 해제할 옵저버
     */
    public void unregisterObserver(YutNoriObserver o) {
        observers.remove(o);
    }

    // 옵저버들에게 게임 종료 알림
    private void notifyGameEnded() {
        for (YutNoriObserver o : observers) {
            o.onGameEnded();
        }
    }

    // 옵저버들에게 턴 바뀌었다고 알림
    private void notifyTurnChanged() {
        for (YutNoriObserver o : observers) {
            o.onTurnChanged();
            o.onYutStateChanged(); // 턴 바뀌면 윷 상태도 초기화되니 같이 알림
        }
    }

    // 옵저버들에게 윷 상태 바뀌었다고 알림
    private void notifyYutStateChanged() {
        for (YutNoriObserver o : observers) {
            o.onYutStateChanged();
        }
    }

    // 옵저버들에게 말 상태 바뀌었다고 알림
    private void notifyPieceMoved() {
        for (YutNoriObserver o : observers) {
            o.onPieceMoved();
        }
    }
}
