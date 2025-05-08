package org.cau02.model.old;
import java.util.Objects;

/** 플레이어의 말(Pawn) */
public class Piece {
    private final int id;           // 플레이어 내 말 식별 번호
    private final Player owner;     // 이 말의 소유자
    private PathNode position;      // 현재 위치 (null이면 시작 전/완주 후)
    private boolean isHome = true;      // 시작 위치(집)에 있는지 여부
    private boolean isFinished = false; // 완주 여부
    // TODO: 업힌 말 목록 등 추가 상태 구현 가능

    public Piece(int id, Player owner) {
        this.id = id;
        this.owner = Objects.requireNonNull(owner, "말의 소유자는 null일 수 없습니다.");
        this.position = null; // 초기 상태: 집
    }

    /** 말을 지정된 위치로 이동시킵니다. (MovementService에서 호출) */
    public void moveTo(PathNode destination) {
        this.position = destination; // null이 될 수도 있음 (잡히거나 완주 시)
        this.isHome = false;
    }

    /** 말을 시작 위치(집)으로 되돌립니다. (MovementService에서 호출) */
    public void returnToHome() {
        this.position = null;
        this.isHome = true;
        this.isFinished = false; // 잡히면 완주 상태 해제
    }

    /** 말의 완주 상태를 설정합니다. (MovementService에서 호출) */
    public void setFinished(boolean finished) {
        if (finished) {
            this.position = null; // 완주하면 판에서 제거됨
            this.isHome = false;
        }
        this.isFinished = finished;
    }

    // --- 상태 확인 메서드 ---
    public boolean isAtHome() { return isHome && !isFinished; }
    public boolean isOnBoard() { return position != null && !isFinished; }
    public boolean isFinished() { return isFinished; }

    // --- Getters ---
    public int getId() { return id; }
    public Player getOwner() { return owner; }
    public PathNode getPosition() { return position; }

    @Override
    public String toString() {
        String posStr;
        if (isFinished()) posStr = "완주";
        else if (isAtHome()) posStr = "집";
        else posStr = "칸" + position.getPosition();
        return "말" + id + "(" + posStr + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        // 플레이어 내에서 ID가 고유하고, 플레이어도 고유 ID를 가지므로 조합하여 비교
        return id == piece.id && owner.getId() == piece.owner.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner.getId());
    }
}
