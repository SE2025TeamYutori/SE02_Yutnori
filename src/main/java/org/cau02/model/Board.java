package org.cau02.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

enum MoveResult {
    MOVE,
    CARRY,
    CATCH
}

public abstract class Board {
    final BoardSpace readySpace = new BoardSpace();
    final BoardSpace goalSpace = new BoardSpace();
    List<BoardSpace> spaces = new ArrayList<>();
    List<BoardPath> paths = new ArrayList<>();
    HashMap<BoardSpace, Piece> pieceOnBoardMap = new HashMap<>();

    public BoardSpace getReadySpace() {
        return readySpace;
    }

    public BoardSpace getGoalSpace() {
        return goalSpace;
    }

    public List<BoardSpace> getSpaces() {
        return List.copyOf(spaces);
    }

    public BoardPath getDefaultPath() {
        return paths.getFirst();
    }

    Board() {}

    abstract BoardPath computeNextPath(BoardPath path, BoardSpace space) throws IllegalArgumentException;

    MoveResult setPieceOnBoardSpace(Piece piece, BoardSpace space) throws IllegalArgumentException {
        if (!pieceOnBoardMap.containsKey(space)) {
            throw new IllegalArgumentException();
        }

        for (BoardSpace s : pieceOnBoardMap.keySet()) {
            if (pieceOnBoardMap.get(s) == piece) {
                pieceOnBoardMap.put(s, null);
            }
        }

        if (space == readySpace || space == goalSpace) {
            return MoveResult.MOVE;
        }

        Piece oldPiece = pieceOnBoardMap.get(space);
        if (oldPiece == null) {
            pieceOnBoardMap.put(space, piece);
            return MoveResult.MOVE;
        } else {
            if (oldPiece.getOwner() == piece.getOwner()) {
                oldPiece.addCarry(piece);
                return MoveResult.CARRY;
            } else {
                oldPiece.reset();
                pieceOnBoardMap.put(space, piece);
                return MoveResult.CATCH;
            }
        }
    }

}
