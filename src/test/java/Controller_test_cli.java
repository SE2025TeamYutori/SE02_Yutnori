import org.cau02.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// 옵저버 구현 클래스
class View_test_cli implements YutNoriObserver {
    GameManager gm;

    View_test_cli(GameManager gm) {
        this.gm = gm;
    }

    // 게임 끝났을 때 draw
    @Override
    public void onGameEnded() {
        System.out.println("<게임 종료!>");
        System.out.println("승리자: " + gm.getWinner() + "번 플레이어");
    }

    // 턴이 넘어갈 때 draw
    @Override
    public void onTurnChanged() {
        System.out.println("<새로운 턴!>");
        System.out.println("현재 차례: " + gm.getCurrentPlayer() + "번 플레이어");
    }

    // 윷 관련 변경될 때 draw
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

    // 말들 상태 변경될 때 draw
    @Override
    public void onPieceMoved() {
        System.out.println("말 정보");
        for (int i = 0; i < gm.getPlayerCount(); i++) { // 각 플레이어에 대해
            System.out.println("  플레이어 " + i);
            System.out.println("    대기 중인 말: " + gm.getReadyPiecesCount(i) + "개");
            System.out.println("    도착한 말: " + gm.getGoalPiecesCount(i) + "개");
            System.out.println("    게임판 위의 말 위치");
            for (Piece p : gm.getActivePieces(i)) {
                System.out.print("      " + gm.getBoard().getSpaces().indexOf(p.getLocation())); // <= 말이 Board의 Spaces의 몇 번 인덱스에 있는지 구하는 방법
                if (p.getState() == PieceState.CARRIED) {
                    System.out.print("[업힘] ");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    // 게임 정보 그리는 함수임
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
        GameManager gm = new GameManager(4, 4, 5); // 게임매니저
        View_test_cli view = new View_test_cli(gm); // 옵저버
        gm.registerObserver(view); // 게임매니저에 옵저버 등록

        // 바깥 루프 하나 == 게임 한 판
        while (true) {
            System.out.println("윷놀이 게임에 오신 걸 환영합니다!");
            // 게임판 모양 설정
            System.out.print("게임판 모양을 선택하세요(4, 5, 6): ");
            int a = sc.nextInt();
            gm.setBoard(a);
            // 플레이어 수 설정
            System.out.print("플레이어 수를 선택하세요(2~4): ");
            int b = sc.nextInt();
            gm.setPlayerCount(b);
            // 플레이어당 말 수 설정
            System.out.print("플레이어당 말 개수를 선택하세요(2~5): ");
            int c = sc.nextInt();
            gm.setPieceCount(c);

            // 게임 시작
            gm.startGame();
            view.printProperties(); // 그냥 정보 함 보여줄라고

            // 루프 하나 == 행동 하나
            while (gm.getState() == GameState.PLAYING) {
                System.out.println("플레이어 " + gm.getCurrentPlayer() + "의 턴"); // 누구 행동할 차례인지 보여주게

                // 행동 선택
                System.out.println("0: 랜덤 윷 던지기, 1: 선택 윷 던지기, 2: 말 이동하기");
                int n = sc.nextInt();

                // 행동에 따라
                switch (n) {
                    case 0: // 랜덤 윷 던지기
                        try {
                            // 랜덤 윷 던지기
                            Yut y0 = gm.throwRandomYut();
                            System.out.println("나온 윷: " + y0);
                            break;
                        } catch (IllegalStateException e) { // 뭔가 윷을 못던지는 상태; 아마 윷 던질 수 있는 횟수가 없을 때
                            System.out.println("윷을 던질 수 없습니다.");
                            break;
                        }

                    case 1: // 선택 윷 던지기
                        try {
                            // 선택 윷 던지기
                            System.out.println("던질 족보를 선택하세요. 0-빽도, 1-도, 2-개, 3-걸, 4-윷, 5-모");
                            int sy = sc.nextInt();
                            Yut y1 = gm.throwSelectedYut(Yut.values()[sy]); // 이런 식으로 선택 윷의 enum 만들어낼 수 있음
                            System.out.println("나온 윷: " + y1);
                            break;
                        } catch (IllegalStateException e) { // 뭔가 윷을 못던지는 상태; 아마 윷 던질 수 있는 횟수가 없을 때
                            System.out.println("윷을 던질 수 없습니다.");
                            break;
                        }

                    case 2: // 말 이동하기
                        // 행동 선택
                        System.out.println("0: 새로운 말 이동하기, 1: 선택 말 이동하기");
                        int m = sc.nextInt();

                        if (m == 0) { // 새로운 말 이동하기
                            if (gm.getReadyPiecesCount(gm.getCurrentPlayer()) == 0) { // 대기 중인 말 있는지 체크; 없으면 안됨
                                System.out.println("대기 중인 말이 없습니다.");
                                break;
                            }

                            // 현재 족보들로 한 번 움직여서 갈 수 있는 칸들 목록 보여주기
                            System.out.println("가능한 목적지 목록");
                            for (Yut y : Yut.values()) { // 각 윷별 족보를 순환하는 방법
                                BoardSpace s = gm.getPossibleLocationsOfNewPiece().get(y.ordinal()); //새 말이 해당 족보로 갈 수 있는 칸 인스턴스
                                if (s != null) { // null이라는건 해당 족보로 이동할 수 있는 횟수가 없는 것
                                    System.out.print(y.name() + ": " + gm.getBoard().getSpaces().indexOf(s) + " "); // <족보이름>: <칸 인덱스>
                                }
                            }
                            System.out.println();

                            // 족보별 남은 이동 횟수 보여주기
                            System.out.println("족보 별 남은 이동 횟수");
                            for (Yut y : Yut.values()) { // 각 윷별 족보를 순환하는 방법
                                System.out.print("  " + y.name() + ": " + gm.getYutResult().get(y.ordinal()) + "회 "); // <족보이름>: <그 족보의 남은 이동횟수>회
                            }
                            System.out.println();
                            
                            // 이동할 족보 선택하기
                            System.out.println("어떤 족보로 이동할까요: 0 -빽도, 1-도, 2-개, 3-걸, 4-윷, 5-모");
                            int ny = sc.nextInt();

                            // 이동
                            try {
                                gm.moveNewPiece(Yut.values()[ny]); // 그 족보로 이동
                            } catch (IllegalStateException e) { // 뭔가 그 족보로 못움직일때
                                System.out.println("해당 족보로 이동 불가능합니다.(" + e.getMessage() + ")");
                            }
                        } else if (m == 1) { // 선택 말 이동하기
                            List<Piece> ps = new ArrayList<>(gm.getActivePieces(gm.getCurrentPlayer())); // ActivePiece들을 set으로 관리하기 힘들어서 일단 list에 옮겨담아둠

                            if (ps.isEmpty()) { // 게임판 위에 말이 없으면 못하지
                                System.out.println("게임판 위에 말이 없습니다.");
                                break;
                            }

                            // 현재 게임판 위의 말들 목록 보여주기
                            System.out.println("현재 게임판 위의 말 목록");
                            for (int i = 0; i < ps.size(); i++) {
                                System.out.println(i + ": " + gm.getBoard().getSpaces().indexOf(ps.get(i).getLocation())); // 각 말이 몇번 칸 위에 있는지
                            }
                            
                            // 게임판 위의 말들 중 움직일 애 선택하기
                            System.out.print("이동할 말의 번호를 선택하세요: ");
                            int pi = sc.nextInt();
                            
                            // 선택한 애가 갈 수 있는 칸들 보여주기
                            System.out.println("가능한 목적지 목록");
                            for (Yut y : Yut.values()) { // 갗 윷별 족보 순환
                                BoardSpace s = gm.getPossibleLocations(ps.get(pi)).get(y.ordinal()); // 해당 말이 (ps.get(pi)) 그 족보로 갈 수 있는 칸
                                if (s != null) { // null이란건 그 족보의 횟수가 없다는 것
                                    System.out.print(y.name() + ": " + gm.getBoard().getSpaces().indexOf(s) + " ");
                                }
                            }
                            System.out.println();
                            
                            // 족보별 남은 이동 가능 횟수 보여주기
                            System.out.println("족보 별 남은 이동 횟수");
                            for (Yut y : Yut.values()) {
                                System.out.print("  " + y.name() + ": " + gm.getYutResult().get(y.ordinal()) + "회 ");
                            }
                            System.out.println();

                            // 이동할 족보 선택하기
                            System.out.println("어떤 족보로 이동할까요: 0-빽도, 1-도, 2-개, 3-걸, 4-윷, 5-모");
                            int py = sc.nextInt();

                            // 이동
                            try {
                                gm.movePiece(ps.get(pi), Yut.values()[py]); // 해당 말을 그 족보로 이동
                            } catch (IllegalStateException e) { // 뭔가 그 족보로 못움직일때
                                System.out.println("해당 족보로 이동 불가능합니다.(" + e.getMessage() + ")");
                            }
                        }
                }
            }

            // 루프가 끝났다는건 턴이 끝났다는것
            System.out.println("게임 종료!");
            view.printProperties();

            // 한 판 더 할지 물어보기
            System.out.println("한판 더? (1/0)");
            int om = sc.nextInt();
            if (om == 1) {
                gm.resetGame(); // 게임 재시작 하려면 게임상태 ready로 만들어줘야됨. 그래야 게임판 모양이나 플레이어수 등등 설정 가능
            } else if (om == 0) {
                break;
            }
        }
    }
}
