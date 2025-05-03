package org.cau02.model;

import java.util.Iterator;
import java.util.List;

/**
 * 게임판의 경로 하나를 나타내는 클래스입니다.
 */
public class BoardPath implements Iterable<BoardSpace> {
    private final List<BoardSpace> path; // 경로; 이뮤터블임

    /**
     * 경로의 해당 인덱스의 칸을 반환합니다. List의 get과 동일.
     * @param index 인덱스
     * @return 해당 칸
     */
    public BoardSpace get(int index) {
        return path.get(index);
    }

    /**
     * 경로의 길이를 반환합니다. List의 size와 동일.
     * @return 경로의 길이
     */
    public int size() {
        return path.size();
    }

    /**
     * 해당 칸의 인덱스 번호를 반환합니다. List의 indexOf와 동일
     * @param space 해당 칸
     * @return 위치한 인덱스
     */
    public int indexOf(BoardSpace space) {
        return path.indexOf(space);
    }

    // 외부에서 생성 못하도록 한 장치.
    // 입력받은 List 복사해서 이뮤터블로 저장
    BoardPath(List<BoardSpace> path) {
        this.path = List.copyOf(path);
    }

    // 이터레이터 구현용
    @Override
    public Iterator<BoardSpace> iterator() {
        return List.copyOf(path).iterator();
    }
}
