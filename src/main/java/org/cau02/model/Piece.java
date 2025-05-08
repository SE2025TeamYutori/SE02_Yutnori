package org.cau02.model;

import java.util.ArrayList;
import java.util.List;

// TODO 움직임 관련 로직 Board로 옮길 필요 있음

/**
 * 말 하나를 나타내는 클래스입니다.
 */
public class Piece {
    // properties
    private final int owner; // 소유자 플레이어 번호

    // states
    // 기본적으로 업혀있으면, location과 path 정보는 의미 없어지고, carrier에게 위임
    private PieceState state = PieceState.READY; // 말 상태
    private BoardSpace location; // 말 위치 칸
    private BoardPath path; // 말 경로
    private Piece carrier = null; // 이 말을 업고 있는 말
    private final List<Piece> carries = new ArrayList<>(); // 이 말이 업고 있는 말들

    // getter & setter
    /**
     * 이 말을 소유한 플레이어의 번호를 반환합니다.
     * @return 이 말을 소유한 플레이어 번호
     */
    public int getOwner() {
        return owner;
    }

    /**
     * 현재 말의 상태를 반환합니다.
     * @return 현재 말의 상태
     * <ul>
        * <li>{@link PieceState#READY}: 출발 전</li>
        * <li>{@link PieceState#ACTIVE}: 게임판 위에 존재</li>
        * <li>{@link PieceState#CARRIED}: 누군가에게 업힘</li>
        * <li>{@link PieceState#GOAL}: 도착함</li>
     * </ul>
     */
    public PieceState getState() {
        return state;
    }

    // 상태를 레디로 변경.
    protected void setStateReady(BoardSpace readySpace, BoardPath defaultPath) {
        state = PieceState.READY;
        location = readySpace;
        path = defaultPath;

        // 업혀있는애들도 전부 초기화 + 업음/업힘 전부 해제
        carrier = null;
        for (Piece p : carries) {
            p.state = PieceState.READY;
            p.location = readySpace;
            p.path = defaultPath;
            p.carrier = null;
        }
        carries.clear();
    }

    // 상태를 활성화로 변경 (준비상태일때만)
    protected void setStateActive() {
        if (state == PieceState.READY) {
            state = PieceState.ACTIVE;
        }
    }

    // 상태를 도착으로 변경
    protected void setStateGoal(BoardSpace goalSpace) {
        state = PieceState.GOAL;
        location = goalSpace;

        carrier = null;
        for (Piece p : carries) { // 업혀있는애들도 전부 도착 처리 + 업음/업힘 전부 해제
            p.state = PieceState.GOAL;
            p.location = goalSpace;
            p.carrier = null;
        }
        carries.clear();
    }

    /**
     * 현재 말이 위치한 칸을 반환합니다.
     * @return 현재 말이 위치한 칸
     */
    public BoardSpace getLocation() {
        if (state == PieceState.CARRIED) { // 업혀있으면 업고있는애 정보 반환
            return carrier.getLocation();
        } else {
            return location;
        }
    }

    protected void setLocation(BoardSpace space) {
        this.location = space;
    }

    /**
     * 현재 말의 경로를 반환합니다.
     * @return 현재 말의 경로
     * @throws IllegalStateException 현재 말의 상태가 {@link PieceState#GOAL}이면
     */
    public BoardPath getPath() throws IllegalStateException {
        if (state == PieceState.GOAL) {
            throw new IllegalStateException("도착한 말의 경로를 반환할 수 없습니다.");
        }
        
        if (state == PieceState.CARRIED) { // 업혀있으면 업고있는애 정보 반환
            return carrier.path;
        } else {
            return path;
        }
    }

    protected void setPath(BoardPath path) {
        this.path = path;
    }

    /**
     * 현재 말을 업고 있는 말을 반환합니다.
     * @return 현재 말을 업고 있는 말. 업혀있지 않다면 null 반환
     */
    public Piece getCarrier() {
        return carrier;
    }

    /**
     * 현재 말이 업고 있는 말들의 List를 반환합니다.
     * @return 현재 말이 업고 있는 말들의 List
     */
    public List<Piece> getCarries() {
        return List.copyOf(carries); // 불변 리스트로 반환
    }

    // 해당 말을 업음
    void addCarry(Piece carry) throws IllegalArgumentException {
        if (carry.owner != this.owner) { // 둘이 소유자가 다르면 못업음
            throw new IllegalArgumentException("소유자가 다른 말을 업을 수 없습니다.");
        }
        // 해당 말이 업고 있는 애들까지 전부 업고, 해당 말의 carriers를 clear
        carry.state = PieceState.CARRIED;
        carry.carrier = this;
        carry.carries.forEach(p -> p.carrier = this);
        this.carries.add(carry);
        this.carries.addAll(carry.carries);
        carry.carries.clear();
    }

    // 외부에서 생성 못하도록 한 장치
    Piece(BoardSpace readySpace, BoardPath defaultPath, int owner) {
        location = readySpace;
        path = defaultPath;
        this.owner = owner;
    }
}
