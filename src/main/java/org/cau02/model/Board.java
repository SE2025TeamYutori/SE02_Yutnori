package org.cau02.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    // 말을 게임판의 해당 위치에 세팅.
    // 잡기, 업기 처리도 하고 그랬는지 리턴
    MoveResult setPieceOnBoardSpace(Piece piece, BoardSpace space) throws IllegalArgumentException {
        // 시작이나 도착 위치로 세팅하면 그냥 리턴
        if (space == readySpace || space == goalSpace) {
            return MoveResult.MOVE;
        }
        
        if (!pieceOnBoardMap.containsKey(space)) { // 게임판에 없는 칸이면 예외
            throw new IllegalArgumentException("게임판에 존재하지 않는 칸입니다.");
        }

        // 이전에 있던 부분에서 제거
        for (BoardSpace s : pieceOnBoardMap.keySet()) {
            if (pieceOnBoardMap.get(s) == piece) {
                pieceOnBoardMap.put(s, null);
            }
        }

        // 일반 상황 처리
        Piece oldPiece = pieceOnBoardMap.get(space); // 움직일 위치에 있던 말 
        if (oldPiece == null) { // 그 위치에 아무 말도 없으면 그냥 세팅 후 리턴
            pieceOnBoardMap.put(space, piece);
            return MoveResult.MOVE;
        } else { // 그 위치에 말이 있는데
            if (oldPiece.getOwner() == piece.getOwner()) { // 같은편 말이면 업고 리턴
                oldPiece.addCarry(piece);
                return MoveResult.CARRY;
            } else { // 다른편 말이면 잡고 리턴
                oldPiece.reset();
                pieceOnBoardMap.put(space, piece);
                return MoveResult.CATCH;
            }
        }
    }
}
