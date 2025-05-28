package org.cau02.ui.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import org.cau02.model.*;

public abstract class AbstractBoardPanel extends JPanel {
    protected static final int SIDE_LENGTH = 400;
    protected static final double BOARDSPACE_RADIUS = 10.0;

    protected final GameManager gm;
    protected final List<Point> points = new ArrayList<>();
    protected final List<JButton> moveButtons = new ArrayList<>();
    protected final List<PieceComponent> pieceComponents = new ArrayList<>();
    
    protected static class PieceComponent {
        protected final Point location;
        protected final int playerId;
        protected final Piece piece;
        protected final int stackCount;
        
        public PieceComponent(Point location, int playerId, Piece piece, int stackCount) {
            this.location = location;
            this.playerId = playerId;
            this.piece = piece;
            this.stackCount = stackCount;
        }
    }

    public AbstractBoardPanel(GameManager gm) {
        this.gm = gm;
        setPreferredSize(new Dimension(SIDE_LENGTH, SIDE_LENGTH));
        setBackground(Color.WHITE);
        setLayout(null);
        setupMouseListener();
    }
    
    protected final void initializeBoard() {
        initializeBoardSpaces();
    }

    protected abstract void initializeBoardSpaces();
    protected abstract List<Integer> getCrossIndexes();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 보드 형태 그리기 (구체 클래스에서 구현)
        paintBoardShape(g2);

        // 원형 말칸 그리기
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            double radius = getCrossIndexes().contains(i) ? BOARDSPACE_RADIUS * 1.5 : BOARDSPACE_RADIUS;
            Shape circle = new Ellipse2D.Double(p.x - radius, p.y - radius, radius * 2, radius * 2);
            g2.setColor(Color.WHITE);
            g2.fill(circle);
            g2.setColor(Color.BLACK);
            g2.draw(circle);
        }
        
        // 말 그리기
        for (PieceComponent pc : pieceComponents) {
            g2.setColor(getColorForPlayer(pc.playerId));
            g2.fillOval(pc.location.x - 15, pc.location.y - 15, 30, 30);
            g2.setColor(Color.BLACK);
            g2.drawOval(pc.location.x - 15, pc.location.y - 15, 30, 30);
            
            if (pc.stackCount > 1) {
                // 파란색 말의 경우 흰색 텍스트, 다른 색은 검은색 텍스트
                Color textColor = (pc.playerId == 1) ? Color.WHITE : Color.BLACK;
                g2.setColor(textColor);
                g2.setFont(new Font("맑은 고딕", Font.BOLD, 12));
                String stackText = "x" + pc.stackCount;
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(stackText);
                g2.drawString(stackText, pc.location.x - textWidth/2, pc.location.y + 5);
            }
        }
    }

    protected abstract void paintBoardShape(Graphics2D g2);
    
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickPoint = e.getPoint();
                for (PieceComponent pc : pieceComponents) {
                    if (isPointNearPiece(clickPoint, pc.location)) {
                        if (pc.playerId == gm.getCurrentPlayer()) {
                            showPossibleMovesForPiece(pc.piece);
                        }
                        return;
                    }
                }
            }
        });
    }
    
    private boolean isPointNearPiece(Point click, Point pieceLocation) {
        return Math.abs(click.x - pieceLocation.x) <= 15 && Math.abs(click.y - pieceLocation.y) <= 15;
    }
    
    private void showPossibleMovesForPiece(Piece piece) {
        clearMoveButtons();
        
        List<BoardSpace> locations = gm.getPossibleLocations(piece);
        
        for (int i = 0; i < 6; i++) {
            if (locations.get(i) != null) {
                int index = gm.getBoard().getSpaces().indexOf(locations.get(i));
                
                JButton button;
                if (index == -1) {
                    button = new JButton("도착!");
                    button.setBounds(200, 350, 80, 25);
                    button.setBackground(Color.ORANGE);
                    button.setForeground(Color.BLACK);
                } else {
                    Point buttonLocation = points.get(index);
                    button = new JButton("이동");
                    button.setBounds(buttonLocation.x - 25, buttonLocation.y - 35, 50, 25);
                    button.setBackground(Color.BLUE);
                    button.setForeground(Color.WHITE);
                }
                
                button.setFont(new Font("Arial", Font.BOLD, 12));
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);
                
                // 버튼 테두리 강조
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                int finalI = i;
                button.addActionListener(e -> {
                    gm.movePiece(piece, Yut.values()[finalI]);
                    clearMoveButtons();
                    updateBoard();
                });
                
                moveButtons.add(button);
                add(button);
            }
        }
        repaint();
    }
    
    public void showPossibleLocationsForNewPiece() {
        clearMoveButtons();
        
        List<BoardSpace> locations = gm.getPossibleLocationsOfNewPiece();
        
        for (int i = 0; i < 6; i++) {
            if (locations.get(i) != null) {
                int index = gm.getBoard().getSpaces().indexOf(locations.get(i));
                Point buttonLocation = points.get(index);
                
                JButton button = new JButton("출발");
                button.setBounds(buttonLocation.x - 25, buttonLocation.y - 35, 50, 25);
                button.setFont(new Font("Arial", Font.BOLD, 12));
                button.setBackground(Color.GREEN);
                button.setForeground(Color.BLACK);
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);
                
                // 버튼 테두리 강조
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                
                int finalI = i;
                button.addActionListener(e -> {
                    gm.moveNewPiece(Yut.values()[finalI]);
                    clearMoveButtons();
                    updateBoard();
                });
                
                moveButtons.add(button);
                add(button);
            }
        }
        repaint();
    }
    
    private void clearMoveButtons() {
        for (JButton button : moveButtons) {
            remove(button);
        }
        moveButtons.clear();
    }
    
    public void updateBoard() {
        pieceComponents.clear();
        
        try {
            for (int i = 0; i < gm.getPlayerCount(); i++) {
                for (Piece p : gm.getActivePieces(i)) {
                    if (p.getState() == PieceState.ACTIVE) {
                        int index = gm.getBoard().getSpaces().indexOf(p.getLocation());
                        Point pieceLocation = points.get(index);
                        int stackCount = p.getCarries().size() + 1;
                        
                        pieceComponents.add(new PieceComponent(pieceLocation, i, p, stackCount));
                    }
                }
            }
        } catch (IllegalStateException e) {
            // 게임이 아직 시작되지 않은 경우 무시
        }
        repaint();
    }
    
    private Color getColorForPlayer(int id) {
        return switch (id) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.MAGENTA;
            default -> Color.GRAY;
        };
    }
}