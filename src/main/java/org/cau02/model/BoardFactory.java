package org.cau02.model;

// 게임판 인스턴스 생성용 팩토리
class BoardFactory {
    static Board createBoard(int boardAngle) throws IllegalArgumentException {
        return switch (boardAngle) {
            case 4 -> new SquareBoard();
            case 5 -> new PentagonBoard();
            case 6 -> new HexagonBoard(); // 현재 x
            default -> throw new IllegalArgumentException("해당 범위의 값으로 게임판을 생성할 수 없습니다.");
        };
    }
}
