package org.cau02.model.old;
import java.util.*;

/** 한 번의 말 이동 정보를 담는 객체 */
public class Move {
    private final Piece pieceToMove;      // 움직일 말
    private final PathNode startNode;       // 시작 위치 (집이면 null일 수 있음)
    private final PathNode endNode;         // 최종 도착 위치
    private final List<Piece> capturedPieces; // 이 이동으로 잡게 되는 상대방 말 목록
    private boolean isFinishingMove = false;// 이 이동으로 완주하는지 여부

    public Move(Piece pieceToMove, PathNode startNode, PathNode endNode, List<Piece> capturedPieces) {
        this.pieceToMove = Objects.requireNonNull(pieceToMove);
        this.startNode = startNode; // 시작점이 집(null)일 수 있음
        this.endNode = Objects.requireNonNull(endNode, "이동 도착지는 null일 수 없습니다 (완주는 FinishingMove 플래그 사용).");
        this.capturedPieces = capturedPieces != null ? new ArrayList<>(capturedPieces) : new ArrayList<>();
    }

    // --- Getters ---
    public Piece getPieceToMove() { return pieceToMove; }
    public PathNode getStartNode() { return startNode; }
    public PathNode getEndNode() { return endNode; }
    public List<Piece> getCapturedPieces() { return Collections.unmodifiableList(capturedPieces); }
    public boolean isFinishingMove() { return isFinishingMove; }
    /** 완주 이동 여부를 설정합니다. (MovementService에서 호출) */
    public void setFinishingMove(boolean finishingMove) { isFinishingMove = finishingMove; }

    @Override
    public String toString() {
        String startDesc = (pieceToMove.isAtHome() && startNode == null) ? "집" : "칸" + (startNode != null ? startNode.getPosition() : "?");
        String endDesc = isFinishingMove ? "완주 지점" : "칸" + endNode.getPosition();
        String captureInfo = capturedPieces.isEmpty() ? "" : ", 잡기: " + capturedPieces.size() + "개";
        return "말" + pieceToMove.getId() + ": " + startDesc + " -> " + endDesc + captureInfo;
    }
}