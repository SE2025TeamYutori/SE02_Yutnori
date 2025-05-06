package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

//보드 패널
public class BoardPanel extends JPanel {
    private final GameManager gameManager;
    private final int boardSize = 500;
    private final Map<BoardSpace, Point> spaceLocations = new HashMap<>();
    private final Color[] playerColors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE
    };
    
    private Piece selectedPiece = null;
    private Consumer<Piece> onPieceSelected = null;
    private final Map<Yut, java.util.List<BoardSpace>> possibleMoves = new HashMap<>();

    //보드 패널 생성
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


    public void setOnPieceSelected(Consumer<Piece> onPieceSelected) {
        this.onPieceSelected = onPieceSelected;
    }

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

    //현재 표시된 "가능한 이동" 초기화
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
        calculateBoardLayout();
        
        // 보드 공간 그리기
        drawBoardSpaces(g2d);
        
        // 보드 위에 말 그리기
        drawPieces(g2d);
        
        // 선택된 말의 가능한 이동 표시
        drawPossibleMoves(g2d);
        
        g2d.dispose();
    }


    //보드 레이아웃 계산
    // 보드 중앙 기준 반지름 3/4 만큼 떨어진 위치에 공간 배치
    // 보드 완성 전 임시 레이아웃
    private void calculateBoardLayout() {
        spaceLocations.clear();
        
        // 보드 정보 가져오기
        Board board = gameManager.getBoard();
        List<BoardSpace> spaces = board.getSpaces();
        
        // 보드 중앙
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // 보드 반경 계산
        int radius = Math.min(getWidth(), getHeight()) / 3;
        
        // 주 경로 공간 위치 계산
        int mainPathSpaceCount = spaces.size() - 2; // 준비 공간과 도착 공간 제외
        
        for (int i = 0; i < spaces.size(); i++) {
            BoardSpace space = spaces.get(i);
            
            if (space == board.getReadySpace()) {
                // 준비 공간은 하단
                spaceLocations.put(space, new Point(centerX, centerY + radius + 30));
            } else if (space == board.getGoalSpace()) {
                // 도착 공간은 중앙
                spaceLocations.put(space, new Point(centerX, centerY));
            } else {
                // 이 공간의 각도 계산
                double angle = 2 * Math.PI * i / mainPathSpaceCount;
                
                // 위치 계산
                int x = centerX + (int)(radius * Math.sin(angle));
                int y = centerY - (int)(radius * Math.cos(angle));
                
                spaceLocations.put(space, new Point(x, y));
            }
        }
    }


    //보드 공간 그리기
    private void drawBoardSpaces(Graphics2D g2d) {
        if (gameManager == null || gameManager.getBoard() == null) return;
        
        Board board = gameManager.getBoard();
        
        for (Map.Entry<BoardSpace, Point> entry : spaceLocations.entrySet()) {
            BoardSpace space = entry.getKey();
            Point point = entry.getValue();
            
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
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillOval(point.x - 15, point.y - 15, 30, 30);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(point.x - 15, point.y - 15, 30, 30);
                
                // Draw space index
                int index = board.getSpaces().indexOf(space);
                g2d.drawString(String.valueOf(index), point.x - 5, point.y + 5);
            }
        }
    }

    //보드 위에 모든 말 그리기
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
                        // 동일 위치에 여러 말 있을 때 오프셋 계산
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
        }
    }

    //가능한 이동 표시
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