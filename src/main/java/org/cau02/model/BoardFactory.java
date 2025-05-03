package org.cau02.model;

class BoardFactory {
    static Board createBoard(int boardAngle) throws IllegalArgumentException {
        if (boardAngle < 3) {
            throw new IllegalArgumentException("");
        }

        return switch (boardAngle) {
            case 4 -> new SquareBoard();
            case 5 -> new PentagonBoard();
            case 6 -> new HexagonBoard();
            default -> throw new IllegalArgumentException("");
        };

    }

    private Board generateBoard(int boardAngle) throws IllegalArgumentException {
        if (boardAngle < 3) {
            throw new IllegalArgumentException("");
        } else {

        }
        return null;
    }
}
