package org.cau02.controller.boardController;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.cau02.model.GameManager;

import java.util.ArrayList;
import java.util.List;

public class HexagonBoardController extends RegularBoardController {
    private static final double PADDING = 40.0;
    private static final double POLYGON_RADIUS = SIDE_LENGTH / 2 - PADDING;

    private static final double COS_THETA = Math.cos(2 * Math.PI / 6);
    private static final double SIN_THETA = Math.sin(2 * Math.PI / 6);

    private static final Point2D CENTER_POINT = new Point2D(SIDE_LENGTH / 2, SIDE_LENGTH / 2);
    private static final List<Integer> CROSS_INDEXES = new ArrayList<>(List.of(0, 5, 10, 15, 20, 25, 42));

    private final Point2D[] crossPoints = new Point2D[6];

    public HexagonBoardController(GameManager gm) {
        super(gm);
    }

    private Point2D rotateOnce(Point2D point) {
        double newX = point.getX() * COS_THETA + point.getY() * SIN_THETA;
        double newY = point.getX() * -SIN_THETA + point.getY() * COS_THETA;

        return new Point2D(newX, newY);
    }

    @Override
    protected void initializeBoardPolygon() {
        boardShape = new Polygon();
        boardShape.setId("boardShape");

        crossPoints[1] = new Point2D(POLYGON_RADIUS, 0);
        crossPoints[2] = rotateOnce(crossPoints[1]);
        crossPoints[3] = rotateOnce(crossPoints[2]);
        crossPoints[4] = rotateOnce(crossPoints[3]);
        crossPoints[5] = rotateOnce(crossPoints[4]);
        crossPoints[0] = rotateOnce(crossPoints[5]);

        for (int i = 0; i < 6; i++) {
            crossPoints[i] = new Point2D(crossPoints[i].getX() + CENTER_POINT.getX(), crossPoints[i].getY() + CENTER_POINT.getY());

            ((Polygon)boardShape).getPoints().add(crossPoints[i].getX());
            ((Polygon)boardShape).getPoints().add(crossPoints[i].getY());
        }

        mainPanel.getChildren().add(boardShape);
    }

    @Override
    protected void initializeDiagLines() {
        Line[] diagLines = new Line[6];

        for (int i = 0; i < diagLines.length; i++) {
            diagLines[i] = new Line(crossPoints[i].getX(), crossPoints[i].getY(), CENTER_POINT.getX(), CENTER_POINT.getY());
        }

        mainPanel.getChildren().addAll(diagLines);
    }

    @Override
    protected void initializeBoardSpaces() {
        //테두리
        for (int i = 0; i < 6; i++) {
            Point2D startPoint = crossPoints[i];
            Point2D endPoint = crossPoints[(i + 1) % 6];
            double xStep = (endPoint.getX() - startPoint.getX()) / 5;
            double yStep = (endPoint.getY() - startPoint.getY()) / 5;

            for (int j = 0; j < 5; j++) {
                points.add(new Point2D(crossPoints[i].getX() + xStep * j, crossPoints[i].getY() + yStep * j));
            }
        }

        //대각선
        for (int i = 0; i < 6; i++) {
            Point2D startPoint = crossPoints[(i + 1) % 6];
            Point2D endPoint = CENTER_POINT;
            double xStep = (endPoint.getX() - startPoint.getX()) / 3;
            double yStep = (endPoint.getY() - startPoint.getY()) / 3;

            for (int j = 1; j <= 2; j++) {
                points.add(new Point2D(crossPoints[(i + 1) % 6].getX() + xStep * j, crossPoints[(i + 1) % 6].getY() + yStep * j));
            }
        }

        // 중앙
        points.add(new Point2D(CENTER_POINT.getX(), CENTER_POINT.getY()));

        for (Point2D point : points) {
            Circle circle = new Circle(point.getX(), point.getY(), BOARDSPACE_RADIUS);
            circles.add(circle);
        }

        for (int i : CROSS_INDEXES) {
            circles.get(i).setRadius(BOARDSPACE_RADIUS * 1.5);
            Circle circle = new Circle(points.get(i).getX(), points.get(i).getY(), BOARDSPACE_RADIUS);
            circles.add(circle);
        }

        mainPanel.getChildren().addAll(circles);
    }

}
