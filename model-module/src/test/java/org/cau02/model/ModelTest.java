package org.cau02.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class YutNoriGameTest {

    private static final Random random = new Random();
    private GameManager gameManager;
    private TestObserver testObserver;

    @TempDir
    Path tempDir;

    /**
     * 게임 이벤트 캡처 및 로깅을 위한 테스트 옵저버
     */
    static class TestObserver implements YutNoriObserver {
        private final GameManager gm;
        private final StringBuilder logBuilder = new StringBuilder();
        private final List<String> gameEvents = new ArrayList<>();
        private Path logFile;

        TestObserver(GameManager gm, Path logFile) {
            this.gm = gm;
            this.logFile = logFile;
        }

        @Override
        public void onGameEnded() {
            String event = "<게임 종료> (승자: 플레이어 " + gm.getWinner() + ")";
            logBuilder.append(event).append("\n");
            gameEvents.add(event);
            flush();
        }

        @Override
        public void onTurnChanged() {
            // 필요시 구현 가능
        }

        public void turnChange(int turn) {
            String event = "<턴 " + turn + ">: 플레이어 " + gm.getCurrentPlayer() +
                    " (윷 개수: " + gm.getCurrentYutCount() + ")";
            logBuilder.append(event).append("\n");
            gameEvents.add(event);

            appendPlayerStates();
            checkFlush();
        }

        @Override
        public void onYutStateChanged() {
            // 필요시 구현 가능
        }

        @Override
        public void onPieceMoved() {
            String event = "  [플레이어 " + gm.getCurrentPlayer() +
                    "] 말을 이동 (윷 개수: " + gm.getCurrentYutCount() + ")";
            logBuilder.append(event).append("\n");
            gameEvents.add(event);

            appendPlayerStates();
            checkFlush();
        }

        public void writeYutLog(Yut yut) {
            String event = "  [플레이어 " + gm.getCurrentPlayer() + "] 윷 던짐: " +
                    yut.name() + " (윷 개수: " + gm.getCurrentYutCount() + ")";
            logBuilder.append(event).append("\n");
            gameEvents.add(event);

            logBuilder.append("             ");
            for (Yut y : Yut.values()) {
                logBuilder.append(y.name()).append(": ")
                        .append(gm.getYutResult().get(y.ordinal())).append(' ');
            }
            logBuilder.append('\n');

            checkFlush();
        }

        public void writeException(Exception e) {
            String event = "<예외>: " + e.getMessage();
            logBuilder.append(event).append('\n');
            gameEvents.add(event);
            flush();
        }

        public void writeStart(int attempt) {
            String event = "<게임 시작> (시도 " + attempt + ")";
            logBuilder.append(event).append("\n");
            gameEvents.add(event);

            logBuilder.append("    보드 크기: ")
                    .append(((RegularBoard)(gm.getBoard())).getBoardAngle())
                    .append(", 플레이어: ").append(gm.getPlayerCount())
                    .append(", 말 개수: ").append(gm.getPieceCount()).append('\n');

            checkFlush();
        }

        private void appendPlayerStates() {
            for (int i = 0; i < gm.getPlayerCount(); i++) {
                logBuilder.append("    [플레이어 ").append(i)
                        .append("의 말] 대기: ").append(gm.getReadyPiecesCount(i))
                        .append(", 활성: {");
                for (Piece p : gm.getActivePieces(i)) {
                    logBuilder.append(gm.getBoard().getSpaces().indexOf(p.getLocation())).append(", ");
                }
                logBuilder.append("}, 골인: ").append(gm.getGoalPiecesCount(i)).append('\n');
            }
        }

        void checkFlush() {
            if (logBuilder.length() > 4096) {
                flush();
            }
        }

        void flush() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile.toFile(), true))) {
                writer.write(logBuilder.toString());
                logBuilder.setLength(0);
            } catch (IOException e) {
                System.err.println("로그 쓰기 실패: " + e.getMessage());
            }
        }

        public List<String> getGameEvents() {
            return new ArrayList<>(gameEvents);
        }

        public void clearEvents() {
            gameEvents.clear();
            logBuilder.setLength(0);
        }
    }

    @BeforeEach
    @DisplayName("각 테스트 전 초기화")
    void setUp() throws IOException {
        gameManager = new GameManager(4, 2, 2);
        Path logFile = tempDir.resolve("test_log.txt");
        Files.createFile(logFile);
        testObserver = new TestObserver(gameManager, logFile);
        gameManager.registerObserver(testObserver);
    }

    @AfterEach
    @DisplayName("각 테스트 후 정리")
    void tearDown() {
        if (gameManager != null) {
            gameManager.resetGame();
        }
    }


    @Test
    @Order(1)
    @DisplayName("기본 설정으로 게임 시작 테스트")
    void testGameStart() {
        gameManager.startGame();
        testObserver.writeStart(1);
        testObserver.turnChange(1);

        assertEquals(GameState.PLAYING, gameManager.getState(), "게임 시작 후 상태는 진행중이어야 함");
        assertTrue(testObserver.getGameEvents().size() > 0, "게임 이벤트가 기록되어야 함");
        assertTrue(testObserver.getGameEvents().get(0).contains("게임 시작"),
                "첫 번째 이벤트는 게임 시작이어야 함");
    }

    @Test
    @Order(2)
    @DisplayName("윷 던지기 메커니즘 테스트")
    void testYutThrowing() {
        gameManager.startGame();
        int initialYutCount = gameManager.getCurrentYutCount();

        Yut thrownYut = throwRandomYut();

        assertNotNull(thrownYut, "던진 윷은 null이 아니어야 함");
        assertTrue(Arrays.asList(Yut.values()).contains(thrownYut),
                "던진 윷은 유효한 윷 값이어야 함");

        List<String> events = testObserver.getGameEvents();
        assertTrue(events.stream().anyMatch(event -> event.contains("윷 던짐")),
                "윷 던지기가 로그에 기록되어야 함");
    }

    @Test
    @Order(3)
    @DisplayName("새 말 이동 가능성 확인 테스트")
    void testCanMoveNewPiece() {
        gameManager.startGame();

        // 윷을 던져서 이동 가능한 상황 만들기
        throwRandomYut();

        boolean canMove = canMoveNewPiece();

        if (gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer()) > 0) {
            // 대기 중인 말이 있으면 상황에 따라 이동 가능해야 함
            if (gameManager.getCurrentMoveCount() != gameManager.getYutResult().getFirst()) {
                assertTrue(canMove, "조건이 맞으면 새 말을 이동할 수 있어야 함");
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("새 말 이동 테스트")
    void testMoveNewPiece() {
        gameManager.startGame();

        // 윷을 던져서 이동 가능한 상황 만들기
        if (gameManager.getCurrentYutCount() == 0) {
            throwRandomYut();
        }

        int initialReadyCount = gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer());

        if (initialReadyCount > 0 && canMoveNewPiece()) {
            assertDoesNotThrow(() -> {
                moveRandomNewPiece();
            }, "새 말 이동은 예외를 발생시키지 않아야 함");

            List<String> events = testObserver.getGameEvents();
            assertTrue(events.stream().anyMatch(event -> event.contains("말을 이동")),
                    "말 이동이 로그에 기록되어야 함");
        }
    }

    @Test
    @Order(5)
    @DisplayName("활성 말 이동 테스트")
    void testMoveActivePiece() {
        gameManager.startGame();

        // 활성 말이 있는 상황 설정
        setupGameWithActivePieces();

        if (!gameManager.getActivePieces(gameManager.getCurrentPlayer()).isEmpty() &&
                gameManager.getCurrentMoveCount() > 0) {

            assertDoesNotThrow(() -> {
                moveRandomActivePiece();
            }, "활성 말 이동은 예외를 발생시키지 않아야 함");

            List<String> events = testObserver.getGameEvents();
            assertTrue(events.stream().anyMatch(event -> event.contains("말을 이동")),
                    "활성 말 이동이 로그에 기록되어야 함");
        }
    }

    @Test
    @Order(6)
    @DisplayName("게임 액션 수행 테스트")
    void testPerformGameAction() {
        gameManager.startGame();

        // 윷을 던져서 액션 가능한 상황 만들기
        throwRandomYut();

        assertDoesNotThrow(() -> {
            performGameAction();
        }, "게임 액션 수행은 예외를 발생시키지 않아야 함");
    }

    @Test
    @Order(7)
    @DisplayName("무효한 이동에 대한 오류 처리 테스트")
    void testErrorHandlingForInvalidMoves() {
        gameManager.startGame();

        // 이동할 수 없는 상황에서 말 이동 시도
        if (gameManager.getCurrentMoveCount() == 0 && gameManager.getCurrentYutCount() == 0) {
            Exception exception = assertThrows(Exception.class, () -> {
                moveRandomNewPiece();
            }, "이동 불가능한 상황에서는 예외가 발생해야 함");

            assertNotNull(exception.getMessage(), "예외 메시지가 존재해야 함");
        }
    }

    @Test
    @Order(8)
    @DisplayName("완전한 게임 시뮬레이션 테스트")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testCompleteGameSimulation() {
        assertDoesNotThrow(() -> {
            simulateCompleteGame(1);
        }, "완전한 게임 시뮬레이션은 예외를 발생시키지 않아야 함");

        // 게임이 제대로 종료되었는지 확인
        List<String> events = testObserver.getGameEvents();
        assertTrue(events.stream().anyMatch(event -> event.contains("게임 종료")),
                "게임이 정상적으로 종료되어야 함");
    }

    @RepeatedTest(5)
    @DisplayName("반복적인 게임 시뮬레이션 테스트")
    void testRepeatedGameSimulation(RepetitionInfo repetitionInfo) {
        int currentRepetition = repetitionInfo.getCurrentRepetition();

        assertDoesNotThrow(() -> {
            testObserver.clearEvents();
            simulateCompleteGame(currentRepetition);
        }, "반복 " + currentRepetition + "번째 게임이 성공적으로 완료되어야 함");

        gameManager.resetGame();
    }

    @Test
    @Order(9)
    @DisplayName("여러 게임 시뮬레이션 통계 테스트")
    void testMultipleGameSimulationStatistics() {
        int successfulGames = 0;
        int totalAttempts = 10;
        List<Exception> exceptions = new ArrayList<>();

        for (int i = 1; i <= totalAttempts; i++) {
            try {
                testObserver.clearEvents();
                simulateCompleteGame(i);
                successfulGames++;
            } catch (Exception e) {
                testObserver.writeException(e);
                exceptions.add(e);
                System.err.println("게임 " + i + " 실패: " + e.getMessage());
            } finally {
                gameManager.resetGame();
            }
        }

        assertTrue(successfulGames > 0,
                "최소 하나의 게임은 성공적으로 완료되어야 함");

        double successRate = (double) successfulGames / totalAttempts * 100;
        System.out.println("성공한 게임: " + successfulGames + "/" + totalAttempts +
                " (" + String.format("%.1f", successRate) + "%)");

        // 성공률이 50% 이상이어야 함
        assertTrue(successRate >= 50.0,
                "게임 성공률이 50% 이상이어야 함. 현재: " + String.format("%.1f", successRate) + "%");
    }

    @Test
    @Order(10)
    @DisplayName("옵저버 이벤트 기록 테스트")
    void testObserverEventRecording() {
        gameManager.startGame();
        testObserver.writeStart(1);

        assertTrue(testObserver.getGameEvents().size() > 0,
                "옵저버가 이벤트를 기록해야 함");

        testObserver.clearEvents();
        assertEquals(0, testObserver.getGameEvents().size(),
                "이벤트 클리어 후 이벤트 목록이 비어야 함");
    }

    // 헬퍼 메서드들 (원본 static 메서드에서 변환)

    private Yut throwRandomYut() {
        Yut yut = gameManager.throwRandomYut();
        testObserver.writeYutLog(yut);
        return yut;
    }

    private void moveRandomNewPiece() throws Exception {
        List<BoardSpace> moveList = gameManager.getPossibleLocationsOfNewPiece();

        List<Yut> canYuts = new ArrayList<>();
        for (Yut y : Yut.values()) {
            if (moveList.get(y.ordinal()) != null) {
                canYuts.add(y);
            }
        }

        if (canYuts.isEmpty()) {
            throw new Exception("새 말을 이동할 수 있는 유효한 위치가 없음");
        }

        int index = random.nextInt(canYuts.size());
        gameManager.moveNewPiece(canYuts.get(index));
    }

    private void moveRandomActivePiece() throws Exception {
        List<Piece> pieces = new ArrayList<>(gameManager.getActivePieces(gameManager.getCurrentPlayer()));

        if (pieces.isEmpty()) {
            throw new Exception("이동할 활성 말이 없음");
        }

        Piece piece = pieces.get(random.nextInt(pieces.size()));
        List<BoardSpace> moveList = gameManager.getPossibleLocations(piece);

        List<Yut> canYuts = new ArrayList<>();
        for (Yut y : Yut.values()) {
            if (moveList.get(y.ordinal()) != null) {
                canYuts.add(y);
            }
        }

        if (canYuts.isEmpty()) {
            throw new Exception("활성 말을 이동할 수 있는 유효한 위치가 없음");
        }

        int index = random.nextInt(canYuts.size());
        gameManager.movePiece(piece, canYuts.get(index));
    }

    private boolean canMoveNewPiece() {
        boolean haveReadyPieces = gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer()) > 0;
        return haveReadyPieces && gameManager.getCurrentMoveCount() != gameManager.getYutResult().getFirst();
    }

    private void setupGameWithActivePieces() {
        // 활성 말이 있는 게임 상태 설정
        try {
            while (gameManager.getActivePieces(gameManager.getCurrentPlayer()).isEmpty() &&
                    gameManager.getState() == GameState.PLAYING) {
                if (gameManager.getCurrentYutCount() == 0) {
                    throwRandomYut();
                }
                if (canMoveNewPiece()) {
                    moveRandomNewPiece();
                }
            }
        } catch (Exception e) {
            System.err.println("게임 설정 실패: " + e.getMessage());
        }
    }

    private void simulateCompleteGame(int attemptNumber) throws Exception {
        // 랜덤 게임 매개변수 설정
        gameManager.setBoard(random.nextInt(4, 6));
        gameManager.setPlayerCount(random.nextInt(2, 5));
        gameManager.setPieceCount(random.nextInt(2, 6));

        gameManager.startGame();
        testObserver.writeStart(attemptNumber);
        testObserver.turnChange(1);

        int currentTurn = 0;
        int turnCount = 1;
        int maxTurns = 1000; // 무한 루프 방지
        int turnCounter = 0;

        while (gameManager.getState() == GameState.PLAYING && turnCounter < maxTurns) {
            turnCounter++;

            if (currentTurn != gameManager.getCurrentPlayer()) {
                turnCount++;
                testObserver.turnChange(turnCount);
                currentTurn = gameManager.getCurrentPlayer();
            }

            performGameAction();
        }

        if (turnCounter >= maxTurns) {
            throw new Exception("게임이 최대 턴 수를 초과함");
        }
    }

    private void performGameAction() throws Exception {
        if (gameManager.getCurrentYutCount() > 0 && gameManager.getCurrentMoveCount() > 0) {
            // 윷을 던지거나 말을 이동할 수 있음
            switch (random.nextInt(2)) {
                case 0:
                    throwRandomYut();
                    break;
                case 1:
                    performPieceMove();
                    break;
                default:
                    throw new Exception("난수 오류");
            }
        } else if (gameManager.getCurrentYutCount() > 0) {
            // 윷만 던질 수 있음
            throwRandomYut();
        } else if (gameManager.getCurrentMoveCount() > 0) {
            // 말만 이동할 수 있음
            performPieceMove();
        } else {
            throw new Exception("윷도 못 던지고 움직이지도 못하는 상태에 도달");
        }
    }

    private void performPieceMove() throws Exception {
        boolean hasReadyPieces = gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer()) > 0;
        boolean hasActivePieces = !gameManager.getActivePieces(gameManager.getCurrentPlayer()).isEmpty();
        boolean canMoveNew = hasReadyPieces && gameManager.getCurrentMoveCount() != gameManager.getYutResult().getFirst();

        if (canMoveNew && hasActivePieces) {
            // 새 말이나 활성 말 둘 다 이동 가능
            switch (random.nextInt(2)) {
                case 0:
                    moveRandomNewPiece();
                    break;
                case 1:
                    moveRandomActivePiece();
                    break;
                default:
                    throw new Exception("난수 오류");
            }
        } else if (canMoveNew) {
            moveRandomNewPiece();
        } else if (hasActivePieces) {
            moveRandomActivePiece();
        } else {
            throw new Exception("이동할 수 있는 말이 없음 - 유효하지 않은 게임 상태");
        }
    }
}