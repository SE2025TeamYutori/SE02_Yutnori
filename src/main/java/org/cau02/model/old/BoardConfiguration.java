package org.cau02.model.old;
import java.util.*;


/** 게임판의 구조와 생성을 담당하는 설정 클래스 */
public class BoardConfiguration {
    // TODO: 실제 윷판 구조(29개 노드)에 맞게 상세 구현 필요
    public List<PathNode> generateStandardPath() {
        List<PathNode> path = new ArrayList<>();
        // 예시: 20개의 기본 노드 생성 및 연결 (실제 윷판 로직 필요)
        PathNode startNode = new PathNode(0); // 0번: 시작점(집) 또는 완주 지점 개념
        path.add(startNode);
        PathNode prev = startNode;
        for (int i = 1; i <= 20; i++) {
            PathNode current = new PathNode(i);
            path.add(current);
            prev.addConnection(current);
            // TODO: 모서리, 교차점 설정 및 지름길 연결 구현
            prev = current;
        }
        // 마지막 노드에서 시작 노드(0)로 돌아오는 완주 경로 설정 (예시)
        if (prev != startNode) {
            // prev.addConnection(startNode); // 마지막 노드가 0으로 가는지 확인 필요
        }
        return path;
    }
}