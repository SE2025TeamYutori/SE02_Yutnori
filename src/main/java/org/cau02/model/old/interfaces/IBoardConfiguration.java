package org.cau02.model.old.interfaces;
import org.cau02.model.old.PathNode;

import java.util.List;

/** 게임판 생성 규칙 정의 */
public interface IBoardConfiguration {
    /** 게임판의 경로 노드 목록을 생성하여 반환합니다. */
    List<PathNode> generatePathLayout();
}