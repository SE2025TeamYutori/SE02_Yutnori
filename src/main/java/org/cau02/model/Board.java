package org.cau02.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * TODO
 *  아직 Board랑 Piece의 이동 관련 책임 분리가 완벽하게 나뉘어지지 않음
 *  약간 스파게티 상태..
 *  근데 일단 돌아가니까 시간 많이 남으면 그때 수정해볼게요
 */

// 보드의 말 이동 결과
enum MoveResult {
    MOVE, // 그냥 이동
    CARRY, // 업힘
    CATCH // 잡음
}

/**
 * 게임판의 추상 클래스입니다.
 */
public abstract class Board {
    final BoardSpace readySpace = new BoardSpace(); // 시작 칸
    final BoardSpace goalSpace = new BoardSpace(); // 도착 칸
    List<BoardSpace> spaces = new ArrayList<>(); // 칸들; 인덱스는 문서 참조
    List<BoardPath> paths = new ArrayList<>(); // 경로들
    HashMap<BoardSpace, Piece> pieceOnBoardMap = new HashMap<>(); // 게임판 위의 말들 정보


    /**
     * 시작 칸을 반환합니다.
     * @return 시작 칸
     */
    public BoardSpace getReadySpace() {
        return readySpace;
    }

    /**
     * 도착 칸을 반환합니다.
     * @return 도착 칸
     */
    public BoardSpace getGoalSpace() {
        return goalSpace;
    }

    /**
     * 게임판의 칸들을 반환합니다.
     * @return 게임판의 칸들 List. 인덱스는 문서 참조
     */
    public List<BoardSpace> getSpaces() {
        return List.copyOf(spaces);
    }

    /**
     * 게임판의 경로 중 시작 경로를 반환합니다.
     * @return 시작 경로
     */
    public BoardPath getDefaultPath() {
        return paths.getFirst();
    }

    // 외부에서 생성 못하도록 한 장치
    Board() {}

    // 현재 경로와 칸을 기반으로 다음 경로 계산하는 메소드 (추상).
    abstract BoardPath computeNextPath(BoardPath path, BoardSpace space) throws IllegalArgumentException;

    // 해당 말이 해당 족보로 이동 가능한 칸을 리턴
    BoardSpace getPossibleLocation(Piece piece, Yut yut) throws IllegalArgumentException {
        if (piece.getState() == PieceState.CARRIED) { // 업혀있으면 업은애한테 위임
            return getPossibleLocation(piece.getCarrier(), yut);
        }

        if (piece.getState() == PieceState.GOAL) { // 도착해있으면 예외
            throw new IllegalArgumentException("도착한 말은 더이상 이동할 수 없습니다.");
        }
        if (piece.getState() == PieceState.READY && yut == Yut.BACKDO) { // 대기상태인데 빽도면 예외
            throw new IllegalArgumentException("출발 전 상태의 말은 빽도로 움직일 수 없습니다.");
        }
        if (piece.getState() == PieceState.ACTIVE && !pieceOnBoardMap.containsValue(piece)) { // 게임판에 있는데 이게임판에 없으면 예외
            throw new IllegalArgumentException("현재 게임판에 존재하지 않은 말은 이동할 수 없습니다.");
        }

        // 경로의 인덱스 계산
        int possibleIndex = yut.getValue();
        if (piece.getState() == PieceState.ACTIVE) {
            possibleIndex += piece.getPath().indexOf(piece.getLocation());
        }

        if (possibleIndex >= piece.getPath().size()) { // 인덱스를 넘어가면 도착한거임
            return goalSpace;
        }

        return piece.getPath().get(possibleIndex);
    }

    // 해당 말을 해당 족보로 이동
    MoveResult move(Piece piece, Yut yut) {
        if (piece.getState() == PieceState.CARRIED) { // 업혀있으면 업은애한테 위임
            return move(piece.getCarrier(), yut);
        }

        if (piece.getState() == PieceState.GOAL) { // 도착해있으면 예외
            throw new IllegalStateException("도착한 말은 더이상 이동할 수 없습니다.");
        }
        if (piece.getState() == PieceState.READY && yut == Yut.BACKDO) { // 대기상태인데 빽도면 예외
            throw new IllegalArgumentException("출발 전 상태의 말은 빽도로 움직일 수 없습니다.");
        }
        if (piece.getState() == PieceState.ACTIVE && !pieceOnBoardMap.containsValue(piece)) { // 게임판에 있는데 이게임판에 없으면 예외
            throw new IllegalArgumentException("현재 게임판에 존재하지 않은 말은 이동할 수 없습니다.");
        }

        // 경로의 인덱스 계산
        int pathIndex = yut.getValue();
        if (piece.getState() == PieceState.ACTIVE) {
            pathIndex += piece.getPath().indexOf(piece.getLocation());
        }

        if (pathIndex >= piece.getPath().size()) { // 인덱스를 넘어가면 도착한거임
            piece.setStateGoal(goalSpace);
            return MoveResult.MOVE;
        }

        piece.setLocation(piece.getPath().get(pathIndex)); // 위치 설정
        piece.setPath(computeNextPath(piece.getPath(), piece.getLocation())); // 경로 설정
        piece.setStateActive(); // 말 상태 활성화로 변경

        // 말을 게임판의 해당 위치에 세팅.
        // 잡기, 업기 처리도 하고 그랬는지 리턴
        
        // 이전에 있던 부분에서 제거
        for (BoardSpace s : pieceOnBoardMap.keySet()) {
            if (pieceOnBoardMap.get(s) == piece) {
                pieceOnBoardMap.put(s, null);
            }
        }

        // 일반 상황 처리
        Piece oldPiece = pieceOnBoardMap.get(piece.getLocation()); // 움직일 위치에 있던 말 
        if (oldPiece == null) { // 그 위치에 아무 말도 없으면 그냥 세팅 후 리턴
            pieceOnBoardMap.put(piece.getLocation(), piece);
            return MoveResult.MOVE;
        } else { // 그 위치에 말이 있는데
            if (oldPiece.getOwner() == piece.getOwner()) { // 같은편 말이면 업고 리턴
                oldPiece.addCarry(piece);
                return MoveResult.CARRY;
            } else { // 다른편 말이면 잡고 리턴
                reset(oldPiece);
                pieceOnBoardMap.put(piece.getLocation(), piece);
                return MoveResult.CATCH;
            }
        }
    }

    // 해당 말 초기화 (대기상태로 보냄)
    void reset(Piece piece) {
        if (piece.getState() == PieceState.CARRIED) { // 업혀있으면 업은애한테 위임
            reset(piece.getCarrier());
            return;
        }

        piece.setStateReady(readySpace, getDefaultPath());
    }
}
