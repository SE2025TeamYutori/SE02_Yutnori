package org.cau02.model;

/**
 * {@link Piece}의 상태를 나타냅니다.
 * <ul>
    * <li>{@link PieceState#READY}: 출발 전</li>
    * <li>{@link PieceState#ACTIVE}: 게임판 위에 존재</li>
    * <li>{@link PieceState#CARRIED}: 누군가에게 업힘</li>
    * <li>{@link PieceState#GOAL}: 도착함</li>
 * </ul>
 */
public enum PieceState {
    READY,
    ACTIVE,
    CARRIED,
    GOAL
}
