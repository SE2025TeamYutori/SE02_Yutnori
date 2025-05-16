package org.cau02.model;

// 게임판 인스턴스 생성용 팩토리
class BoardFactory {
    static Board createRegularBoard(int boardAngle) throws IllegalArgumentException {
        if (boardAngle < 4) {
            throw new IllegalArgumentException("게임판은 사각형 이상이어야 합니다.");
        }

        return new RegularBoard(boardAngle);
    }
}
