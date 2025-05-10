package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;


public class HexagonBoardUI extends JPanel {

    /* ────────── 필드 ────────── */
    private final GameManager gameManager;
    private final int boardSize = 500;                      // 기본 선호 사이즈

    private final Map<BoardSpace, Point> spaceLocations = new HashMap<>();
    private final Map<BoardSpace, List<BoardSpace>> spaceConnections = new HashMap<>();

    private final Color[] playerColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

    private Piece selectedPiece = null;
    private Consumer<Piece> onPieceSelected;
    private final Map<Yut, List<BoardSpace>> possibleMoves = new HashMap<>();


    /* ────────── 생성자 ────────── */
    public HexagonBoardUI(GameManager gameManager) {
        this.gameManager = gameManager;
        setPreferredSize(new Dimension(boardSize, boardSize));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleMouseClick(e.getX(), e.getY()); }
        });
    }


    /* ────────── 외부 제어용 메서드 ────────── */
    public void setOnPieceSelected(Consumer<Piece> cb)       { this.onPieceSelected = cb; }
    public void setSelectedPiece(Piece p)                    { this.selectedPiece = p; repaint(); }

    public void showPossibleMoves(Map<Yut, BoardSpace> map) {
        possibleMoves.clear();
        map.forEach((y, s) -> { if (s != null) possibleMoves.computeIfAbsent(y, _ -> new ArrayList<>()).add(s); });
        repaint();
    }
    public void clearPossibleMoves() { possibleMoves.clear(); repaint(); }


    /* ────────── 렌더링 엔트리 ────────── */
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameManager == null || gameManager.getBoard() == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        calculateHexagonBoardLayout();
        drawSpaceConnections(g2d);
        drawBoardSpaces(g2d);
        drawPieces(g2d);
        drawPossibleMoves(g2d);

        g2d.dispose();
    }


	//레이아웃 계산
    private void calculateHexagonBoardLayout() {
        spaceLocations.clear();
        spaceConnections.clear();

        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();
        if (spaces.size() < 43) return;

        /* 인덱스 ↔ BoardSpace */
        Map<Integer, BoardSpace> idx = new HashMap<>();
        for (int i = 0; i < 43; i++) idx.put(i, spaces.get(i));

        /* 중심·반경 */
        int padding = 60;
        int d = Math.min(getWidth(), getHeight()) - 2 * padding;
        int r = d / 2;
        Point center = new Point(getWidth() / 2, getHeight() / 2);

        /* 1️⃣ 꼭짓점 0,5,10,15,20,25 */
        double startDeg = 60;
        for (int v = 0; v < 6; v++) {
            double ang = Math.toRadians(startDeg - v * 60.0);   // 시계 방향
            int x = (int) (center.x + r * Math.cos(ang));
            int y = (int) (center.y + r * Math.sin(ang));
            spaceLocations.put(idx.get(v * 5), new Point(x, y));
        }

        /* 2️⃣ 변 내부 4 노드 */
        for (int side = 0; side < 6; side++) {
            int vs = side * 5, ve = ((side + 1) % 6) * 5;
            Point ps = spaceLocations.get(idx.get(vs));
            Point pe = spaceLocations.get(idx.get(ve));
            for (int k = 1; k <= 4; k++) {
                double t = k / 5.0;
                spaceLocations.put(idx.get(vs + k),
                    new Point((int)(ps.x + t*(pe.x-ps.x)), (int)(ps.y + t*(pe.y-ps.y))));
            }
        }

        /* 3️⃣ 방사형(12)  —  새 번호 매핑 */
        int[] outerRad = { 40, 30, 32, 34, 36, 38 };   // vertex 0,5,10,15,20,25
        for (int k = 0; k < 6; k++) {
            int vIdx = k * 5;
            int outerIdx = outerRad[k];     // 바깥쪽
            int innerIdx = outerIdx + 1;    // 안쪽

            Point vp = spaceLocations.get(idx.get(vIdx));
            int x1 = (int) (vp.x + (center.x - vp.x) / 3.0);
            int y1 = (int) (vp.y + (center.y - vp.y) / 3.0);
            int x2 = (int) (vp.x + 2 * (center.x - vp.x) / 3.0);
            int y2 = (int) (vp.y + 2 * (center.y - vp.y) / 3.0);

            spaceLocations.put(idx.get(outerIdx), new Point(x1, y1));
            spaceLocations.put(idx.get(innerIdx), new Point(x2, y2));
        }

        /* 4️⃣ 중앙 */
        spaceLocations.put(idx.get(42), center);

        /* 연결(외곽·방사선) … 기존 코드 동일 */
        spaceLocations.keySet().forEach(bs -> spaceConnections.put(bs, new ArrayList<>()));
        for (int i = 0; i < 30; i++) {
            BoardSpace a = idx.get(i), b = idx.get((i + 1) % 30);
            spaceConnections.get(a).add(b);  spaceConnections.get(b).add(a);
        }
        for (int k = 0; k < 6; k++) {
            int vIdx = k * 5;
            int outerIdx = outerRad[k], innerIdx = outerIdx + 1;
            BoardSpace v = idx.get(vIdx), r1 = idx.get(outerIdx), r2 = idx.get(innerIdx), c = idx.get(42);
            spaceConnections.get(v).add(r1); spaceConnections.get(r1).add(v);
            spaceConnections.get(r1).add(r2); spaceConnections.get(r2).add(r1);
            spaceConnections.get(r2).add(c); spaceConnections.get(c).add(r2);
        }
    }


    /* ────────── (1) 선분 그리기 ────────── */
    private void drawSpaceConnections(Graphics2D g2d) {
        g2d.setColor(new Color(180, 180, 180));
        g2d.setStroke(new BasicStroke(2f));
        spaceConnections.forEach((from, tos) -> {
            Point pf = spaceLocations.get(from);
            if (pf == null) return;
            tos.forEach(to -> {
                Point pt = spaceLocations.get(to);
                if (pt != null) g2d.drawLine(pf.x, pf.y, pt.x, pt.y);
            });
        });
    }


    /* ────────── (2) 노드(원) 그리기 ────────── */
    private void drawBoardSpaces(Graphics2D g2d) {
        List<BoardSpace> spaces = gameManager.getBoard().getSpaces();

        for (int i = 0; i < 43; i++) {
            BoardSpace sp = spaces.get(i);
            Point p = spaceLocations.get(sp);
            if (p == null) continue;

            boolean isVertex = (i % 5 == 0 && i <= 25);
            boolean isCenter = (i == 42);

            if (isVertex || isCenter) {                        // 이중 원
                g2d.setColor(isCenter ? new Color(255, 220, 180) : new Color(255, 240, 200));
                g2d.fillOval(p.x - 20, p.y - 20, 40, 40);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - 20, p.y - 20, 40, 40);

                g2d.setColor(isCenter ? new Color(255, 235, 200) : new Color(255, 250, 230));
                g2d.fillOval(p.x - 12, p.y - 12, 24, 24);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - 12, p.y - 12, 24, 24);

                if (i == 0) g2d.drawString("출발", p.x - 15, p.y + 30);
            } else {                                           // 단일 원
                g2d.setColor(Color.WHITE);
                g2d.fillOval(p.x - 12, p.y - 12, 24, 24);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - 12, p.y - 12, 24, 24);
            }
            g2d.drawString(String.valueOf(i), p.x - 4, p.y + 4);
        }
    }


    /* ────────── (3) 말 그리기 ────────── */
    private void drawPieces(Graphics2D g2d) {
        if (gameManager == null || gameManager.getState() != GameState.PLAYING) return;

        for (int player = 0; player < gameManager.getPlayerCount(); player++) {
            Map<BoardSpace, List<Piece>> grouped = new HashMap<>();
            for (Piece pc : gameManager.getActivePieces(player)) {
                grouped.computeIfAbsent(pc.getLocation(), _ -> new ArrayList<>()).add(pc);
            }

            grouped.forEach((sp, list) -> {
                Point p = spaceLocations.get(sp); if (p == null) return;
                int off = list.size() > 1 ? 8 : 0;

                for (int i = 0; i < list.size(); i++) {
                    Piece pc = list.get(i);
                    int dx = (i % 2 == 0) ? -off : off;
                    int dy = (i / 2 == 0) ? -off : off;

                    g2d.setColor(playerColors[pc.getOwner()]);
                    if (pc == selectedPiece) {
                        g2d.setStroke(new BasicStroke(2));
                        g2d.fillOval(p.x - 10 + dx, p.y - 10 + dy, 20, 20);
                        g2d.setColor(Color.WHITE);
                        g2d.drawOval(p.x - 10 + dx, p.y - 10 + dy, 20, 20);
                    } else {
                        g2d.fillOval(p.x - 8 + dx, p.y - 8 + dy, 16, 16);
                    }
                    g2d.setStroke(new BasicStroke(1));
                }
            });
        }
    }


    /* ────────── (4) 이동 가능 노드 하이라이트 ────────── */
    private void drawPossibleMoves(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0, 128));
        possibleMoves.forEach((yut, list) -> {
            for (BoardSpace sp : list) {
                Point p = spaceLocations.get(sp);
                if (p != null) {
                    g2d.fillOval(p.x - 20, p.y - 20, 40, 40);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(yut.name(), p.x - 15, p.y - 25);
                    g2d.setColor(new Color(255, 255, 0, 128));
                }
            }
        });
    }


    /* ────────── 마우스 선택 처리 ────────── */
    private void handleMouseClick(int x, int y) {
        if (gameManager == null || gameManager.getState() != GameState.PLAYING || gameManager.getCurrentPlayer() == null) return;

        for (Piece pc : gameManager.getActivePieces(gameManager.getCurrentPlayer())) {
            Point p = spaceLocations.get(pc.getLocation());
            if (p != null && isNear(x, y, p.x, p.y)) {
                selectedPiece = pc;
                if (onPieceSelected != null) onPieceSelected.accept(pc);
                repaint();
                return;
            }
        }
    }
    private boolean isNear(int cx, int cy, int px, int py) {
        int dx = cx - px, dy = cy - py;
        return dx * dx + dy * dy <= 225;
    }
}
