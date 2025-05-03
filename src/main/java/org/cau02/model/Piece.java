package org.cau02.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO 움직임 관련 로직 Board로 옮길 필요 있음

/**
 * 말 하나를 나타내는 클래스입니다.
 */
public class Piece {
    // properties
    private final int owner; // 소유자 플레이어 번호
    private final Board board; // 위치한 게임판

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
     * 이 말이 위치한 게임판 인스턴스를 반환합니다.
     * @return 이 말이 위치한 게임판 인스턴스
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 현재 말의 상태를 반환합니다.
     * @return 현재 말의 상태
     * <ul>{@link PieceState#READY}: 출발 전</ul>
     * <ul>{@link PieceState#ACTIVE}: 게임판 위에 존재</ul>
     * <ul>{@link PieceState#CARRIED}: 누군가에게 업힘</ul>
     * <ul>{@link PieceState#GOAL}: 도착함</ul>
     */
    public PieceState getState() {
        return state;
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
    Piece(Board board, int owner) {
        this.board = Objects.requireNonNull(board, "");
        location = board.readySpace;
        path = board.getDefaultPath();
        this.owner = owner;
    }

    // 해당 족보로 이동 가능한 칸을 리턴
    BoardSpace getPossibleLocation(Yut yut) {
        if (state == PieceState.CARRIED) { // 업혀있으면 업은애한테 위임
            return carrier.getPossibleLocation(yut);
        } else if (state == PieceState.GOAL) { // 도착해있으면 예외
            throw new IllegalArgumentException("도착한 말은 더이상 이동할 수 없습니다.");
        } else if (state == PieceState.READY && yut == Yut.BACKDO) { // 대기상태인데 빽도면 예외
            throw new IllegalArgumentException("출발 전 상태의 말은 빽도로 움직일 수 없습니다.");
        }

        // 경로의 인덱스 계산
        int possibleIndex = yut.getValue();
        if (state == PieceState.ACTIVE) {
            possibleIndex += path.indexOf(location);
        }

        if (possibleIndex >= path.size()) { // 인덱스를 넘어가면 도착한거임
            return board.goalSpace;
        } else if (possibleIndex == 0) { // 0번칸은 마지막칸임
            possibleIndex = path.size() - 1;
        }

        return path.get(possibleIndex);
    }

    // 해당 족보로 이동
    void move(Yut yut) {
        if (state == PieceState.CARRIED) { // 업혀있으면 업은애한테 위임
            carrier.move(yut);
            return;
        } else if (state == PieceState.GOAL) { // 도착해있으면 예외
            throw new IllegalStateException("도착한 말은 더이상 이동할 수 없습니다.");
        } else if (state == PieceState.READY && yut == Yut.BACKDO) { // 대기상태인데 빽도면 예외
            throw new IllegalArgumentException("출발 전 상태의 말은 빽도로 움직일 수 없습니다.");
        }

        // 경로의 인덱스 계산
        int pathIndex = yut.getValue();
        if (state == PieceState.ACTIVE) {
            pathIndex += path.indexOf(location);
        }

        if (pathIndex >= path.size()) { // 인덱스를 넘어가면 도착한거임
            state = PieceState.GOAL;
            location = board.goalSpace;
            for (Piece p : carries) { // 업혀있는애들도 전부 도착 처리 + 업음/업힘 전부 해제
                p.state = PieceState.GOAL;
                p.location = board.goalSpace;
                p.carrier = null;
            }
            carries.clear();
            return;
        } else if (pathIndex == 0) { // 0번칸은 마지막칸임
            pathIndex = path.size() - 1;
        }

        location = path.get(pathIndex); // 위치 설정
        path = board.computeNextPath(path, location); // 경로 설정
        if (state == PieceState.READY) { // 활성화
            state = PieceState.ACTIVE;
        }
    }

    // 해당 말 초기화 (대기상태로 보냄)
    void reset() {
        if (state == PieceState.CARRIED) { // 업혀있으면 업은애한테 위임
            carrier.reset();
            return;
        }

        //초기화
        state = PieceState.READY;
        location = board.readySpace;
        path = board.getDefaultPath();

        // 업혀있는애들도 전부 초기화 + 업음/업힘 전부 해제
        carrier = null;
        for (Piece p : carries) {
            p.state = PieceState.READY;
            p.location = board.readySpace;
            p.path = board.getDefaultPath();
            p.carrier = null;
        }
        carries.clear();
    }
}
