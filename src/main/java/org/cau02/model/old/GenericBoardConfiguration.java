package org.cau02.model.old;
import org.cau02.model.old.interfaces.IBoardConfiguration;

import java.util.*;

/** N각형 형태의 커스텀 윷판 생성을 담당하는 구현체 */
public class GenericBoardConfiguration implements IBoardConfiguration {

    private final int sides; // N각형의 N (변의 수)
    private final int nodesPerSide; // 각 변 위의 노드 수 (모서리 포함 개념)

    public GenericBoardConfiguration(int sides, int nodesPerSide) {
        if (sides < 3) throw new IllegalArgumentException("판은 최소 삼각형(3변) 이상이어야 합니다.");
        if (nodesPerSide < 1) throw new IllegalArgumentException("각 변에는 최소 1개 이상의 노드가 있어야 합니다.");
        this.sides = sides;
        this.nodesPerSide = nodesPerSide;
    }

    @Override
    public List<PathNode> generatePathLayout() {
        List<PathNode> path = new ArrayList<>();
        PathNode startFinishNode = new PathNode(0); // 0번: 시작 및 도착 지점
        path.add(startFinishNode);

        PathNode previousNode = startFinishNode;
        int nodeCounter = 1;
        int totalOuterNodes = sides * nodesPerSide;

        // 외곽 경로 생성 (지름길 제외)
        for (int s = 0; s < sides; s++) {
            for (int n = 0; n < nodesPerSide; n++) {
                PathNode currentNode = new PathNode(nodeCounter++);
                path.add(currentNode);
                previousNode.addConnection(currentNode); // 단방향 연결

                // 모서리 판정: 각 변의 마지막 노드가 모서리
                if (n == nodesPerSide - 1) {
                    currentNode.setCorner(true);
                    // TODO: 지름길 또는 교차로 설정 로직 필요 시 여기에 추가
                    // 예: if (s == 0) { /* 첫번째 코너에서 중앙 연결? */ }
                }
                previousNode = currentNode;
            }
        }

        // 마지막 외곽 노드를 시작/도착(0번) 노드로 연결 (완주 경로)
        if (previousNode != startFinishNode) {
            previousNode.addConnection(startFinishNode);
        }

        // TODO: 지름길 (Shortcut) 및 중앙 경로 생성 로직 구현 필요
        // 예: PathNode centerNode = new PathNode(totalOuterNodes + 1); path.add(centerNode);
        // 예: 특정 코너 노드와 centerNode 연결, centerNode와 다른 코너 연결 등

        System.out.println("GenericBoardConfiguration: " + sides + "각형, 변당 " + nodesPerSide + "칸 외곽 경로 생성됨 (지름길 미구현).");
        return path;
    }
}
