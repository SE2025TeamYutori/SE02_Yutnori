package org.cau02.view;

import org.cau02.model.old.Board;
import org.cau02.model.old.Move;
import org.cau02.model.old.Player;
import org.cau02.model.old.YutResult;

import java.util.List;

/** 게임의 사용자 인터페이스(표현 및 입력) 정의 */
public interface GameView {

    /** 시작 환영 메시지를 표시합니다. */
    void displayWelcome();

    /** 플레이어 이름 목록을 입력받아 반환합니다. */
    List<String> promptPlayerNames();

    /** 윷 던지기 방식(랜덤/지정)을 선택받아 반환합니다. */
    ThrowType promptThrowType(Player player);

    /** 지정할 윷 결과를 선택받아 반환합니다. */
    YutResult promptSpecificYutResult();

    /** 현재 턴 정보를 표시합니다. */
    void displayTurnStart(int turnCount, Player player);

    /** 플레이어의 현재 상태(점수, 말 위치 등)를 표시합니다. */
    void displayPlayerStatus(Player player);

    /** 현재 게임판의 상태를 표시합니다. */
    void displayBoard(Board board, List<Player> players);

    /** (랜덤) 윷 던지기 결과를 표시합니다. */
    void displayThrowResult(YutResult result);

    /** 사용자에게 특정 액션(Enter 누르기 등)을 요청합니다. */
    void promptAction(String message);

    /** 가능한 이동 목록을 보여주고 사용자(Human)의 선택을 받아 반환합니다. */
    Move promptMoveChoice(List<Move> possibleMoves, Player currentPlayer);

    /** 실행된 이동 정보를 표시합니다. */
    void displayMoveAction(Move move);

    /** 보너스 턴 발생을 알립니다. */
    void displayBonusTurn();

    /** 게임 종료 및 최종 승자를 표시합니다. */
    void displayWinner(Player winner);

    /** 일반 정보 메시지를 표시합니다. */
    void displayMessage(String message);

    /** View 사용이 끝날 때 리소스를 정리합니다. (예: Scanner 닫기) */
    void close();
}