package org.cau02.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Piece {
    // properties
    private final int owner;
    private final Board board;

    // states
    private PieceState state = PieceState.READY;
    private BoardSpace location;
    private BoardPath path;
    private Piece carrier = null;
    private final List<Piece> carries = new ArrayList<>();

    // getter & setter
    public int getOwner() {
        return owner;
    }

    public Board getBoard() {
        return board;
    }

    public PieceState getState() {
        return state;
    }

    public BoardSpace getLocation() {
        if (state == PieceState.CARRIED) {
            return carrier.getLocation();
        } else {
            return location;
        }
    }

    public BoardPath getPath() throws IllegalStateException {
        return switch (state) {
            case ACTIVE -> path;
            case CARRIED -> carrier.path;
            default -> throw new IllegalStateException();
        };
    }

    public Piece getCarrier() {
        return carrier;
    }

    public List<Piece> getCarries() {
        return List.copyOf(carries);
    }

    void addCarry(Piece carry) {
        if (carry.owner != this.owner) {
            throw new IllegalArgumentException();
        }
        carry.state = PieceState.CARRIED;
        carry.carrier = this;
        carry.carries.forEach(p -> p.carrier = this);
        this.carries.add(carry);
        this.carries.addAll(carry.carries);
        carry.carries.clear();
    }

    Piece(Board board, int owner) {
        this.board = Objects.requireNonNull(board, "");
        location = board.readySpace;
        path = board.getDefaultPath();
        this.owner = owner;
    }

    BoardSpace getPossibleLocation(Yut yut) {
        if (state == PieceState.CARRIED) {
            return carrier.getPossibleLocation(yut);
        } else if (state == PieceState.GOAL) {
            throw new IllegalStateException();
        } else if (state == PieceState.READY && yut == Yut.BACKDO) {
            throw new IllegalArgumentException();
        }

        int possibleIndex = yut.getValue();
        if (state == PieceState.ACTIVE) {
            possibleIndex += path.indexOf(location);
        }

        if (possibleIndex >= path.size()) {
            return board.goalSpace;
        } else if (possibleIndex == 0) {
            possibleIndex = path.size() - 1;
        }

        return path.get(possibleIndex);
    }

    void move(Yut yut) {
        if (state == PieceState.CARRIED) {
            carrier.move(yut);
            return;
        } else if (state == PieceState.GOAL) {
            throw new IllegalStateException();
        } else if (state == PieceState.READY && yut == Yut.BACKDO) {
            throw new IllegalArgumentException();
        }

        int pathIndex = yut.getValue();
        if (state == PieceState.ACTIVE) {
            pathIndex += path.indexOf(location);
        }

        if (pathIndex >= path.size()) {
            state = PieceState.GOAL;
            location = board.goalSpace;
            for (Piece p : carries) {
                p.state = PieceState.GOAL;
                p.location = board.goalSpace;
                p.carrier = null;
            }
            carries.clear();
            return;
        } else if (pathIndex == 0) {
            pathIndex = path.size() - 1;
        }

        location = path.get(pathIndex);
        path = board.computeNextPath(path, location);
        if (state == PieceState.READY) {
            state = PieceState.ACTIVE;
        }
    }

    void reset() {
        if (state == PieceState.CARRIED) {
            carrier.reset();
            return;
        }

        state = PieceState.READY;
        location = board.readySpace;
        path = board.getDefaultPath();
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
