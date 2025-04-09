package org.cau02.model;

import java.util.*;
import java.util.stream.Collectors;

/** 말의 이동 계산 및 실행 로직 담당 */
public class MovementService {

    private final Board board;
    private final List<Player> players; // 게임 내 모든 플레이어 정보 접근

    public MovementService(Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board);
        this.players = Objects.requireNonNull(players);
    }

    /**
     * 특정 말과 윷 결과에 대해 가능한 모든 이동(Move 객체) 목록 계산
     */
    public List<Move> calculateValidMoves(Piece piece, YutResult result) {
        List<Move> possibleMoves = new ArrayList<>();
        if (piece.isFinished()) return possibleMoves; // 완주한 말 이동 불가

        // TODO: 구현 필요
        return possibleMoves;
    }

    /**
     * 선택된 이동(Move)을 실제 게임 상태에 반영
     */
    public void executeMove(Move move) {
        if (move == null) return;

        Piece movingPiece = move.getPieceToMove();
        Player owner = movingPiece.getOwner();

        // TODO: 구현 필요

        // 3. 이동한 플레이어 점수 업데이트 (완주 시 점수 반영됨)
        owner.updateScore();
    }

    // --- Helper Methods ---

    /**
     * 지정된 노드에서 지정된 칸 수만큼 이동한 후의 도착 노드를 반환합니다.
     * (지름길, 교차로, 백도 고려 필요 - 현재는 단순 외곽 경로 이동)
     */
    private PathNode traversePath(PathNode start, int steps) {
        // TODO: 실제 윷판 경로 탐색 로직 상세 구현 필요
        PathNode current = start;
        if (steps == 0) return start; // 이동 없음
        // TODO: 구현 필요
        return current;
    }

    /** 특정 노드에 위치한 모든 (완주 안 한) 말 찾기 */
    private List<Piece> findPiecesAt(PathNode node) {
        if (node == null) return Collections.emptyList();
        return players.stream()
                .flatMap(player -> player.getPieces().stream())
                .filter(p -> !p.isFinished() && node.equals(p.getPosition()))
                .collect(Collectors.toList());
    }
}
