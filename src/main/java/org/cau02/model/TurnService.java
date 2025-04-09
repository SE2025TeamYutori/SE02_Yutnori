package org.cau02.model;

import java.util.List;
import java.util.Objects;

/** 게임 턴 관리 로직 담당 */
public class TurnService {

    /** 다음 플레이어로 턴을 넘김 */
    public void advanceTurn(Game game) {
        Objects.requireNonNull(game);
        if (game.getStatus() != GameStatus.IN_PROGRESS) return;

        List<Player> players = game.getPlayers();
        if (players.isEmpty()) return; // 플레이어 없으면 진행 불가

        int currentPlayerIndex = players.indexOf(game.getCurrentPlayer());
        // 다음 플레이어 인덱스 계산 (순환)
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // Game 내부 상태 변경 메서드 호출 (protected 접근)
        game.setCurrentPlayerInternal(players.get(nextPlayerIndex));
        game.incrementTurnCountInternal();
    }

    /** 보너스 턴 조건 확인 (윷/모 또는 말 잡기) */
    public boolean shouldGetBonusTurn(YutResult result, Move lastMove) {
        // 1. 윷 또는 모를 던졌을 경우
        if (result != null && result.isBonus()) {
            return true;
        }
        // 2. 상대방 말을 잡았을 경우
        if (lastMove != null && !lastMove.getCapturedPieces().isEmpty()) {
            // TODO: 상세 규칙 확인 (예: 백도로 잡으면 보너스 없는지 등)
            return true;
        }
        return false;
    }
}