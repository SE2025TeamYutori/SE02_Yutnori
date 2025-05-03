package org.cau02.model.old;
import org.cau02.view.GameView;

import java.util.*;
import java.util.stream.Collectors;

/** 게임 참가 플레이어 **/
public class Player {
    private static int nextId = 1; // 플레이어 고유 ID 생성을 위한 정적 카운터
    protected final int id;
    protected final String name;
    protected final Color color;
    protected List<Piece> pieces; // 보유한 말 목록
    protected int score = 0; // 완주한 말의 수

    /**
     * 플레이어를 생성합니다.
     * @param name 플레이어 이름
     * @param color 플레이어 색상
     * @param numberOfPieces 보유할 말의 개수 (2-5개)
     */
    public Player(String name, Color color, int numberOfPieces) {
        if (numberOfPieces < 2 || numberOfPieces > 5) {
            throw new IllegalArgumentException("말의 개수는 2개에서 5개 사이여야 합니다.");
        }
        this.id = nextId++;
        this.name = Objects.requireNonNull(name, "플레이어 이름은 null일 수 없습니다.");
        this.color = Objects.requireNonNull(color, "플레이어 색상은 null일 수 없습니다.");
        initializePieces(numberOfPieces);
    }

    private void initializePieces(int numberOfPieces) {
        this.pieces = new ArrayList<>();
        for (int i = 1; i <= numberOfPieces; i++) {
            pieces.add(new Piece(i, this));
        }
    }

    /** 완주한 말의 수를 기반으로 점수를 업데이트합니다. */
    public void updateScore() {
        this.score = (int) pieces.stream().filter(Piece::isFinished).count();
    }

    /** 모든 말을 완주했는지 확인합니다. */
    public boolean hasFinishedAllPieces() {
        // 모든 말 개수와 점수가 같은지 비교
        return pieces.size() == score && score > 0; // 최소 1개는 완주해야 함
    }

    /** 현재 움직일 수 있는 상태의 말 목록을 반환합니다. (집 또는 판 위) */
    public List<Piece> getMovablePieces() {
        return pieces.stream()
                .filter(p -> !p.isFinished())
                .collect(Collectors.toList());
    }

    /** 특정 노드에 위치한 자신의 (완주 안 한) 말들을 반환합니다. */
    public List<Piece> getPiecesAt(PathNode node) {
        if (node == null) return Collections.emptyList();
        return pieces.stream()
                .filter(p -> node.equals(p.getPosition()) && !p.isFinished())
                .collect(Collectors.toList());
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public Color getColor() { return color; }
    public List<Piece> getPieces() { return Collections.unmodifiableList(pieces); }
    public int getScore() { return score; }

    @Override
    public String toString() { return name + "(" + color.getName() + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id; // 고유 ID로 비교
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * 플레이어가 가능한 이동 목록 중에서 하나를 선택하는 방법을 정의합니다.
     *
     * @param possibleMoves 가능한 이동 목록
     * @param view          사용자 입력를 얻기 위한 View 인터페이스
     * @return 선택된 Move 객체 (이동 안 함 또는 불가 시 null)
     */
    public Move chooseMove(List<Move> possibleMoves, GameView view) {
        return null;
    }
}
