package org.cau02.model.old;

import org.cau02.model.old.interfaces.IBoardConfiguration;

import java.util.*;

/** 게임판 자체를 나타내는 클래스 */
public class Board {
    private List<PathNode> pathNodes;
    private IBoardConfiguration config; // 인터페이스 타입 의존
    private PathNode startNode; // 시작/도착 지점 (Node 0)

    public Board(IBoardConfiguration config) {
        this.config = Objects.requireNonNull(config, "Board 설정 객체는 null일 수 없습니다.");
        createBoardPath();
    }

    private void createBoardPath() {
        this.pathNodes = config.generatePathLayout(); // 설정 객체에 경로 생성 위임
        this.startNode = findNodeByPosition(0).orElseThrow(
                () -> new IllegalStateException("Board 생성 실패: 시작 노드(0번)를 찾을 수 없습니다.")
        );
    }

    /** 지정된 위치 번호로 노드를 찾습니다. */
    public Optional<PathNode> findNodeByPosition(int position) {
        return pathNodes.stream()
                .filter(node -> node.getPosition() == position)
                .findFirst();
    }

    public PathNode getStartNode() { return startNode; }
    public List<PathNode> getPathNodes() { return Collections.unmodifiableList(pathNodes); }
}