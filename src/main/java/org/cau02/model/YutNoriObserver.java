package org.cau02.model;

/**
 * {@link GameManager}에 등록할 수 있는 옵저버 인터페이스입니다.
 * <ul>
    * <li>{@link YutNoriObserver#onGameEnded()}: 게임이 종료되었을 때</li>
    * <li>{@link YutNoriObserver#onTurnChanged()}: 턴이 바뀌었을 때</li>
    * <li>{@link YutNoriObserver#onYutStateChanged()}: 윷 상태가 변화했을 때</li>
    * <li>{@link YutNoriObserver#onPieceMoved()}: 말의 움직임이 있었을 때</li>
 * </ul>
 */
public interface YutNoriObserver {
    void onGameEnded();
    void onTurnChanged();
    void onYutStateChanged();
    void onPieceMoved();
}
