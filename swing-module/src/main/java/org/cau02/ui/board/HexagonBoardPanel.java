package org.cau02.ui.board;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import org.cau02.model.*;

public class HexagonBoardPanel extends AbstractBoardPanel {
    private static final double PADDING = 30.0;
    private static final double POLYGON_RADIUS = SIDE_LENGTH / 2 - PADDING;

    private static final double COS_THETA = Math.cos(2 * Math.PI / 6);
    private static final double SIN_THETA = Math.sin(2 * Math.PI / 6);

    private static final Point CENTER = new Point(SIDE_LENGTH / 2, SIDE_LENGTH / 2);
    private final Point[] crossPoints = new Point[6];
    private final List<Integer> crossIndexes = List.of(0, 5, 10, 15, 20, 25, 42);

    public HexagonBoardPanel(GameManager gm) {
        super(gm);
        initializeBoard();
    }

    private Point rotateOnce(Point p) {
        double x = p.x * COS_THETA + p.y * SIN_THETA;
        double y = p.x * -SIN_THETA + p.y * COS_THETA;
        return new Point((int)x, (int)y);
    }

    @Override
    protected void initializeBoardSpaces() {
        Point base = new Point((int) POLYGON_RADIUS, 0);
        crossPoints[1] = base;
        crossPoints[2] = rotateOnce(crossPoints[1]);
        crossPoints[3] = rotateOnce(crossPoints[2]);
        crossPoints[4] = rotateOnce(crossPoints[3]);
        crossPoints[5] = rotateOnce(crossPoints[4]);
        crossPoints[0] = rotateOnce(crossPoints[5]);

        for (int i = 0; i < 6; i++) {
            crossPoints[i] = new Point(crossPoints[i].x + CENTER.x, crossPoints[i].y + CENTER.y);
        }

        // 테두리 경로
        for (int i = 0; i < 6; i++) {
            Point start = crossPoints[i];
            Point end = crossPoints[(i + 1) % 6];
            double xStep = (end.x - start.x) / 5.0;
            double yStep = (end.y - start.y) / 5.0;
            for (int j = 0; j < 5; j++) {
                points.add(new Point((int)(start.x + xStep * j), (int)(start.y + yStep * j)));
            }
        }

        // 대각선 경로
        for (int i = 0; i < 6; i++) {
            Point start = crossPoints[(i + 1) % 6];
            double xStep = (CENTER.x - start.x) / 3.0;
            double yStep = (CENTER.y - start.y) / 3.0;
            for (int j = 1; j <= 2; j++) {
                points.add(new Point((int)(start.x + xStep * j), (int)(start.y + yStep * j)));
            }
        }

        // 중앙
        points.add(new Point(CENTER.x, CENTER.y));
    }

    @Override
    protected void paintBoardShape(Graphics2D g2) {
        // 육각형 외곽선
        Path2D hexagon = new Path2D.Double();
        hexagon.moveTo(crossPoints[0].x, crossPoints[0].y);
        for (int i = 1; i < 6; i++) {
            hexagon.lineTo(crossPoints[i].x, crossPoints[i].y);
        }
        hexagon.closePath();
        g2.setColor(Color.BLACK);
        g2.draw(hexagon);

        // 대각선
        for (Point p : crossPoints) {
            g2.drawLine(p.x, p.y, CENTER.x, CENTER.y);
        }
    }

    @Override
    protected List<Integer> getCrossIndexes() {
        return crossIndexes;
    }
}
