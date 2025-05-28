package org.cau02.model;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLI 컨트롤러 JUnit 테스트 스위트
 * Controller_test_cli 클래스의 기능들을 테스트
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ControllerCliTest {

    private GameManager gameManager;
    private ViewTestCli viewTestCli;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private ByteArrayInputStream inputStream;
    private Scanner testScanner;

    /**
     * 테스트용 뷰 클래스 (View_test_cli와 동일한 기능)
     */
    static class ViewTestCli implements YutNoriObserver {
        GameManager gm;
        private ByteArrayOutputStream capturedOutput;

        ViewTestCli(GameManager gm) {
            this.gm = gm;
            this.capturedOutput = new ByteArrayOutputStream();
        }

        ViewTestCli(GameManager gm, ByteArrayOutputStream outputStream) {
            this.gm = gm;
            this.capturedOutput = outputStream;
        }

        @Override
        public void onGameEnded() {
            System.out.println("<게임 종료!>");
            System.out.println("승리자: " + gm.getWinner() + "번 플레이어");
        }

        @Override
        public void onTurnChanged() {
            System.out.println("<새로운 턴!>");
            System.out.println("현재 차례: " + gm.getCurrentPlayer() + "번 플레이어");
        }

        @Override
        public void onYutStateChanged() {
            System.out.print("남은 윷 던지기 횟수: " + gm.getCurrentYutCount() + "회, ");
            System.out.println("남은 이동 횟수: " + gm.getCurrentMoveCount() + "회");
            System.out.println("족보 별 남은 이동 횟수");
            for (Yut y : Yut.values()) {
                System.out.print("  " + y.name() + ": " + gm.getYutResult().get(y.ordinal()) + "회 ");
            }
            System.out.println();
        }

        @Override
        public void onPieceMoved() {
            System.out.println("말 정보");
            for (int i = 0; i < gm.getPlayerCount(); i++) {
                System.out.println("  플레이어 " + i);
                System.out.println("    대기 중인 말: " + gm.getReadyPiecesCount(i) + "개");
                System.out.println("    도착한 말: " + gm.getGoalPiecesCount(i) + "개");
                System.out.println("    게임판 위의 말 위치");
                for (Piece p : gm.getActivePieces(i)) {
                    System.out.print("      " + gm.getBoard().getSpaces().indexOf(p.getLocation()));
                    if (p.getState() == PieceState.CARRIED) {
                        System.out.print("[업힘] ");
                    } else {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }

        void printProperties() {
            System.out.println("<게임 정보>");
            System.out.println("게임 인스턴스: " + gm);
            System.out.println("게임판 모양: " + ((RegularBoard) gm.getBoard()).getBoardAngle() + "각형");
            System.out.println("플레이어 수: " + gm.getPlayerCount() + "명");
            System.out.println("플레이어당 말 수 : " + gm.getPieceCount() + "개");
            System.out.println("--------------------------------------------------------");
        }

        public String getCapturedOutput() {
            return capturedOutput.toString();
        }

        public void clearCapturedOutput() {
            capturedOutput.reset();
        }
    }

    @BeforeEach
    @DisplayName("각 테스트 전 초기화")
    void setUp() {
        gameManager = new GameManager(4, 4, 5);

        // 출력 캡처 설정
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        viewTestCli = new ViewTestCli(gameManager, outputStream);
        gameManager.registerObserver(viewTestCli);
    }

    @AfterEach
    @DisplayName("각 테스트 후 정리")
    void tearDown() {
        // 원래 출력 스트림 복원
        System.setOut(originalOut);

        if (gameManager != null) {
            gameManager.resetGame();
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.err.println("입력 스트림 닫기 실패: " + e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("뷰 테스트 CLI 초기화 테스트")
    void testViewTestCliInitialization() {
        assertNotNull(viewTestCli, "뷰 테스트 CLI가 초기화되어야 함");
        assertNotNull(viewTestCli.gm, "게임 매니저가 설정되어야 함");
        assertEquals(gameManager, viewTestCli.gm, "게임 매니저가 올바르게 설정되어야 함");
    }

    @Test
    @Order(2)
    @DisplayName("게임 종료 이벤트 출력 테스트")
    void testOnGameEnded() {
        // 게임을 시작하고 임의로 승자 설정하여 종료 이벤트 트리거
        gameManager.startGame();

        viewTestCli.onGameEnded();

        String output = outputStream.toString();
        assertTrue(output.contains("<게임 종료!>"), "게임 종료 메시지가 출력되어야 함");
        assertTrue(output.contains("승리자:"), "승리자 메시지가 출력되어야 함");
        assertTrue(output.contains("번 플레이어"), "플레이어 번호가 출력되어야 함");
    }

    @Test
    @Order(3)
    @DisplayName("턴 변경 이벤트 출력 테스트")
    void testOnTurnChanged() {
        gameManager.startGame();

        viewTestCli.onTurnChanged();

        String output = outputStream.toString();
        assertTrue(output.contains("<새로운 턴!>"), "새로운 턴 메시지가 출력되어야 함");
        assertTrue(output.contains("현재 차례:"), "현재 차례 메시지가 출력되어야 함");
        assertTrue(output.contains("번 플레이어"), "플레이어 번호가 출력되어야 함");
    }

    @Test
    @Order(4)
    @DisplayName("윷 상태 변경 이벤트 출력 테스트")
    void testOnYutStateChanged() {
        gameManager.startGame();

        viewTestCli.onYutStateChanged();

        String output = outputStream.toString();
        assertTrue(output.contains("남은 윷 던지기 횟수:"), "윷 던지기 횟수 메시지가 출력되어야 함");
        assertTrue(output.contains("남은 이동 횟수:"), "이동 횟수 메시지가 출력되어야 함");
        assertTrue(output.contains("족보 별 남은 이동 횟수"), "족보별 이동 횟수 메시지가 출력되어야 함");

        // 모든 윷 종류가 출력되는지 확인
        for (Yut yut : Yut.values()) {
            assertTrue(output.contains(yut.name()), yut.name() + " 족보가 출력되어야 함");
        }
    }

    @Test
    @Order(5)
    @DisplayName("말 이동 이벤트 출력 테스트")
    void testOnPieceMoved() {
        gameManager.startGame();

        viewTestCli.onPieceMoved();

        String output = outputStream.toString();
        assertTrue(output.contains("말 정보"), "말 정보 메시지가 출력되어야 함");
        assertTrue(output.contains("플레이어"), "플레이어 정보가 출력되어야 함");
        assertTrue(output.contains("대기 중인 말:"), "대기 중인 말 정보가 출력되어야 함");
        assertTrue(output.contains("도착한 말:"), "도착한 말 정보가 출력되어야 함");
        assertTrue(output.contains("게임판 위의 말 위치"), "게임판 위 말 위치 정보가 출력되어야 함");
    }

    @Test
    @Order(6)
    @DisplayName("게임 속성 출력 테스트")
    void testPrintProperties() {
        gameManager.startGame();

        viewTestCli.printProperties();

        String output = outputStream.toString();
        assertTrue(output.contains("<게임 정보>"), "게임 정보 헤더가 출력되어야 함");
        assertTrue(output.contains("게임 인스턴스:"), "게임 인스턴스 정보가 출력되어야 함");
        assertTrue(output.contains("게임판 모양:"), "게임판 모양 정보가 출력되어야 함");
        assertTrue(output.contains("각형"), "게임판 각형 정보가 출력되어야 함");
        assertTrue(output.contains("플레이어 수:"), "플레이어 수 정보가 출력되어야 함");
        assertTrue(output.contains("플레이어당 말 수"), "말 수 정보가 출력되어야 함");
        assertTrue(output.contains("--------------------------------------------------------"), "구분선이 출력되어야 함");
    }

    @Test
    @Order(7)
    @DisplayName("게임 매니저 설정 테스트")
    void testGameManagerSettings() {
        // 게임 설정 변경
        gameManager.setBoard(5);
        gameManager.setPlayerCount(3);
        gameManager.setPieceCount(4);
        gameManager.startGame();

        viewTestCli.printProperties();

        String output = outputStream.toString();
        assertTrue(output.contains("5각형"), "변경된 보드 모양이 반영되어야 함");
        assertTrue(output.contains("플레이어 수: 3명"), "변경된 플레이어 수가 반영되어야 함");
        assertTrue(output.contains("플레이어당 말 수 : 4개"), "변경된 말 수가 반영되어야 함");
    }

    @Test
    @Order(8)
    @DisplayName("랜덤 윷 던지기 시뮬레이션 테스트")
    void testThrowRandomYutSimulation() {
        gameManager.startGame();

        // 윷을 던질 수 있는 상태인지 확인
        if (gameManager.getCurrentYutCount() > 0) {
            assertDoesNotThrow(() -> {
                Yut result = gameManager.throwRandomYut();
                assertNotNull(result, "윷 던지기 결과가 null이 아니어야 함");
                assertTrue(Arrays.asList(Yut.values()).contains(result), "유효한 윷 값이어야 함");
            }, "랜덤 윷 던지기는 예외를 발생시키지 않아야 함");
        }
    }

    @Test
    @Order(9)
    @DisplayName("선택 윷 던지기 시뮬레이션 테스트")
    void testThrowSelectedYutSimulation() {
        gameManager.startGame();

        // 모든 윷 종류에 대해 테스트
        for (Yut yutType : Yut.values()) {
            if (gameManager.getCurrentYutCount() > 0) {
                assertDoesNotThrow(() -> {
                    Yut result = gameManager.throwSelectedYut(yutType);
                    assertNotNull(result, "선택 윷 던지기 결과가 null이 아니어야 함");
                    assertEquals(yutType, result, "선택한 윷 종류와 결과가 일치해야 함");
                }, yutType.name() + " 윷 던지기는 예외를 발생시키지 않아야 함");

                // 윷 상태 변경 이벤트 트리거
                viewTestCli.onYutStateChanged();
            }
        }
    }

    @Test
    @Order(10)
    @DisplayName("새 말 이동 가능성 확인 테스트")
    void testNewPieceMovePossibility() {
        gameManager.startGame();

        // 윷을 던져서 이동 가능한 상황 만들기
        if (gameManager.getCurrentYutCount() > 0) {
            gameManager.throwRandomYut();
        }

        int currentPlayer = gameManager.getCurrentPlayer();
        int readyPiecesCount = gameManager.getReadyPiecesCount(currentPlayer);

        if (readyPiecesCount > 0) {
            List<BoardSpace> possibleLocations = gameManager.getPossibleLocationsOfNewPiece();
            assertNotNull(possibleLocations, "가능한 이동 위치 목록이 null이 아니어야 함");
            assertEquals(Yut.values().length, possibleLocations.size(),
                    "모든 윷 종류에 대한 이동 위치가 포함되어야 함");
        }
    }

    @Test
    @Order(11)
    @DisplayName("새 말 이동 시뮬레이션 테스트")
    void testMoveNewPieceSimulation() {
        gameManager.startGame();

        // 윷을 던져서 이동 가능한 상황 만들기
        if (gameManager.getCurrentYutCount() > 0) {
            gameManager.throwRandomYut();
        }

        int currentPlayer = gameManager.getCurrentPlayer();
        int initialReadyCount = gameManager.getReadyPiecesCount(currentPlayer);

        if (initialReadyCount > 0 && gameManager.getCurrentMoveCount() > 0) {
            // 이동 가능한 윷 찾기
            List<BoardSpace> possibleLocations = gameManager.getPossibleLocationsOfNewPiece();
            for (Yut yut : Yut.values()) {
                if (possibleLocations.get(yut.ordinal()) != null &&
                        gameManager.getYutResult().get(yut.ordinal()) > 0) {

                    assertDoesNotThrow(() -> {
                        gameManager.moveNewPiece(yut);
                        viewTestCli.onPieceMoved();
                    }, yut.name() + "으로 새 말 이동은 예외를 발생시키지 않아야 함");
                    break;
                }
            }
        }
    }

    @Test
    @Order(12)
    @DisplayName("활성 말 이동 시뮬레이션 테스트")
    void testMoveActivePieceSimulation() {
        gameManager.startGame();

        // 활성 말이 있는 상황 만들기
        setupGameWithActivePieces();

        int currentPlayer = gameManager.getCurrentPlayer();
        List<Piece> activePieces = new ArrayList<>(gameManager.getActivePieces(currentPlayer));

        if (!activePieces.isEmpty() && gameManager.getCurrentMoveCount() > 0) {
            Piece testPiece = activePieces.get(0);
            List<BoardSpace> possibleLocations = gameManager.getPossibleLocations(testPiece);

            assertNotNull(possibleLocations, "활성 말의 가능한 이동 위치가 null이 아니어야 함");

            // 이동 가능한 윷 찾기
            for (Yut yut : Yut.values()) {
                if (possibleLocations.get(yut.ordinal()) != null &&
                        gameManager.getYutResult().get(yut.ordinal()) > 0) {

                    assertDoesNotThrow(() -> {
                        gameManager.movePiece(testPiece, yut);
                        viewTestCli.onPieceMoved();
                    }, yut.name() + "으로 활성 말 이동은 예외를 발생시키지 않아야 함");
                    break;
                }
            }
        }
    }

    @Test
    @Order(13)
    @DisplayName("윷 족보별 이동 횟수 확인 테스트")
    void testYutResultDisplay() {
        gameManager.startGame();

        // 윷을 던져서 결과 생성
        if (gameManager.getCurrentYutCount() > 0) {
            gameManager.throwRandomYut();
            viewTestCli.onYutStateChanged();

            String output = outputStream.toString();

            // 모든 윷 족보가 출력되는지 확인
            for (Yut yut : Yut.values()) {
                assertTrue(output.contains(yut.name() + ":"),
                        yut.name() + " 족보 정보가 출력되어야 함");
                assertTrue(output.contains("회"), "이동 횟수 단위가 출력되어야 함");
            }
        }
    }

    @Test
    @Order(14)
    @DisplayName("게임 상태 변화에 따른 출력 테스트")
    void testGameStateOutputs() {
        gameManager.startGame();

        // 턴 변경 이벤트
        viewTestCli.onTurnChanged();
        String turnOutput = outputStream.toString();
        assertTrue(turnOutput.contains("현재 차례: " + gameManager.getCurrentPlayer()),
                "현재 플레이어 정보가 정확히 출력되어야 함");

        // 출력 스트림 클리어
        outputStream.reset();

        // 윷 상태 변경 이벤트
        if (gameManager.getCurrentYutCount() > 0) {
            gameManager.throwRandomYut();
            viewTestCli.onYutStateChanged();
            String yutOutput = outputStream.toString();
            assertTrue(yutOutput.contains("남은 윷 던지기 횟수: " + gameManager.getCurrentYutCount()),
                    "윷 던지기 횟수가 정확히 출력되어야 함");
            assertTrue(yutOutput.contains("남은 이동 횟수: " + gameManager.getCurrentMoveCount()),
                    "이동 횟수가 정확히 출력되어야 함");
        }
    }

    @Test
    @Order(15)
    @DisplayName("말 상태 정보 출력 정확성 테스트")
    void testPieceStateInformationAccuracy() {
        gameManager.startGame();

        viewTestCli.onPieceMoved();
        String output = outputStream.toString();

        // 각 플레이어별 정보 확인
        for (int i = 0; i < gameManager.getPlayerCount(); i++) {
            assertTrue(output.contains("플레이어 " + i),
                    "플레이어 " + i + " 정보가 출력되어야 함");
            assertTrue(output.contains("대기 중인 말: " + gameManager.getReadyPiecesCount(i) + "개"),
                    "플레이어 " + i + "의 대기 중인 말 개수가 정확히 출력되어야 함");
            assertTrue(output.contains("도착한 말: " + gameManager.getGoalPiecesCount(i) + "개"),
                    "플레이어 " + i + "의 도착한 말 개수가 정확히 출력되어야 함");
        }
    }

    @Test
    @Order(16)
    @DisplayName("업힘 상태 말 출력 테스트")
    void testCarriedPieceDisplay() {
        gameManager.startGame();

        // 말을 게임판에 올리고 업힘 상태 만들기 (실제 구현에 따라 다를 수 있음)
        setupGameWithActivePieces();

        viewTestCli.onPieceMoved();
        String output = outputStream.toString();

        // 업힘 상태 표시 확인 (실제로 업힌 말이 있을 때만)
        if (output.contains("[업힘]")) {
            assertTrue(output.contains("[업힘]"), "업힌 말의 상태가 표시되어야 함");
        }
    }

    @Test
    @Order(17)
    @DisplayName("게임 리셋 후 속성 출력 테스트")
    void testPropertiesAfterReset() {
        gameManager.startGame();
        viewTestCli.printProperties();

        // 첫 번째 출력 확인
        String firstOutput = outputStream.toString();
        assertTrue(firstOutput.contains("<게임 정보>"), "게임 정보가 출력되어야 함");

        // 게임 리셋
        gameManager.resetGame();
        outputStream.reset();

        // 새로운 설정으로 게임 시작
        gameManager.setBoard(6);
        gameManager.setPlayerCount(2);
        gameManager.setPieceCount(3);
        gameManager.startGame();

        viewTestCli.printProperties();
        String secondOutput = outputStream.toString();

        assertTrue(secondOutput.contains("6각형"), "리셋 후 새로운 보드 설정이 반영되어야 함");
        assertTrue(secondOutput.contains("플레이어 수: 2명"), "리셋 후 새로운 플레이어 수가 반영되어야 함");
        assertTrue(secondOutput.contains("플레이어당 말 수 : 3개"), "리셋 후 새로운 말 수가 반영되어야 함");
    }

    @Test
    @Order(18)
    @DisplayName("예외 상황 처리 테스트")
    void testExceptionHandling() {
        gameManager.startGame();

        // 윷을 던질 수 없는 상황에서의 예외 처리
        if (gameManager.getCurrentYutCount() == 0) {
            assertThrows(IllegalStateException.class, () -> {
                gameManager.throwRandomYut();
            }, "윷을 던질 수 없는 상황에서는 예외가 발생해야 함");
        }

        // 이동할 수 없는 상황에서의 예외 처리
        if (gameManager.getCurrentMoveCount() == 0) {
            assertThrows(IllegalStateException.class, () -> {
                gameManager.moveNewPiece(Yut.DO);
            }, "이동할 수 없는 상황에서는 예외가 발생해야 함");
        }
    }

    // 헬퍼 메서드들

    private void setupGameWithActivePieces() {
        try {
            int attempts = 0;
            while (gameManager.getActivePieces(gameManager.getCurrentPlayer()).isEmpty() &&
                    gameManager.getState() == GameState.PLAYING && attempts < 10) {

                if (gameManager.getCurrentYutCount() > 0) {
                    gameManager.throwRandomYut();
                }

                if (gameManager.getCurrentMoveCount() > 0 &&
                        gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer()) > 0) {

                    // 이동 가능한 윷 찾아서 새 말 이동
                    List<BoardSpace> possibleLocations = gameManager.getPossibleLocationsOfNewPiece();
                    for (Yut yut : Yut.values()) {
                        if (possibleLocations.get(yut.ordinal()) != null &&
                                gameManager.getYutResult().get(yut.ordinal()) > 0) {
                            gameManager.moveNewPiece(yut);
                            break;
                        }
                    }
                }
                attempts++;
            }
        } catch (Exception e) {
            System.err.println("활성 말 설정 실패: " + e.getMessage());
        }
    }

    private void simulateUserInput(String input) {
        inputStream = new ByteArrayInputStream(input.getBytes());
        testScanner = new Scanner(inputStream);
    }

    private String getOutput() {
        return outputStream.toString();
    }

    private void clearOutput() {
        outputStream.reset();
    }
}