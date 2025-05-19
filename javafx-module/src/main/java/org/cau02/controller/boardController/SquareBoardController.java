package org.cau02.controller.boardController;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.cau02.model.GameManager;

import java.util.ArrayList;
import java.util.List;

public class SquareBoardController extends RegularBoardController {
    private static final double PADDING = 60.0;
    private static final double INNER_SIDE_LENGTH = SIDE_LENGTH - 2 * PADDING;
    private static final double START = PADDING;
    private static final double END = SIDE_LENGTH - PADDING;
    private static final double SPACE_LENGTH = INNER_SIDE_LENGTH / 5;
    private static final double DIAG_SPACE_LENGTH = INNER_SIDE_LENGTH / 6;
    private static final List<Integer> CROSS_INDEXES = new ArrayList<>(List.of(0, 5, 10, 15, 28));

    public SquareBoardController(GameManager gm) {
        super(gm);
    }

    @Override
    protected void initializeBoardPolygon() {
        boardShape = new Rectangle();
        boardShape.setId("boardShape");

        ((Rectangle)boardShape).setX(START);
        ((Rectangle)boardShape).setY(START);
        ((Rectangle)boardShape).setWidth(INNER_SIDE_LENGTH);
        ((Rectangle)boardShape).setHeight(INNER_SIDE_LENGTH);

        mainPanel.getChildren().add(boardShape);
    }

    @Override
    protected void initializeDiagLines() {
        Line diagLine1 = new Line(START, START, END, END);
        Line diagLine2 = new Line(START, END, END, START);

        mainPanel.getChildren().addAll(diagLine1, diagLine2);
    }

    @Override
    protected void initializeBoardSpaces() {
        // 테두리
        for (int i = 0; i <= 5; i++) {
            points.add(new Point2D(END, END - i * SPACE_LENGTH));
        }
        for (int i = 1; i <= 5; i++) {
            points.add(new Point2D(END - i * SPACE_LENGTH, START));
        }
        for (int i = 1; i <= 5; i++) {
            points.add(new Point2D(START, START + i * SPACE_LENGTH));
        }
        for (int i = 1; i <= 4; i++) {
            points.add(new Point2D(START + i * SPACE_LENGTH, END));
        }

        //대각선
        for (int i = 1; i <= 2; i++) {
            points.add(new Point2D(END - i * DIAG_SPACE_LENGTH, START + i * DIAG_SPACE_LENGTH));
        }
        for (int i = 1; i <= 2; i++) {
            points.add(new Point2D(START + i * DIAG_SPACE_LENGTH, START + i * DIAG_SPACE_LENGTH));
        }
        for (int i = 1; i <= 2; i++) {
            points.add(new Point2D(START + i * DIAG_SPACE_LENGTH, END - i * DIAG_SPACE_LENGTH));
        }
        for (int i = 1; i <= 2; i++) {
            points.add(new Point2D(END - i * DIAG_SPACE_LENGTH, END - i * DIAG_SPACE_LENGTH));
        }

        // 중앙
        points.add(new Point2D((START + END) / 2, (START + END) / 2));

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
