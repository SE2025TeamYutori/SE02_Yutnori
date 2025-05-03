package org.cau02.model;

public interface YutNoriObserver {
    void onGameEnded();
    void onTurnChanged();
    void onYutStateChanged();
    void onPieceMoved();
}
