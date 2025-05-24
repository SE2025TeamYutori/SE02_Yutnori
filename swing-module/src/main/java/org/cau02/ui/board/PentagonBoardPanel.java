package org.cau02.ui.board;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import org.cau02.model.*;

public class PentagonBoardPanel extends AbstractBoardPanel {
    private static final double PADDING = 35.0;
    private static final double POLYGON_RADIUS = (SIDE_LENGTH / 2 - PADDING) / Math.sin(3 * Math.PI / 5);

    private static final double COS_THETA = Math.cos(2 * Math.PI / 5);
    private static final double SIN_THETA = Math.sin(2 * Math.PI / 5);

    private static final Point CENTER = new Point(SIDE_LENGTH / 2, (int)(PADDING * 1.2 + POLYGON_RADIUS));
    private final Point[] crossPoints = new Point[5];
    private final List<Integer> crossIndexes = List.of(0, 5, 10, 15, 20, 35);

    public PentagonBoardPanel(GameManager gm) {
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
        Point top = new Point(0, -(int)POLYGON_RADIUS);
        crossPoints[2] = top;
        crossPoints[3] = rotateOnce(crossPoints[2]);
        crossPoints[4] = rotateOnce(crossPoints[3]);
        crossPoints[0] = rotateOnce(crossPoints[4]);
        crossPoints[1] = rotateOnce(crossPoints[0]);

        for (int i = 0; i < 5; i++) {
            crossPoints[i] = new Point(crossPoints[i].x + CENTER.x, crossPoints[i].y + CENTER.y);
        }

        // 테두리 경로
        for (int i = 0; i < 5; i++) {
            Point start = crossPoints[i];
            Point end = crossPoints[(i + 1) % 5];
            double xStep = (end.x - start.x) / 5.0;
            double yStep = (end.y - start.y) / 5.0;
            for (int j = 0; j < 5; j++) {
                points.add(new Point((int)(start.x + xStep * j), (int)(start.y + yStep * j)));
            }
        }

        // 대각선 경로
        for (int i = 0; i < 5; i++) {
            Point start = crossPoints[(i + 1) % 5];
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
        // 다각형 외곽선
        Path2D pentagon = new Path2D.Double();
        pentagon.moveTo(crossPoints[0].x, crossPoints[0].y);
        for (int i = 1; i < 5; i++) {
            pentagon.lineTo(crossPoints[i].x, crossPoints[i].y);
        }
        pentagon.closePath();
        g2.setColor(Color.BLACK);
        g2.draw(pentagon);

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
