import org.cau02.model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class ModelTestObserver implements YutNoriObserver {
    private final GameManager gm;
    private final StringBuilder logBuilder = new StringBuilder();

    ModelTestObserver(GameManager gm) {
        this.gm = gm;
    }

    public void onGameEnded() {
        logBuilder.append("<game ended>\n");
        flush();
    }

    public void onTurnChanged() {
    }

    public void TurnChange(int turn) {
        logBuilder.append("<Turn ")
                .append(turn)
                .append(">: Player ")
                .append(gm.getCurrentPlayer())
                .append(" (Yut Count: ")
                .append(gm.getCurrentYutCount())
                .append(")\n");

        for (int i = 0; i < gm.getPlayerCount(); i++) {
            logBuilder.append("    [Player ").append(i)
                    .append("'s Pieces] Ready: ").append(gm.getReadyPiecesCount(i))
                    .append(", Active: {");
            for (Piece p : gm.getActivePieces(i)) {
                logBuilder.append(gm.getBoard().getSpaces().indexOf(p.getLocation())).append(", ");
            }
            logBuilder.append("}, Goal: ").append(gm.getGoalPiecesCount(i)).append('\n');
        }

        checkFlush();
    }

    public void onYutStateChanged() {
    }

    public void onPieceMoved() {
        logBuilder.append("  [Player ")
                .append(gm.getCurrentPlayer())
                .append("] moves Piece (Yut Count: ")
                .append(gm.getCurrentYutCount())
                .append(")\n");
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            logBuilder.append("    [Player ").append(i)
                    .append("'s Pieces] Ready: ").append(gm.getReadyPiecesCount(i))
                    .append(", Active: {");
            for (Piece p : gm.getActivePieces(i)) {
                logBuilder.append(gm.getBoard().getSpaces().indexOf(p.getLocation())).append(", ");
            }
            logBuilder.append("}, Goal: ").append(gm.getGoalPiecesCount(i)).append('\n');
        }

        checkFlush();
    }

    public void writeYutLog(Yut yut) {
        logBuilder.append("  [Player ")
                .append(gm.getCurrentPlayer())
                .append("] throws Yut: ")
                .append(yut.name())
                .append(" (Yut Count: ")
                .append(gm.getCurrentYutCount())
                .append(")\n");

        logBuilder.append("             ");
        for (Yut y : Yut.values()) {
            logBuilder.append(y.name()).append(": ").append(gm.getYutResult().get(y.ordinal())).append(' ');
        }
        logBuilder.append('\n');

        checkFlush();
    }

    public void writeException(Exception e) {
        logBuilder.append("<Exception>: ").append(e.getMessage()).append('\n');
        flush();
    }

    public void writeStart(int attempt) {
        logBuilder.append("<game started> (attempt ").append(attempt).append(")\n");
        logBuilder.append("    board size: ").append(((RegularBoard)(gm.getBoard())).getBoardAngle())
                .append(", players: ").append(gm.getPlayerCount())
                .append(", pieces: ").append(gm.getPieceCount()).append('\n');

        checkFlush();
    }

    void checkFlush() {
        if (logBuilder.length() > 4096) {
            flush();
        }
    }

    void flush() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logPath/log.txt", true))) {
            writer.write(logBuilder.toString());
            logBuilder.setLength(0); // clear buffer
        } catch (IOException ee) {
            System.err.println("Failed to write log: " + ee.getMessage());
        }
    }
}


public class ModelTest {
    private static final Random random = new Random();
    private static final GameManager gm = new GameManager(4, 2, 2);
    private static final ModelTestObserver observer = new ModelTestObserver(gm);

    static {
        gm.registerObserver(observer);
    }

    ModelTest() {
        gm.registerObserver(observer);
    }

    static void throwRandomYut() {
        observer.writeYutLog(gm.throwRandomYut());
    }

    static void moveRandomNewPiece() {
        List<BoardSpace> moveList = gm.getPossibleLocationsOfNewPiece();

        List<Yut> canYuts = new ArrayList<>();
        for (Yut y : Yut.values()) {
            if (moveList.get(y.ordinal()) != null) {
                canYuts.add(y);
            }
        }

        int index = random.nextInt(canYuts.size());
        gm.moveNewPiece(canYuts.get(index));
    }

