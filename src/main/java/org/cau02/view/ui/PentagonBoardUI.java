package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;


public class PentagonBoardUI extends JPanel {
    // 기본 필드
    private final GameManager gameManager;
    private final int boardSize = 500;

    // 노드 좌표 ↔ BoardSpace 매핑
    private final Map<BoardSpace, Point> spaceLocations = new HashMap<>();
    private final Map<BoardSpace, List<BoardSpace>> spaceConnections = new HashMap<>();

    private final Color[] playerColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

    private Piece selectedPiece = null;
    private Consumer<Piece> onPieceSelected = null;
    private final Map<Yut, List<BoardSpace>> possibleMoves = new HashMap<>();


    public PentagonBoardUI(GameManager gameManager) {
        this.gameManager = gameManager;
        setPreferredSize(new Dimension(boardSize, boardSize));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleMouseClick(e.getX(), e.getY()); }
        });
    }


    public void setOnPieceSelected(Consumer<Piece> cb) { this.onPieceSelected = cb; }
    public void setSelectedPiece(Piece p) { this.selectedPiece = p; repaint(); }

    public void showPossibleMoves(Map<Yut, BoardSpace> map) {
        possibleMoves.clear();
        map.forEach((y, s) -> { if (s != null) possibleMoves.computeIfAbsent(y, _ -> new ArrayList<>()).add(s); });
        repaint();
    }
    public void clearPossibleMoves() { possibleMoves.clear(); repaint(); }

    // 렌더링
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameManager == null || gameManager.getBoard() == null) return;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        calculatePentagonBoardLayout();
        drawSpaceConnections(g2d);
        drawBoardSpaces(g2d);
        drawPieces(g2d);
        drawPossibleMoves(g2d);
        g2d.dispose();
    }


    private void calculatePentagonBoardLayout() {
        spaceLocations.clear();
        spaceConnections.clear();

        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();

        // 인덱스 매핑 0‑35
        Map<Integer, BoardSpace> idx = new HashMap<>();
        for (int i = 0; i < Math.min(36, spaces.size()); i++) idx.put(i, spaces.get(i));

        //꼭짓점 (0,5,10,15,20)
        int padding = 60;
        int d = Math.min(getWidth(), getHeight()) - 2 * padding;
        int r = d / 2;
        Point center = new Point(getWidth() / 2, getHeight() / 2);

        // 54°부터 반시계방향 72° 간격 → 하단 변 수평 (54°,126°가 하단 두 꼭짓점)
        double startRad = Math.toRadians(54);
        for (int v = 0; v < 5; v++) {
            double ang = startRad - v * 2 * Math.PI / 5;
            int x = (int) (center.x + r * Math.cos(ang));
            int y = (int) (center.y + r * Math.sin(ang));
            spaceLocations.put(idx.get(v * 5), new Point(x, y));
        }

        //각 변 내부 4 노드
        for (int v = 0; v < 5; v++) {
            int vs = v * 5;
            int ve = ((v + 1) % 5) * 5;
            Point ps = spaceLocations.get(idx.get(vs));
            Point pe = spaceLocations.get(idx.get(ve));
            for (int k = 1; k <= 4; k++) {
                double t = k / 5.0; // 0.2,0.4,0.6,0.8
                int x = (int) (ps.x + t * (pe.x - ps.x));
                int y = (int) (ps.y + t * (pe.y - ps.y));
                spaceLocations.put(idx.get(vs + k), new Point(x, y));
            }
        }

        //꼭짓점-중앙 사이 노드
        int[][] spokes = {
                {5, 25, 26},
                {10, 27, 28},
                {15, 29, 30},
                {20, 31, 32},
                {0, 33, 34}
        };
        for (int[] s : spokes) {
            Point vp = spaceLocations.get(idx.get(s[0]));
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0; // 1/3·2/3
                int x = (int) (vp.x + (center.x - vp.x) * t);
                int y = (int) (vp.y + (center.y - vp.y) * t);
                spaceLocations.put(idx.get(s[j]), new Point(x, y));
            }
        }
        spaceLocations.put(idx.get(35), center);

        //연결선
        spaceLocations.keySet().forEach(bs -> spaceConnections.put(bs, new ArrayList<>()));
        // 외곽 링
        for (int i = 0; i < 25; i++) {
            BoardSpace a = idx.get(i);
            BoardSpace b = idx.get((i + 1) % 25);
            spaceConnections.get(a).add(b);
        }
        // spokes
        for (int[] s : spokes) {
            BoardSpace v = idx.get(s[0]);
            BoardSpace n1 = idx.get(s[1]);
            BoardSpace n2 = idx.get(s[2]);
            BoardSpace c = idx.get(35);
            spaceConnections.get(v).add(n1);
            spaceConnections.get(n1).add(n2);
            spaceConnections.get(n2).add(c);
            spaceConnections.get(n1).add(v);
            spaceConnections.get(n2).add(n1);
            spaceConnections.get(c).add(n2);
        }
    }


    private void drawSpaceConnections(Graphics2D g2d) {
        g2d.setColor(new Color(180, 180, 180));
        g2d.setStroke(new BasicStroke(2f));
        spaceConnections.forEach((from, tos) -> {
            Point pf = spaceLocations.get(from);
            tos.forEach(to -> {
                Point pt = spaceLocations.get(to);
                if (pf != null && pt != null) g2d.drawLine(pf.x, pf.y, pt.x, pt.y);
            });
        });
    }

    private void drawBoardSpaces(Graphics2D g2d) {
        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();
        for (int i = 0; i < Math.min(36, spaces.size()); i++) {
            BoardSpace sp = spaces.get(i);
            Point p = spaceLocations.get(sp);
            if (p == null) continue;
            boolean isVertex = i % 5 == 0 && i <= 20;
            boolean isCenter = i == 35;

            if (isVertex || isCenter) {
                g2d.setColor(isCenter ? new Color(255, 220, 180) : new Color(255, 240, 200));
                g2d.fillOval(p.x - 20, p.y - 20, 40, 40);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - 20, p.y - 20, 40, 40);
                g2d.setColor(isCenter ? new Color(255, 235, 200) : new Color(255, 250, 230));
                g2d.fillOval(p.x - 12, p.y - 12, 24, 24);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - 12, p.y - 12, 24, 24);
                if (i == 0) g2d.drawString("출발", p.x - 15, p.y + 30);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillOval(p.x - 12, p.y - 12, 24, 24);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x - 12, p.y - 12, 24, 24);
            }
            g2d.drawString(String.valueOf(i), p.x - 4, p.y + 4);
        }
    }

    private void drawPieces(Graphics2D g2d) {
        if (gameManager == null || gameManager.getState() != GameState.PLAYING) return;
        try {
            for (int player = 0; player < gameManager.getPlayerCount(); player++) {
                Map<BoardSpace, List<Piece>> grouped = new HashMap<>();
                for (Piece pc : gameManager.getActivePieces(player)) {
                    grouped.computeIfAbsent(pc.getLocation(), k -> new ArrayList<>()).add(pc);
                }
                grouped.forEach((sp, list) -> {
                    Point p = spaceLocations.get(sp);
                    if (p == null) return;
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
        } catch (Exception ignored) {}
    }

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
    private boolean isNear(int cx, int cy, int px, int py) { int dx = cx - px, dy = cy - py; return dx * dx + dy * dy <= 225; }
}
