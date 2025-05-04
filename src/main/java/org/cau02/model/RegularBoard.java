package org.cau02.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 정다각형 모양 게임판을 나타내는 추상 클래스입니다.
 * {@link Board#getSpaces()}의 인덱스 규칙 (문서 참고)
 * <ol>
    * <li>먼저 가장자리 경로를 따라 반시계방향 번호</li>
    * <li>갈림길에서, 가운데로 가는 방향을 따라 번호</li>
    * <li>그 다음 갈림길은 반시계 방향을 따라 계속</li>
    * <li>가운데 칸</li>
 * </ol>
 */
public abstract class RegularBoard extends Board {
    protected final int boardAngle; // n각형의 n
    private final HashMap<BoardPath, HashMap<BoardSpace, BoardPath>> stateMachine = new HashMap<>();

    /**
     * 게임판이 몇각형 모양인지 반환합니다.
     * @return n각형의 n
     */
    public int getBoardAngle() {
        return boardAngle;
    }

    /**
     * 입력된 각 개수를 바탕으로 게임판을 자동으로 생성합니다.
     * @param boardAngle n각형의 n
     */
    protected RegularBoard(int boardAngle) {
        this.boardAngle = boardAngle;
        generateBoard();
        generateStateMachine();
    }

    /*
     * n에 따라 게임판을 자동으로 생성해주는 메소드
     * 총 칸은 7*boardAngle+1개로 생성
     * 총 경로는 2*(boardAngle-2)개로 생성. 모든 경로의 끝에는 0 추가
     *  - 기본경로(경로0): 테두리 따라감; 0~5*boardAngle-1
     *  - 2*i-1번째 경로: 기본경로로 가다 i번째 갈림길에서 갈라지는 경로; 가운데 지나가고 도착지와 두번째로 가까운 길로 지나감
     *  - 2*i번째 경로: 2*i-1번째 경로로 가다 가운데 갈림길에서 갈라지는 경로; 가운데 지나가고 도착지와 첫번째로 가까운 길로 지나감
     *  - 단 마지막 경로는, 도착지와 두번째로 가까운 길로 가면 더 멀어지므로, 바로 도착지와 첫번째로 가까운 길로 지나감.
     * 마지막으로 말 위치 Map 생성
     */
    private void generateBoard() {
        // 일단 칸들 생성
        spaces = new ArrayList<>(7 * boardAngle + 1);
        for (int i = 0; i < 7 * boardAngle + 1; i++) {
            spaces.add(new BoardSpace());
        }

        // 먼저 경로의 인덱스들을 생성함
        List<List<Integer>> paths_indexes = new ArrayList<>();
        // 테두리 경로 (기본경로); 0~5*boardAngle-1, 0
        paths_indexes.add(IntStream.range(0, 5 * boardAngle).boxed().collect(Collectors.toList()));
        paths_indexes.getFirst().add(0);
        // 가운데칸에서 도착지와 두번째로 가까운 길; 7*boardAngle-3, 7*boardAngle-4, 5*boardAngle-5~5*boardAngle-1, 0
        List<Integer> shortPathIndex1 = new ArrayList<>(Arrays.asList(7 * boardAngle - 3, 7 * boardAngle - 4));
        shortPathIndex1.addAll(IntStream.range(5 * boardAngle - 5, 5 * boardAngle).boxed().toList());
        shortPathIndex1.add(0);
        // 가운데칸에서 도착지와 첫번째로 가까운 길; 7*boardAngle-1, 7*boardAngle-2, 0
        List<Integer> shortPathIndex2 = Arrays.asList(7 * boardAngle - 1, 7 * boardAngle - 2, 0);
        // 가운데 칸 인덱스; 7*boardAngle
        int centerIndex = 7 * boardAngle;
        // 마지막 갈림길 제외하고, 각 갈림길들에서의 경로들
        for (int i = 1; i <= boardAngle - 3; i++) {
            int crossRoadIndex = 5 * i; // 갈림길 칸 인덱스

            // path1: 가운데 칸을 지나치는 경로
            // path2: 가운데 칸에 멈췄다 가는 경로
            // 0~갈림길칸 길 추가; 0~5*i
            List<Integer> path1 = IntStream.rangeClosed(0, crossRoadIndex).boxed().collect(Collectors.toList());
            List<Integer> path2 = IntStream.rangeClosed(0, crossRoadIndex).boxed().collect(Collectors.toList());

            // 갈림길칸~가운데칸 길 추가; 5*boardAngle+2*i-2~5*boardAngle+2*i-1, 5*i
            path1.addAll(Arrays.asList(5 * boardAngle + 2 * i - 2, 5 * boardAngle + 2 * i - 1));
            path2.addAll(Arrays.asList(5 * boardAngle + 2 * i - 2, 5 * boardAngle + 2 * i - 1));
            path1.add(centerIndex);
            path2.add(centerIndex);
            
            // 가운데칸에서 도착지와의 두번째, 첫번째로 가까운 길 추가
            path1.addAll(shortPathIndex1);
            path2.addAll(shortPathIndex2);

            // 완성
            paths_indexes.add(path1);
            paths_indexes.add(path2);
        }
        // 마지막 갈림길 경로; 가운데칸에서 도착지와 두번째로 가까운 길의 경로는 없음
        // 0~갈림길칸
        List<Integer> path1 = IntStream.rangeClosed(0, 5 * boardAngle - 10).boxed().collect(Collectors.toList());
        // 갈림길칸~가운데칸
        path1.addAll(Arrays.asList(7 * boardAngle - 6, 7 * boardAngle - 5, centerIndex));
        // 가운데칸~첫번째로가까운길
        path1.addAll(shortPathIndex2);
        //완성
        paths_indexes.add(path1);

        // 경로 인덱스들을 바탕으로 경로들 생성
        paths = new ArrayList<>();
        for (List<Integer> pathIndexes : paths_indexes) {
            List<BoardSpace> path = new ArrayList<>();
            for (int i : pathIndexes) {
                path.add(spaces.get(i));
            }
            paths.add(new BoardPath(path));
        }

        // 말 위치 Map 생성
        pieceOnBoardMap = new HashMap<>();
        for (BoardSpace space : spaces) {
            pieceOnBoardMap.put(space, null);
        }
    }

    /*
     * stateMachine 생성
     * 규칙
     *  - 0 -> 2*i-1 when i*5 (i=1~boardAngle-2) (외부 갈림길)
     *  - 2*i-1 -> 0 when i*5-1 (i=1~boardAngle-2) (외부 갈림길 경로에서 바깥으로 빽도)
     * 마지막 경로는 가운데 갈림길에서 갈라지지 않음
     *  - 2*i-1 -> 2*i when 7*boardAngle (i=1~boardAngle-3) (내부 갈림길(가운데 갈림길))
     *  - 2*i -> 2*i-1 when boardAngle*5+2*i-1 (i=1~boardAngle-3) (내부 갈림길에서 외부 갈림길로 빽도)
     */
    private void generateStateMachine() {
        for (BoardPath path : paths) {
            stateMachine.put(path, new HashMap<>());
        }

        for (int i = 1; i <= boardAngle - 3; i++) {
            stateMachine.get(paths.getFirst()).put(spaces.get(i * 5), paths.get(2 * i - 1));
            stateMachine.get(paths.get(2 * i - 1)).put(spaces.get(i * 5 - 1), paths.getFirst());
            stateMachine.get(paths.get(2 * i - 1)).put(spaces.get(7 * boardAngle), paths.get(2 * i));
            stateMachine.get(paths.get(2 * i)).put(spaces.get(boardAngle * 5 + 2 * i - 1), paths.get(2 * i - 1));
        }

        int i = boardAngle - 2;
        stateMachine.get(paths.getFirst()).put(spaces.get(i * 5), paths.get(2 * i - 1));
        stateMachine.get(paths.get(2 * i - 1)).put(spaces.get(i * 5 - 1), paths.getFirst());
    }

    /*
     * computeNextPath 구현
     * generateStateMachine에서 생성한 stateMachine을 사용하여(Map 기반) 바로 뽑아옴
     */
    @Override
    BoardPath computeNextPath(BoardPath path, BoardSpace space) throws IllegalArgumentException {
        if (!spaces.contains(space) || !paths.contains(path)) {
            throw new IllegalArgumentException("경로나 칸이 게임판에 존재하지 않습니다.");
        }

        return stateMachine.get(path).getOrDefault(space, path);
    }
}
