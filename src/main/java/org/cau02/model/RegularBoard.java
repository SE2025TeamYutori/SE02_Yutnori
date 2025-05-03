package org.cau02.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 정다각형 모양 게임판을 나타내는 추상 클래스입니다.
 */
public abstract class RegularBoard extends Board {
    protected final int boardAngle; // n각형의 n

    /**
     * 게임판이 몇각형 모양인지 반환합니다.
     * @return n각형의 n
     */
    public int getBoardAngle() {
        return boardAngle;
    }

    // 외부에서 생성 못하도록 한 장치.
    RegularBoard(int boardAngle) {
        this.boardAngle = boardAngle;
        //generateBoard(); // 자동생성 구현 완료하면 사용
    }

    // n에 따라 게임판을 자동으로 생성해주는 메소드
    // 현재 버그남. 사용 x
    private void generateBoard() {
        spaces = new ArrayList<>(7 * boardAngle + 1);
        for (int i = 0; i < 7 * boardAngle + 1; i++) {
            spaces.add(new BoardSpace());
        }

        List<List<Integer>> paths_indexes = new ArrayList<>();
        paths_indexes.add(IntStream.range(0, 5 * boardAngle).boxed().collect(Collectors.toList()));
        paths_indexes.getFirst().add(0);
        List<Integer> shortPathIndex1 = new ArrayList<>(Arrays.asList(7 * boardAngle - 3, 7 * boardAngle - 4));
        shortPathIndex1.addAll(IntStream.range(5 * boardAngle - 5, 5 * boardAngle).boxed().toList());
        shortPathIndex1.add(0);
        List<Integer> shortPathIndex2 = Arrays.asList(7 * boardAngle - 1, 7 * boardAngle - 2, 0);
        int centerIndex = 7 * boardAngle;
        for (int i = 1; i < boardAngle - 3; i++) {
            int crossRoadIndex = 5 * i;

            List<Integer> path1 = IntStream.range(0, crossRoadIndex).boxed().collect(Collectors.toList());
            List<Integer> path2 = IntStream.range(0, crossRoadIndex).boxed().collect(Collectors.toList());

            path1.addAll(Arrays.asList(5 * boardAngle + 2 * i - 2, 5 * boardAngle + 2 * i - 1));
            path2.addAll(Arrays.asList(5 * boardAngle + 2 * i - 2, 5 * boardAngle + 2 * i - 1));
            path1.add(centerIndex);
            path2.add(centerIndex);
            path1.addAll(shortPathIndex1);
            path2.addAll(shortPathIndex2);

            paths_indexes.add(path1);
            paths_indexes.add(path2);
        }
        List<Integer> path1 = IntStream.range(0, 5 * boardAngle - 10).boxed().collect(Collectors.toList());
        path1.addAll(Arrays.asList(7 * boardAngle - 6, 7 * boardAngle - 5, centerIndex));
        path1.addAll(shortPathIndex2);
        paths_indexes.add(path1);

        paths = new ArrayList<>();

        for (List<Integer> pathIndexes : paths_indexes) {
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

    // 어떤 모양에서도 적용 가능한 메소드로
    // 현재 구현 안됨. 사용 x
    @Override
    BoardPath computeNextPath(BoardPath path, BoardSpace space) throws IllegalArgumentException {
        if (!spaces.contains(space) || !paths.contains(path)) {
            throw new IllegalArgumentException();
        }

        // 표를 만들고 뽑아내자
        // HashMap<BoardPath, HashMap<BoardSpace, BoardPath>>

        if (paths.indexOf(path) == 0) {

        } else {

        }


        return null;
    }
}
