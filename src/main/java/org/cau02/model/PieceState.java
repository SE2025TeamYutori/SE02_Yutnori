package org.cau02.model;

/**
 * {@link Piece}의 상태를 나타냅니다.
 * <ul>{@link PieceState#READY}: 출발 전</ul>
 * <ul>{@link PieceState#ACTIVE}: 게임판 위에 존재</ul>
 * <ul>{@link PieceState#CARRIED}: 누군가에게 업힘</ul>
 * <ul>{@link PieceState#GOAL}: 도착함</ul>
 */
public enum PieceState {
    READY,
    ACTIVE,
    CARRIED,
    GOAL
}
