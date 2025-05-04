import org.cau02.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class View_test_cli implements YutNoriObserver {
    GameManager gm;

    View_test_cli(GameManager gm) {
        this.gm = gm;
    }

    @Override
    public void onGameEnded() {
        System.out.println("<게임 종료!>");
        System.out.println("승리자: " + gm.getWinner() + "번 플레이어");
    }

    @Override
    public void onTurnChanged() {
        System.out.println("<새로운 턴!>");
        System.out.println("현재 차례: " + gm.getCurrentPlayer() + "번 플레이어");
    }

    @Override
    public void onYutStateChanged() {
        System.out.print("남은 윷 던지기 횟수: " + gm.getCurrentYutCount() + "회, ");
        System.out.println("남은 이동 횟수: " + gm.getCurrentMoveCount() + "회");
        System.out.println("족보 별 남은 이동 횟수");
        for (Yut y : Yut.values()) {
            System.out.print("  " + y.name() + ": " + gm.getYutResult().get(y.ordinal()) + "회 ");
        }
        System.out.println();
    }

    @Override
    public void onPieceMoved() {
        System.out.println("말 정보");
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            System.out.println("  플레이어 " + i);
            System.out.println("    대기 중인 말: " + gm.getReadyPiecesCount(i) + "개");
            System.out.println("    도착한 말: " + gm.getGoalPiecesCount(i) + "개");
            System.out.println("    게임판 위의 말 위치");
            for (Piece p : gm.getActivePieces(i)) {
                System.out.print("      " + gm.getBoard().getSpaces().indexOf(p.getLocation()));
                if (p.getState() == PieceState.CARRIED) {
                    System.out.print("[업힘] ");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    void printProperties() {
        System.out.println("<게임 정보>");
        System.out.println("게임 인스턴스: " + gm);
        System.out.println("게임판 모양: " + ((RegularBoard) gm.getBoard()).getBoardAngle() + "각형");
        System.out.println("플레이어 수: " + gm.getPlayerCount() + "명");
        System.out.println("플레이어당 말 수 : " + gm.getPieceCount() + "개");
        System.out.println("--------------------------------------------------------");
    }
}

public class Controller_test_cli {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        GameManager gm = new GameManager(4, 4, 5);
        View_test_cli view = new View_test_cli(gm);
        gm.registerObserver(view);

        while (true) {
            System.out.println("윷놀이 게임에 오신 걸 환영합니다!");
            System.out.print("게임판 모양을 선택하세요(4, 5, 6): ");
            int a = sc.nextInt();
            gm.setBoard(a);
            System.out.print("플레이어 수를 선택하세요(2~4): ");
            int b = sc.nextInt();
            gm.setPlayerCount(b);
            System.out.print("플레이어당 말 개수를 선택하세요(2~5): ");
            int c = sc.nextInt();
            gm.setPieceCount(c);

            gm.startGame();
            view.printProperties();

            while (gm.getState() == GameState.PLAYING) {
                System.out.println("플레이어 " + gm.getCurrentPlayer() + "의 턴");

                System.out.println("0: 랜덤 윷 던지기, 1: 선택 윷 던지기, 2: 말 이동하기");
                int n = sc.nextInt();

                switch (n) {
                    case 0:
                        try {
                            Yut y0 = gm.throwRandomYut();
                            System.out.println("나온 윷: " + y0);
                            break;
                        } catch (IllegalStateException e) {
                            System.out.println("윷을 던질 수 없습니다.");
                            break;
                        }

                    case 1:
                        try {
                            System.out.println("던질 족보를 선택하세요. 0-빽도, 1-도, 2-개, 3-걸, 4-윷, 5-모");
                            int sy = sc.nextInt();
                            Yut y1 = gm.throwSelectedYut(Yut.values()[sy]);
                            System.out.println("나온 윷: " + y1);
                            break;
                        } catch (IllegalStateException e) {
                            System.out.println("윷을 던질 수 없습니다.");
                            break;
                        }

                    case 2:
                        System.out.println("0: 새로운 말 이동하기, 1: 선택 말 이동하기");
                        int m = sc.nextInt();

                        if (m == 0) {
                            if (gm.getReadyPiecesCount(gm.getCurrentPlayer()) == 0) {
                                System.out.println("대기 중인 말이 없습니다.");
                                break;
                            }

                            System.out.println("가능한 목적지 목록");
                            for (BoardSpace s : gm.getPossibleLocationsOfNewPiece()) {
                                System.out.print(gm.getBoard().getSpaces().indexOf(s) + " ");
                            }
                            System.out.println();

                            System.out.println("어떤 족보로 이동할까요: 0 -빽도, 1-도, 2-개, 3-걸, 4-윷, 5-모");
                            System.out.println("족보 별 남은 이동 횟수");
                            for (Yut y : Yut.values()) {
                                System.out.print("  " + y.name() + ": " + gm.getYutResult().get(y.ordinal()) + "회 ");
                            }
                            System.out.println();
                            int ny = sc.nextInt();
                            try {
                                gm.moveNewPiece(Yut.values()[ny]);
                            } catch (IllegalStateException e) {
                                System.out.println("해당 족보로 이동 불가능합니다.(" + e.getMessage() + ")");
                            }
                        } else if (m == 1) {
                            List<Piece> ps = new ArrayList<>(gm.getActivePieces(gm.getCurrentPlayer()));

                            if (ps.isEmpty()) {
                                System.out.println("게임판 위에 말이 없습니다.");
                                break;
                            }

                            System.out.println("현재 게임판 위의 말 목록");
                            for (int i = 0; i < ps.size(); i++) {
                                System.out.println(i + ": " + gm.getBoard().getSpaces().indexOf(ps.get(i).getLocation()));
                            }
                            System.out.print("이동할 말의 번호를 선택하세요: ");
                            int pi = sc.nextInt();
                            System.out.println("가능한 목적지 목록");
                            for (BoardSpace s : gm.getPossibleLocations(ps.get(pi))) {
                                System.out.print(gm.getBoard().getSpaces().indexOf(s) + " ");
                            }
                            System.out.println("어떤 족보로 이동할까요: 0-빽도, 1-도, 2-개, 3-걸, 4-윷, 5-모");
                            System.out.println("족보 별 남은 이동 횟수");
                            for (Yut y : Yut.values()) {
                                System.out.print("  " + y.name() + ": " + gm.getYutResult().get(y.ordinal()) + "회 ");
                            }
                            System.out.println();
                            int py = sc.nextInt();

                            try {
                                gm.movePiece(ps.get(pi), Yut.values()[py]);
                            } catch (IllegalStateException e) {
                                System.out.println("해당 족보로 이동 불가능합니다.(" + e.getMessage() + ")");
                            }
                        }
                }
            }

            System.out.println("게임 종료!");
            view.printProperties();

            System.out.println("한판 더? (1/0)");
            int om = sc.nextInt();
            if (om == 1) {
                gm.resetGame();
            } else if (om == 0) {
                break;
            }
        }
    }
}
