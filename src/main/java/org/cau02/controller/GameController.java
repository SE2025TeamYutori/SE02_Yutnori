package org.cau02.controller;

import org.cau02.model.old.interfaces.IBoardConfiguration;
import org.cau02.model.old.*;
import org.cau02.view.GameView;
import org.cau02.view.ThrowType;

import java.util.*;

/** 게임 로직과 사용자 인터페이스(View)를 연결하고 게임 흐름을 제어 */
public class GameController {
    private final Game game;       // Model: 게임 상태 및 로직
    private final GameView view;   // View: 사용자 인터페이스

    // 기본 게임 설정값 (프로토타입용)
    private static final int DEFAULT_NUM_PIECES = 4;
    private static final int DEFAULT_BOARD_SIDES = 4; // 4각형
    private static final int DEFAULT_NODES_PER_SIDE = 5; // 변당 노드 수 (모서리 포함 개념)

    /**
     * 게임 컨트롤러를 생성합니다.
     * @param game 게임 모델 객체
     * @param view 게임 뷰 객체
     */
    public GameController(Game game, GameView view) {
        this.game = Objects.requireNonNull(game);
        this.view = Objects.requireNonNull(view);
    }

    /** 게임 시작 및 메인 루프 실행 */
    public void startGame() {
        view.displayWelcome();
        try {
            // 1. 게임 설정 (플레이어, 말 개수, 판 등)
            List<String> playerNames = view.promptPlayerNames();
            // TODO: 사용자에게 말 개수, 판 모양 입력받는 로직 추가 가능
            int numPieces = DEFAULT_NUM_PIECES;
            IBoardConfiguration boardConfig = new GenericBoardConfiguration(
                    DEFAULT_BOARD_SIDES, DEFAULT_NODES_PER_SIDE
            );

            // 2. 게임 모델 초기화
            game.initializeGame(playerNames, numPieces, boardConfig);

            // 3. 게임 메인 루프 실행
            playGameLoop();

        } catch (IllegalArgumentException | IllegalStateException e) {
            view.displayMessage("게임 시작 오류: " + e.getMessage());
        } catch (Exception e) {
            view.displayMessage("예상치 못한 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 디버깅용 스택 트레이스 출력
        } finally {
            view.close(); // 게임 종료 시 View 리소스 정리
        }
    }

    /** 게임 메인 루프 */
    private void playGameLoop() {
        while (game.getStatus() == GameStatus.IN_PROGRESS) {
            Player currentPlayer = game.getCurrentPlayer();
            view.displayTurnStart(game.getTurnCount(), currentPlayer);
            view.displayPlayerStatus(currentPlayer);
            view.displayBoard(game.getBoard(), game.getPlayers());

            boolean turnActionsCompleted = false; // 현재 플레이어의 턴 액션 완료 여부

            // 한 플레이어가 보너스 턴 등으로 여러 번 액션 가능
            while (!turnActionsCompleted && game.getStatus() == GameStatus.IN_PROGRESS) {
                // 4. 윷 던지기 (방식 선택 및 실행)
                YutResult currentResult = performThrowAction(currentPlayer);
                if (currentResult == null) { // 윷 던지기 실패/오류
                    view.displayMessage("윷 던지기 중 오류 발생. 턴을 종료합니다.");
                    turnActionsCompleted = true; // 오류 시 턴 강제 종료
                    continue;
                }

                // 5. 이동할 말 선택 및 실행
                performMoveAction(currentPlayer, currentResult);

                // 6. 게임 종료 조건 확인
                if (game.checkWinCondition()) {
                    view.displayWinner(currentPlayer); // 승리 메시지 표시
                    return; // 게임 루프 즉시 종료
                }

                // 7. 보너스 턴 확인
                // 주의: 마지막 실행된 Move 객체를 보너스 턴 확인에 사용해야 함
                // 현재 구조에서는 performMoveAction 내부에서 Move가 처리되므로,
                // bonus 확인 로직 개선 필요 (예: performMoveAction이 Move 반환)
                // 임시: null 전달하여 윷/모 보너스만 확인
                boolean bonus = game.checkForBonusTurn(currentResult, null);
                if (bonus) {
                    view.displayBonusTurn();
                    // turnActionsCompleted = false 유지 -> 루프 계속 (다음 액션)
                } else {
                    turnActionsCompleted = true; // 보너스 없으면 턴 액션 종료
                }
            } // end of player action loop

            // 8. 다음 턴으로 넘기기 (게임이 아직 진행 중일 때)
            if (game.getStatus() == GameStatus.IN_PROGRESS) {
                game.advanceToNextTurn();
            }
        } // end of game loop
    }

    /** 현재 플레이어의 윷 던지기 액션을 처리하고 결과를 반환 */
    private YutResult performThrowAction(Player currentPlayer) {
        ThrowType throwType = view.promptThrowType(currentPlayer);
        YutResult result;

        if (throwType == ThrowType.RANDOM) {
            view.promptAction("Enter를 눌러 윷을 던지세요");
            result = game.playerThrowsYut();
            if (result != null) {
                view.displayThrowResult(result);
            }
        } else { // throwType == ThrowType.SPECIFIC
            result = view.promptSpecificYutResult();
            // 결과 표시는 promptSpecificYutResult 내부에서 처리
        }
        return result;
    }

    /** 현재 플레이어의 말 이동 액션을 처리 */
    private void performMoveAction(Player currentPlayer, YutResult result) {
        List<Piece> movablePieces = currentPlayer.getMovablePieces();
        List<Move> allPossibleMoves = new ArrayList<>();
        for (Piece piece : movablePieces) {
            allPossibleMoves.addAll(game.getAvailableMovesForPiece(piece, result));
        }

        // Player 객체에게 이동 선택 위임
        Move chosenMove = game.playerChoosesMove(allPossibleMoves, view);

        // 선택된 이동 실행 및 결과 표시
        game.executePlayerMove(chosenMove);
        view.displayMoveAction(chosenMove);

        // 이동 후 보드 상태 다시 표시 (선택 사항)
        if (chosenMove != null) {
            view.displayBoard(game.getBoard(), game.getPlayers());
        }
    }
}
