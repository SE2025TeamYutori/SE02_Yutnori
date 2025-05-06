package org.cau02.model;

// 게임판 인스턴스 생성용 팩토리
class BoardFactory {
    static Board createBoard(int boardAngle) throws IllegalArgumentException {
        if (boardAngle < 4) {
            throw new IllegalArgumentException("게임판은 사각형 이상이어야 합니다.");
        }
        return switch (boardAngle) {
            case 4 -> new SquareBoard();
            case 5 -> new PentagonBoard();
            case 6 -> new HexagonBoard();
            default -> new RegularBoard(boardAngle) {}; // 익명클래스로 생성
        };
    }
}
