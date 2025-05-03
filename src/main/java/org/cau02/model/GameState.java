package org.cau02.model;

/**
 * {@link GameManager}의 상태를 나타냅니다.
 * <ul>{@link GameState#READY}: 게임 시작 전</ul>
 * <ul>{@link GameState#PLAYING}: 게임 중</ul>
 * <ul>{@link GameState#FINISHED}: 게임 종료</ul>
 */
public enum GameState {
    READY, // 게임 시작 전
    PLAYING, // 게임 중
    FINISHED // 게임 종료
}
