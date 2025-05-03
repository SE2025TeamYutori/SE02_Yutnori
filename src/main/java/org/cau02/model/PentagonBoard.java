package org.cau02.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 오각형 모양 윷놀이 게임판입니다.
 */
public class PentagonBoard extends RegularBoard {
    private static final int[][] PATHS_INDEXES = {
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 0 },
            { 0, 1, 2, 3, 4, 5, 25, 26, 35, 32, 31, 20, 21, 22, 23, 24, 0 },
            { 0, 1, 2, 3, 4, 5, 25, 26, 35, 34, 33, 0 },
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 27, 28, 35, 32, 31, 20, 21, 22, 23, 24, 0 },
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 27, 28, 35, 34, 33, 0},
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 29, 30, 35, 34, 33, 0}
    };

    PentagonBoard() {
        super(5);

        // RegularBoard의 자동 생성 구현 완료하면 이하 삭제
        spaces = new ArrayList<>(7 * boardAngle + 1);
        for (int i = 0; i < 7 * boardAngle + 1; i++) {
            spaces.add(new BoardSpace());
        }
        paths = new ArrayList<>();

        for (int[] pathIndexes : PATHS_INDEXES) {
            List<BoardSpace> path = new ArrayList<>();
            for (int i : pathIndexes) {
                path.add(spaces.get(i));
            }
            paths.add(new BoardPath(path));
        }

        pieceOnBoardMap = new HashMap<>();
        for (BoardSpace space : spaces) {
            pieceOnBoardMap.put(space, null);
        }
    }

    // RegularBoard의 메소드 구현 완료하면 이 메소드 구현 삭제
    @Override
    BoardPath computeNextPath(BoardPath path, BoardSpace space) throws IllegalArgumentException {
        if (!spaces.contains(space) || !paths.contains(path)) {
            throw new IllegalArgumentException("경로나 칸이 게임판에 존재하지 않습니다.");
        }

        return switch (paths.indexOf(path)) {
            case 0 -> switch (spaces.indexOf(space)) {
                case 5 -> paths.get(1);
                case 10 -> paths.get(3);
                case 15 -> paths.get(5);
                default -> path;
            };
            case 1 -> switch (spaces.indexOf(space)) {
                case 35 -> paths.get(2);
                case 4 -> paths.get(0);
                default -> path;
            };
            case 2 -> switch (spaces.indexOf(space)) {
                case 26 -> paths.get(1);
                default -> path;
            };
            case 3 -> switch (spaces.indexOf(space)) {
                case 35 -> paths.get(4);
                case 9 -> paths.get(0);
                default -> path;
            };
            case 4 -> switch (spaces.indexOf(space)) {
                case 28 -> paths.get(3);
                default -> path;
            };
            case 5 -> switch (spaces.indexOf(space)) {
                case 14 -> paths.get(0);
                default -> path;
            };
            default -> throw new IllegalArgumentException(); //도달 불가
        };
    }
}
