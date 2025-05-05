package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;


//정사각형 보드 UI
public class SquareBoardUI extends JPanel {
    private final GameManager gameManager;
    private final int boardSize = 500;

    //spaceLocations : 각 공간의 위치를 저장하는 맵
    //spaceLocations.put(space, new Point(x, y));
    private final Map<BoardSpace, Point> spaceLocations = new HashMap<>();


    
    //spaceConnections : 각 공간의 연결 정보를 저장하는 맵
    //spaceConnections.put(indexedSpaces.get(i), new ArrayList<>());
    private final Map<BoardSpace, List<BoardSpace>> spaceConnections = new HashMap<>();
    private final Color[] playerColors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE
    };
    
    private Piece selectedPiece = null;
    private Consumer<Piece> onPieceSelected = null;
    private final Map<Yut, java.util.List<BoardSpace>> possibleMoves = new HashMap<>();

    public SquareBoardUI(GameManager gameManager) {
        this.gameManager = gameManager;
        setPreferredSize(new Dimension(boardSize, boardSize));
        
        //말 선택 이벤트 처리
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    //말 선택 콜백 설정
    public void setOnPieceSelected(Consumer<Piece> onPieceSelected) {
        this.onPieceSelected = onPieceSelected;
    }

    //현재 선택된 말 설정
    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
        repaint();
    }

    /**
     * 선택된 말의 가능한 이동 위치와 윷 타입 표시
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

    //현재 표시된 가능한 이동 초기화
    public void clearPossibleMoves() {
        possibleMoves.clear();
        repaint();
    }

    /**
     * 보드 위에서 마우스 클릭 이벤트 처리
     * 
     * @param x 마우스 클릭 위치 x 좌표
     * @param y 마우스 클릭 위치 y 좌표
     */
    private void handleMouseClick(int x, int y) {
        if (gameManager == null || gameManager.getState() != GameState.PLAYING || gameManager.getCurrentPlayer() == null) 
            return;
        
        try {
            // 활성 말 클릭 여부 확인
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
        }
    }

    /**
     * 클릭된 위치가 말 근처에 있는지 확인
     * 
     * @param clickX 클릭 위치 x 좌표
     * @param clickY 클릭 위치 y 좌표
     * @param pieceX 말 위치 x 좌표
     * @param pieceY 말 위치 y 좌표
     * @return 클릭이 말 반경 내에 있는지 여부
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
        calculateTraditionalBoardLayout();
        
        // 공간 간의 연결 선 먼저 그리기 (공간 뒤에 그리기)
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
     * 사각형 보드 레이아웃 계산 
     */
    private void calculateTraditionalBoardLayout() {
        spaceLocations.clear();
        spaceConnections.clear();
        
        // 보드 정보 가져오기
        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();

        // 사각형 보드 크기 계산
        int padding = 50;
        int squareSize = Math.min(getWidth(), getHeight()) - 2 * padding;
        
        // 사각형 보드 코너 위치 계산
        int left = (getWidth() - squareSize) / 2;
        int top = (getHeight() - squareSize) / 2;
        int right = left + squareSize;
        int bottom = top + squareSize;
        
        // 코너 점 계산
        Point topLeft = new Point(left, top);
        Point topRight = new Point(right, top);
        Point bottomLeft = new Point(left, bottom);
        Point bottomRight = new Point(right, bottom);
        
        // 중앙 점 계산
        Point center = new Point((left + right) / 2, (top + bottom) / 2);
        
        // 공간 저장 맵 생성 (0부터 28까지)
        Map<Integer, BoardSpace> indexedSpaces = new HashMap<>();
        for (int i = 0; i < Math.min(29, spaces.size()); i++) {
            indexedSpaces.put(i, spaces.get(i));
        }
        
        // 코너 공간 배치 (0, 5, 10, 15)
        spaceLocations.put(indexedSpaces.get(0), bottomRight);
        spaceLocations.put(indexedSpaces.get(5), topRight);
        spaceLocations.put(indexedSpaces.get(10), topLeft);
        spaceLocations.put(indexedSpaces.get(15), bottomLeft);
        
        // 가장자리 공간 배치 (1-4, 6-9, 11-14, 16-19)
        // 오른쪽 가장자리 (1-4)
        for (int i = 1; i <= 4; i++) {
            int y = bottomRight.y - (i * squareSize / 5);
            spaceLocations.put(indexedSpaces.get(i), new Point(right, y));
        }
        
        // 위쪽 가장자리 (6-9)
        for (int i = 6; i <= 9; i++) {
            int x = topRight.x - ((i - 5) * squareSize / 5);
            spaceLocations.put(indexedSpaces.get(i), new Point(x, top));
        }
        
        // 왼쪽 가장자리 (11-14)
        for (int i = 11; i <= 14; i++) {
            int y = topLeft.y + ((i - 10) * squareSize / 5);
            spaceLocations.put(indexedSpaces.get(i), new Point(left, y));
        }
        
        // 아래쪽 가장자리 (16-19)
        for (int i = 16; i <= 19; i++) {
            int x = bottomLeft.x + ((i - 15) * squareSize / 5);
            spaceLocations.put(indexedSpaces.get(i), new Point(x, bottom));
        }
        
        // 대각선 공간 배치 (20-27)
        // 코너와 중앙 사이의 거리를 나누어 계산
        
        // 위쪽 오른쪽 (5)에서 중앙 (28)까지 - 노드 20은 코너에 더 가까워, 21은 중앙에 더 가까움
        spaceLocations.put(indexedSpaces.get(20), new Point(
                topRight.x - (topRight.x - center.x) / 3, 
                topRight.y + (center.y - topRight.y) / 3));
        spaceLocations.put(indexedSpaces.get(21), new Point(
                topRight.x - 2 * (topRight.x - center.x) / 3, 
                topRight.y + 2 * (center.y - topRight.y) / 3));
        
        // 위쪽 왼쪽 (10)에서 중앙 (28)까지 - 노드 22, 23 순서대로 꼭짓점에 더 가깝게   
        spaceLocations.put(indexedSpaces.get(22), new Point(
                topLeft.x + (center.x - topLeft.x) / 3, 
                topLeft.y + (center.y - topLeft.y) / 3));
        spaceLocations.put(indexedSpaces.get(23), new Point(
                topLeft.x + 2 * (center.x - topLeft.x) / 3, 
                topLeft.y + 2 * (center.y - topLeft.y) / 3));
        
        // 아래쪽 왼쪽 (15)에서 중앙 (28)까지 - 노드 24, 25 순서대로 꼭짓점에 더 가깝게
        spaceLocations.put(indexedSpaces.get(24), new Point(
                bottomLeft.x + (center.x - bottomLeft.x) / 3, 
                bottomLeft.y - (bottomLeft.y - center.y) / 3));
        spaceLocations.put(indexedSpaces.get(25), new Point(
                bottomLeft.x + 2 * (center.x - bottomLeft.x) / 3, 
                bottomLeft.y - 2 * (bottomLeft.y - center.y) / 3));
        
        // 아래쪽 오른쪽 (0)에서 중앙 (28)까지 - 노드 26, 27 순서대로 꼭짓점에 더 가깝게
        spaceLocations.put(indexedSpaces.get(26), new Point(
                bottomRight.x - (bottomRight.x - center.x) / 3, 
                bottomRight.y - (bottomRight.y - center.y) / 3));
        spaceLocations.put(indexedSpaces.get(27), new Point(
                bottomRight.x - 2 * (bottomRight.x - center.x) / 3, 
                bottomRight.y - 2 * (bottomRight.y - center.y) / 3));
        
        // 중앙 공간 배치 (28)
        spaceLocations.put(indexedSpaces.get(28), center);
        
        // 공간 간의 연결 설정
        setupSpaceConnections(indexedSpaces);
        
        // 남은 공간 처리 (Ready 및 Goal 공간)
        if (spaces.size() > 29) {
            // Ready 및 Goal 공간 찾기
            for (int i = 29; i < spaces.size(); i++) {
                BoardSpace space = spaces.get(i);
                if (space == board.getReadySpace()) {
                    // Ready 공간은 오른쪽 아래 모서리 아래에 있음
                    spaceLocations.put(space, new Point(right - 50, bottom + 50));
                } else if (space == board.getGoalSpace()) {
                    // Goal 공간은 중앙에 있음
                    spaceLocations.put(space, center);
                }
            }
        }
    }
    
    /**
     * 연결선 설정
     * 
     * @param indexedSpaces 공간 인덱스를 BoardSpace 객체로 매핑하는 맵
     */
    private void setupSpaceConnections(Map<Integer, BoardSpace> indexedSpaces) {
        // 연결 맵 초기화
        for (int i = 0; i < 29; i++) {
            spaceConnections.put(indexedSpaces.get(i), new ArrayList<>());
        }
        
        // 외부 정사각형 (0-19) 연결
        for (int i = 0; i < 19; i++) {
            spaceConnections.get(indexedSpaces.get(i)).add(indexedSpaces.get((i + 1) % 20));
        }
        spaceConnections.get(indexedSpaces.get(19)).add(indexedSpaces.get(0));
        
        // 코너 대각선 연결
        // 오른쪽 아래 모서리 (0)에서 대각선 (26, 27) 연결
        spaceConnections.get(indexedSpaces.get(0)).add(indexedSpaces.get(26));
        spaceConnections.get(indexedSpaces.get(26)).add(indexedSpaces.get(27));
        spaceConnections.get(indexedSpaces.get(27)).add(indexedSpaces.get(28));
        
        // 위쪽 오른쪽 모서리 (5)에서 대각선 (20, 21) 연결
        spaceConnections.get(indexedSpaces.get(5)).add(indexedSpaces.get(20));
        spaceConnections.get(indexedSpaces.get(20)).add(indexedSpaces.get(21));
        spaceConnections.get(indexedSpaces.get(21)).add(indexedSpaces.get(28));
        
        // 위쪽 왼쪽 모서리 (10)에서 대각선 (22, 23) 연결
        spaceConnections.get(indexedSpaces.get(10)).add(indexedSpaces.get(22));
        spaceConnections.get(indexedSpaces.get(22)).add(indexedSpaces.get(23));
        spaceConnections.get(indexedSpaces.get(23)).add(indexedSpaces.get(28));
        
        // 아래쪽 왼쪽 모서리 (15)에서 대각선 (24, 25) 연결
        spaceConnections.get(indexedSpaces.get(15)).add(indexedSpaces.get(24));
        spaceConnections.get(indexedSpaces.get(24)).add(indexedSpaces.get(25));
        spaceConnections.get(indexedSpaces.get(25)).add(indexedSpaces.get(28));
        
        // 중앙에서 모든 대각선 연결
        spaceConnections.get(indexedSpaces.get(28)).add(indexedSpaces.get(21));
        spaceConnections.get(indexedSpaces.get(28)).add(indexedSpaces.get(23));
        spaceConnections.get(indexedSpaces.get(28)).add(indexedSpaces.get(25));
        spaceConnections.get(indexedSpaces.get(28)).add(indexedSpaces.get(27));
    }

    /**
     * 공간 간의 연결 그리기
     * 
     * @param g2d 그래픽 컨텍스트
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
     * 
     * @param g2d 그래픽 컨텍스트
     */
    private void drawBoardSpaces(Graphics2D g2d) {
        if (gameManager == null || gameManager.getBoard() == null) return;
        
        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();
        
        for (int i = 0; i < Math.min(29, spaces.size()); i++) {
            BoardSpace space = spaces.get(i);
            Point point = spaceLocations.get(space);
            
            if (point != null) {
                // 공간 유형에 따라 그리기
                if (i == 0 || i == 5 || i == 10 || i == 15) {
                    // 코너 공간 - 이중 원 그리기
                    g2d.setColor(new Color(255, 240, 200)); // 밝은 베이지 색상 사용
                    g2d.fillOval(point.x - 20, point.y - 20, 40, 40);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 20, point.y - 20, 40, 40);
                    g2d.setColor(new Color(255, 250, 230));
                    g2d.fillOval(point.x - 12, point.y - 12, 24, 24);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 12, point.y - 12, 24, 24);
                    
                    // 출발 지점에 "출발" 레이블 추가
                    if (i == 0) {
                        g2d.drawString("출발", point.x - 15, point.y + 30);
                    }
                } else if (i == 28) {
                    // 중앙 공간 - 코너 공간과 유사한 이중 원 그리기
                    g2d.setColor(new Color(255, 220, 180)); // 밝은 주황색
                    g2d.fillOval(point.x - 20, point.y - 20, 40, 40);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 20, point.y - 20, 40, 40);
                    g2d.setColor(new Color(255, 235, 200));
                    g2d.fillOval(point.x - 12, point.y - 12, 24, 24);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 12, point.y - 12, 24, 24);
                } else {
                    // 일반 공간
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(point.x - 12, point.y - 12, 24, 24);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(point.x - 12, point.y - 12, 24, 24);
                }
                
                // 공간 인덱스 그리기
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(i), point.x - 4, point.y + 4);
            }
        }
        
        // 준비 및 목표 공간이 존재하는 경우 그리기
        if (spaces.size() > 29) {
            for (int i = 29; i < spaces.size(); i++) {
                BoardSpace space = spaces.get(i);
                Point point = spaceLocations.get(space);
                
                if (point != null) {
                    if (space == board.getReadySpace()) {
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.fillRect(point.x - 40, point.y - 20, 80, 40);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(point.x - 40, point.y - 20, 80, 40);
                        g2d.drawString("Ready", point.x - 15, point.y + 5);
                    } else if (space == board.getGoalSpace()) {
                        g2d.setColor(Color.YELLOW);
                        g2d.fillOval(point.x - 25, point.y - 25, 50, 50);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(point.x - 25, point.y - 25, 50, 50);
                        g2d.drawString("Goal", point.x - 15, point.y + 5);
                    }
                }
            }
        }
    }

    /**
     * 보드 위에 모든 말 그리기
     * 
     * @param g2d 그래픽 컨텍스트
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
                        // 동일 위치에 여러 말 그리기 위한 오프셋 계산
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
            // 렌더링 중 예외 무시
        }
    }

    /**
     * 가능한 이동 표시
     * 
     * @param g2d 그래픽 컨텍스트
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
                    
                    // 이 이동에 대한 윷 타입 그리기
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(yut.name(), point.x - 15, point.y - 25);
                }
            }
        }
    }
}