    static void moveRandomActivedPiece() {
        List<Piece> ps = new ArrayList<>(gm.getActivePieces(gm.getCurrentPlayer()));
        Piece p = ps.get(random.nextInt(ps.size()));

        List<BoardSpace> moveList = gm.getPossibleLocations(p);

        List<Yut> canYuts = new ArrayList<>();
        for (Yut y : Yut.values()) {
            if (moveList.get(y.ordinal()) != null) {
                canYuts.add(y);
            }
        }

        int index = random.nextInt(canYuts.size());
        gm.movePiece(p, canYuts.get(index));
    }

    static boolean canMoveNewPiece() {
        boolean haveReadyPieces = gm.getReadyPiecesCount(gm.getCurrentPlayer()) > 0;
        int backdoCount = gm.getYutResult().getFirst();

        if (haveReadyPieces && gm.getCurrentMoveCount() != gm.getYutResult().getFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        int counter = 0;

        while (counter < 100) {
            counter++;
            System.out.println("Start attempt " + counter);
            try {
                gm.setBoard(random.nextInt(4, 7));
                gm.setPlayerCount(random.nextInt(2, 5));
                gm.setPieceCount(random.nextInt(2, 6));
                gm.startGame();
                observer.writeStart(counter);
                observer.TurnChange(1);

                int nowTurn = 0;
                int turnCount = 1;

                while (gm.getState() == GameState.PLAYING) {
                    if (nowTurn != gm.getCurrentPlayer()) {
                        turnCount++;
                        observer.TurnChange(turnCount);
                        nowTurn = gm.getCurrentPlayer();
                    }

                    if (gm.getCurrentYutCount() > 0 && gm.getCurrentMoveCount() > 0) {
                        switch (random.nextInt(2)) {
                            case 0:
                                throwRandomYut();
                                break;
                            case 1:
                                if (gm.getReadyPiecesCount(gm.getCurrentPlayer()) > 0 && gm.getCurrentMoveCount() != gm.getYutResult().getFirst() && !gm.getActivePieces(gm.getCurrentPlayer()).isEmpty()) {
                                    switch (random.nextInt(2)) {
                                        case 0:
                                            moveRandomNewPiece();
                                            break;
                                        case 1:
                                            moveRandomActivedPiece();
                                            break;
                                    }
                                } else if (gm.getReadyPiecesCount(gm.getCurrentPlayer()) > 0 && gm.getCurrentMoveCount() != gm.getYutResult().getFirst()) {
                                    moveRandomNewPiece();
                                } else if (!gm.getActivePieces(gm.getCurrentPlayer()).isEmpty()) {
                                    moveRandomActivedPiece();
                                } else {
                                    throw new Exception("윷도 못던지고 움직이지도 못하는 상태에 도달");
                                }
                                break;
                            default:
                                throw new Exception("난수오류");
                        }
                    } else if (gm.getCurrentYutCount() > 0) {
                        throwRandomYut();
                    } else if (gm.getCurrentMoveCount() > 0) {
                        if (gm.getReadyPiecesCount(gm.getCurrentPlayer()) > 0 && gm.getCurrentMoveCount() != gm.getYutResult().getFirst() && !gm.getActivePieces(gm.getCurrentPlayer()).isEmpty()) {
                            switch (random.nextInt(2)) {
                                case 0:
                                    moveRandomNewPiece();
                                    break;
                                case 1:
                                    moveRandomActivedPiece();
                                    break;
                                default:
                                    throw new Exception("난수오류");
                            }
                        } else if (gm.getReadyPiecesCount(gm.getCurrentPlayer()) > 0 && gm.getCurrentMoveCount() != gm.getYutResult().getFirst()) {
                            moveRandomNewPiece();
                        }
                        else if (!gm.getActivePieces(gm.getCurrentPlayer()).isEmpty()) {
                            moveRandomActivedPiece();
                        } else {
                            throw new Exception("윷도 못던지고 움직이지도 못하는 상태에 도달");
                        }
                    } else {
                        throw new Exception("윷도 못던지고 움직이지도 못하는 상태에 도달");
                    }
                }
            } catch (Exception e) {
                observer.writeException(e);
                e.printStackTrace();
            }
            System.out.println("complete attempt " + counter);
            gm.resetGame();
        }
    }
}
