package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * 통합 보드 패널 - N각형 보드를 자동으로 렌더링
 */
public class BoardPanel extends JPanel {
    private final GameManager gameManager;
    private final int boardSize = 500;
    
    // 각 보드 공간에 대한 화면 좌표 맵핑
    private final Map<BoardSpace, Point> spaceLocations = new HashMap<>();
    
    // 공간 간의 연결 정보를 저장하는 맵
    private final Map<BoardSpace, List<BoardSpace>> spaceConnections = new HashMap<>();
    
    private final Color[] playerColors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE
    };
    
    private Piece selectedPiece = null;
    private Consumer<Piece> onPieceSelected = null;
    private final Map<Yut, List<BoardSpace>> possibleMoves = new HashMap<>();

    /**
     * 보드 패널 생성
     * 
     * @param gameManager 게임 관리자
     */
    public BoardPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        setPreferredSize(new Dimension(boardSize, boardSize));
        
        // 말 선택을 위한 마우스 리스너
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    /**
     * 말 선택 콜백 설정
     * 
     * @param onPieceSelected 말 선택 시 호출될 콜백
     */
    public void setOnPieceSelected(Consumer<Piece> onPieceSelected) {
        this.onPieceSelected = onPieceSelected;
    }

    /**
     * 현재 선택된 말 설정
     * 
     * @param piece 선택된 말
     */
    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
        repaint();
    }

    /**
     * 선택된 말의 가능한 이동 위치 표시
     * 
     * @param possibleLocations 윷 타입별 가능한 목적지 공간 맵
     */
    public void showPossibleMoves(Map<Yut, BoardSpace> possibleLocations) {
        possibleMoves.clear();
        
        for (Map.Entry<Yut, BoardSpace> entry : possibleLocations.entrySet()) {
            if (entry.getValue() != null) {
                if (!possibleMoves.containsKey(entry.getKey())) {
                    possibleMoves.put(entry.getKey(), new ArrayList<>());
                }
                possibleMoves.get(entry.getKey()).add(entry.getValue());
            }
        }
        
        repaint();
    }

    /**
     * 현재 표시된 "가능한 이동" 초기화
     */
    public void clearPossibleMoves() {
        possibleMoves.clear();
        repaint();
    }

    /**
     * 보드에서 마우스 클릭 처리
     * 
     * @param x 클릭 위치 x 좌표
     * @param y 클릭 위치 y 좌표
     */
    private void handleMouseClick(int x, int y) {
        if (gameManager == null || gameManager.getState() != GameState.PLAYING || gameManager.getCurrentPlayer() == null) 
            return;
        
        try {
            // 클릭된 위치가 활성 말 근처에 있는지 확인
            for (Piece piece : gameManager.getActivePieces(gameManager.getCurrentPlayer())) {
                BoardSpace location = piece.getLocation();
                Point point = spaceLocations.get(location);
                
                if (point != null && isPointNearPiece(x, y, point.x, point.y)) {
                    selectedPiece = piece;
                    if (onPieceSelected != null) {
                        onPieceSelected.accept(piece);
                    }
                    repaint();
                    return;
                }
            }
        } catch (Exception e) {
            // 예외 무시
        }
    }

    /**
     * 클릭된 위치가 말 근처에 있는지 확인
     */
    private boolean isPointNearPiece(int clickX, int clickY, int pieceX, int pieceY) {
        int pieceRadius = 15;
        int dx = clickX - pieceX;
        int dy = clickY - pieceY;
        return dx * dx + dy * dy <= pieceRadius * pieceRadius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (gameManager == null || gameManager.getBoard() == null) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 보드 레이아웃 계산
        calculateBoardLayout();
        
        // 공간 간의 연결 선 그리기
        drawSpaceConnections(g2d);
        
        // 보드 공간 그리기
        drawBoardSpaces(g2d);
        
        // 보드 위에 말 그리기
        drawPieces(g2d);
        
        // 선택된 말의 가능한 이동 표시
        drawPossibleMoves(g2d);
        
        g2d.dispose();
    }

    /**
     * 보드 레이아웃 계산 - 다각형 수(N)에 따라 자동으로 보드 구성
     */
    private void calculateBoardLayout() {
        spaceLocations.clear();
        spaceConnections.clear();
        
        // 보드 정보 가져오기
        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();
        
        // 다각형 수 가져오기
        int n = 4; // 기본값: 정사각형
        if (board instanceof RegularBoard) {
            n = ((RegularBoard) board).getBoardAngle();
        }
        
        // 패딩 및 반지름 계산
        int padding = 60;
        int diameter = Math.min(getWidth(), getHeight()) - 2 * padding;
        int radius = diameter / 2;
        
        // 중심 좌표
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        
        // 인덱스 매핑 생성
        Map<Integer, BoardSpace> indexedSpaces = new HashMap<>();
        for (int i = 0; i < Math.min(7 * n + 1, spaces.size()); i++) {
            indexedSpaces.put(i, spaces.get(i));
        }
        
        // 중앙 노드 (7*n)
        int centerNodeIdx = 7 * n;
        spaceLocations.put(indexedSpaces.get(centerNodeIdx), center);
        
        // 꼭짓점 노드 배치 (0, 5, 10, 15, ...)
        double startAngle = Math.PI / n;

        for (int v = 0; v < n; v++) {
            int vertexIdx = v * 5;
            double angle = startAngle + (v * 2 * Math.PI / n);
            
            int x = (int) (center.x + radius * Math.sin(angle));
            int y = (int) (center.y + radius * Math.cos(angle));
            
            spaceLocations.put(indexedSpaces.get(vertexIdx), new Point(x, y));
        }
        
        // 변 내부 노드 배치 (각 꼭짓점 사이에 4개의 노드)
        for (int v = 0; v < n; v++) {
            int startIdx = v * 5;
            int endIdx = ((v + 1) % n) * 5;
            
            Point startPoint = spaceLocations.get(indexedSpaces.get(startIdx));
            Point endPoint = spaceLocations.get(indexedSpaces.get(endIdx));
            
            for (int k = 1; k <= 4; k++) {
                double t = k / 5.0; // 0.2, 0.4, 0.6, 0.8
                
                int x = (int) (startPoint.x + t * (endPoint.x - startPoint.x));
                int y = (int) (startPoint.y + t * (endPoint.y - startPoint.y));
                
                spaceLocations.put(indexedSpaces.get(startIdx + k), new Point(x, y));
            }
        }
        
        // 대각 노드 배치 (5*n ~ 7*n-1)
        // 5번 노드부터 시작해서 0번 노드까지 차례로 대각선 노드 배치
        for (int v = 0; v < n; v++) {
            // 현재 처리할 꼭짓점 인덱스 - 5번부터 시작하여 0번까지 (5, 10, 15, ..., 0)
            int vertexIdx = ((v + 1) % n) * 5;
            // 대각선 노드 인덱스 계산
            int outerIdx = 5 * n + 2 * v;     // 꼭짓점에 가까운 대각 노드
            int innerIdx = outerIdx + 1;      // 중앙에 가까운 대각 노드

            Point vertexPoint = spaceLocations.get(indexedSpaces.get(vertexIdx));

            // 꼭짓점과 중심 사이의 1/3, 2/3 지점에 대각 노드 배치
            int x1 = (int) (vertexPoint.x + (center.x - vertexPoint.x) / 3.0);
            int y1 = (int) (vertexPoint.y + (center.y - vertexPoint.y) / 3.0);

            int x2 = (int) (vertexPoint.x + 2 * (center.x - vertexPoint.x) / 3.0);
            int y2 = (int) (vertexPoint.y + 2 * (center.y - vertexPoint.y) / 3.0);

            // 작은 숫자 노드가 꼭짓점과 가깝게 배치
            spaceLocations.put(indexedSpaces.get(outerIdx), new Point(x1, y1));
            spaceLocations.put(indexedSpaces.get(innerIdx), new Point(x2, y2));
        }
        
        // 연결 초기화
        for (int i = 0; i < 7 * n + 1; i++) {
            if (indexedSpaces.containsKey(i)) {
                spaceConnections.put(indexedSpaces.get(i), new ArrayList<>());
            }
        }
        
        // 외곽 연결 (0 ~ 5*n-1)
        for (int i = 0; i < 5 * n; i++) {
            BoardSpace current = indexedSpaces.get(i);
            BoardSpace next = indexedSpaces.get((i + 1) % (5 * n));
            
            spaceConnections.get(current).add(next);
        }
        
        // 중앙 노드 가져오기
        BoardSpace centerSpace = indexedSpaces.get(centerNodeIdx);

        // 대각 연결
        for (int v = 0; v < n; v++) {
            // 현재 처리할 꼭짓점 인덱스 - 5번부터 시작하여 0번까지 (5, 10, 15, ..., 0)
            int vertexIdx = ((v + 1) % n) * 5;
            int outerIdx = 5 * n + 2 * v;     // 꼭짓점에 가까운 대각 노드
            int innerIdx = outerIdx + 1;      // 중앙에 가까운 대각 노드

            BoardSpace vertex = indexedSpaces.get(vertexIdx);
            BoardSpace outer = indexedSpaces.get(outerIdx);
            BoardSpace inner = indexedSpaces.get(innerIdx);

            // 꼭짓점 -> 대각 -> 중앙 연결
            spaceConnections.get(vertex).add(outer);
            spaceConnections.get(outer).add(inner);
            spaceConnections.get(inner).add(centerSpace);

            // 양방향 연결
            spaceConnections.get(outer).add(vertex);
            spaceConnections.get(inner).add(outer);
            spaceConnections.get(centerSpace).add(inner);
        }
        
        // Ready 및 Goal 공간 처리
        if (spaces.size() > 7 * n + 1) {
            // Ready 공간 찾기
            BoardSpace readySpace = board.getReadySpace();
            if (readySpace != null) {
                spaceLocations.put(readySpace, new Point(center.x, center.y + radius + 50));
            }
            
            // Goal 공간 - 중앙에 배치
            BoardSpace goalSpace = board.getGoalSpace();
            if (goalSpace != null) {
                spaceLocations.put(goalSpace, center);
            }
        }
    }

    /**
     * 공간 간의 연결 선 그리기
     */
    private void drawSpaceConnections(Graphics2D g2d) {
        g2d.setColor(new Color(180, 180, 180));
        g2d.setStroke(new BasicStroke(2.0f));
        
        // 모든 연결 그리기
        for (Map.Entry<BoardSpace, List<BoardSpace>> entry : spaceConnections.entrySet()) {
            BoardSpace fromSpace = entry.getKey();
            Point fromPoint = spaceLocations.get(fromSpace);
            
            if (fromPoint != null) {
                for (BoardSpace toSpace : entry.getValue()) {
                    Point toPoint = spaceLocations.get(toSpace);
                    if (toPoint != null) {
                        g2d.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
                    }
                }
            }
        }
    }

    /**
     * 모든 보드 공간 그리기
     */
    private void drawBoardSpaces(Graphics2D g2d) {
        if (gameManager == null || gameManager.getBoard() == null) return;
        
        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();
        
        // 다각형 수 가져오기
        int n = 4; // 기본값: 정사각형
        if (board instanceof RegularBoard) {
            n = ((RegularBoard) board).getBoardAngle();
        }
        
        int centerNodeIdx = 7 * n; // centerNodeIdx 선언 추가
        
        // 모든 공간 그리기
        for (int i = 0; i < Math.min(7 * n + 1, spaces.size()); i++) {
            BoardSpace space = spaces.get(i);
            Point point = spaceLocations.get(space);
            
            if (point != null) {
                boolean isVertex = i % 5 == 0 && i < 5 * n; // 꼭짓점 노드
                boolean isCenter = i == centerNodeIdx;      // 중앙 노드
                
                if (isVertex || isCenter) {
                    // 특별 노드 (꼭짓점, 중앙) - 이중 원 그리기
                    g2d.setColor(isCenter ? new Color(255, 220, 180) : new Color(255, 240, 200));
                    g2d.fillOval(point.x - 20, point.y - 20, 40, 40);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 20, point.y - 20, 40, 40);
                    
                    g2d.setColor(isCenter ? new Color(255, 235, 200) : new Color(255, 250, 230));
                    g2d.fillOval(point.x - 12, point.y - 12, 24, 24);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 12, point.y - 12, 24, 24);
                    
                    // 출발 지점 표시
                    if (i == 0) {
                        g2d.drawString("출발", point.x - 15, point.y + 30);
                    }
                } else {
                    // 일반 노드 그리기
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(point.x - 12, point.y - 12, 24, 24);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 12, point.y - 12, 24, 24);
                }
                
                // 노드 인덱스 그리기
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(i), point.x - 4, point.y + 4);
            }
        }
        
        // Ready 및 Goal 공간 그리기
        if (spaces.size() > 7 * n + 1) {
            BoardSpace readySpace = board.getReadySpace();
            Point readyPoint = spaceLocations.get(readySpace);
            
            if (readyPoint != null) {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(readyPoint.x - 40, readyPoint.y - 20, 80, 40);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(readyPoint.x - 40, readyPoint.y - 20, 80, 40);
                g2d.drawString("Ready", readyPoint.x - 15, readyPoint.y + 5);
            }
            
            // Goal 공간은 중앙과 동일한 위치지만 다른 표현 사용
            BoardSpace goalSpace = board.getGoalSpace();
            Point goalPoint = spaceLocations.get(goalSpace);
            
            if (goalPoint != null && goalSpace != spaces.get(centerNodeIdx)) {
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(goalPoint.x - 25, goalPoint.y - 25, 50, 50);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(goalPoint.x - 25, goalPoint.y - 25, 50, 50);
                g2d.drawString("Goal", goalPoint.x - 15, goalPoint.y + 5);
            }
        }
    }

    /**
     * 보드 위에 모든 말 그리기
     */
    private void drawPieces(Graphics2D g2d) {
        if (gameManager == null || gameManager.getState() != GameState.PLAYING) return;
        
        try {
            for (int player = 0; player < gameManager.getPlayerCount(); player++) {
                // 이 플레이어의 활성 말 가져오기
                Set<Piece> playerPieces = gameManager.getActivePieces(player);
                
                // 위치별 말 그룹화
                Map<BoardSpace, List<Piece>> piecesByLocation = new HashMap<>();
                for (Piece piece : playerPieces) {
                    BoardSpace location = piece.getLocation();
                    if (!piecesByLocation.containsKey(location)) {
                        piecesByLocation.put(location, new ArrayList<>());
                    }
                    piecesByLocation.get(location).add(piece);
                }
                
                // 말 그리기
                for (Map.Entry<BoardSpace, List<Piece>> entry : piecesByLocation.entrySet()) {
                    BoardSpace location = entry.getKey();
                    List<Piece> pieces = entry.getValue();
                    Point point = spaceLocations.get(location);
                    
                    if (point != null) {
                        // 동일 위치에 여러 말이 있을 때 오프셋 계산
                        int offset = pieces.size() > 1 ? 8 : 0;
                        
                        for (int i = 0; i < pieces.size(); i++) {
                            Piece piece = pieces.get(i);
                            int offsetX = (i % 2 == 0) ? -offset : offset;
                            int offsetY = (i / 2 == 0) ? -offset : offset;
                            
                            g2d.setColor(playerColors[piece.getOwner()]);
                            
                            // 선택된 말 강조 표시
                            if (piece == selectedPiece) {
                                g2d.setStroke(new BasicStroke(2.0f));
                                g2d.fillOval(point.x - 10 + offsetX, point.y - 10 + offsetY, 20, 20);
                                g2d.setColor(Color.WHITE);
                                g2d.drawOval(point.x - 10 + offsetX, point.y - 10 + offsetY, 20, 20);
                            } else {
                                g2d.fillOval(point.x - 8 + offsetX, point.y - 8 + offsetY, 16, 16);
                            }
                            
                            // 스트로크 복원
                            g2d.setStroke(new BasicStroke(1.0f));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 예외 무시
        }
    }

    /**
     * 가능한 이동 표시
     */
    private void drawPossibleMoves(Graphics2D g2d) {
        for (Map.Entry<Yut, List<BoardSpace>> entry : possibleMoves.entrySet()) {
            Yut yut = entry.getKey();
            for (BoardSpace space : entry.getValue()) {
                Point point = spaceLocations.get(space);
                if (point != null) {
                    // 이 가능한 이동에 대한 강조 표시
                    g2d.setColor(new Color(255, 255, 0, 128)); // 반투명 노란색
                    g2d.fillOval(point.x - 20, point.y - 20, 40, 40);
                    
                    // 이 이동에 대한 윷 타입 표시
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(yut.name(), point.x - 15, point.y - 25);
                }
            }
        }
    }
}