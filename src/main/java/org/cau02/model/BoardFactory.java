package org.cau02.model;

class BoardFactory {
    static Board createBoard(int boardAngle) throws IllegalArgumentException {
        if (boardAngle < 3) {
            throw new IllegalArgumentException("");
        }

        return switch (boardAngle) {
            case 4 -> new SquareBoard();
            case 5 -> new PentagonBoard();
            case 6 -> new HexagonBoard(); // 현재 x
            default -> throw new IllegalArgumentException("");
        };
    }
}
