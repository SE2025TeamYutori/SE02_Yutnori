package org.cau02.model.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.*;

/** 게임 판의 각 위치(칸)를 나타내는 노드 */
public class PathNode {
    private final int position; // 노드의 고유 위치 식별자
    private boolean isCorner = false;     // 모서리 여부
    private boolean isCenter = false;     // 중앙 교차점 여부 (지름길 구현 시 사용)
    private boolean isIntersection = false; // 분기점/합류점 여부 (지름길 구현 시 사용)
    private List<PathNode> nextNodes = new ArrayList<>(); // 다음 이동 가능한 노드 목록

    public PathNode(int position) {
        this.position = position;
    }

    public void addConnection(PathNode node) {
        if (node != null && !this.nextNodes.contains(node)) {
            this.nextNodes.add(node);
            // 양방향 연결 필요 시: node.addReverseConnection(this);
        }
    }

    // --- Getters and Setters ---
    public int getPosition() { return position; }
    public boolean isCorner() { return isCorner; }
    public void setCorner(boolean corner) { isCorner = corner; }
    public boolean isCenter() { return isCenter; }
    public void setCenter(boolean center) { isCenter = center; }
    public boolean isIntersection() { return isIntersection; }
    public void setIntersection(boolean intersection) { isIntersection = intersection; }
    public List<PathNode> getNextNodes() { return Collections.unmodifiableList(nextNodes); } // 방어적 복사

    @Override
    public String toString() { return "Node(" + position + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathNode pathNode = (PathNode) o;
        return position == pathNode.position;
    }

    @Override
    public int hashCode() { return Objects.hash(position); }
}
