package org.cau02.controller.boardController;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.cau02.model.GameManager;

import java.util.ArrayList;
import java.util.List;

public class SquareBoardController extends BoardController {
    private static final double PADDING = 50.0;
    private static final double SIDE_LENGTH = 500.0;
    private static final double INNER_SIDE_LENGTH = SIDE_LENGTH - 2 * PADDING;
    private static final double RADIUS = 20.0;
    private static final double START = PADDING;
    private static final double END = SIDE_LENGTH - PADDING;
    private static final double SPACE_LENGTH = INNER_SIDE_LENGTH / 5;
    private static final double DIAG_SPACE_LENGTH = INNER_SIDE_LENGTH / 6;
    private static final List<Integer> CROSS_INDEXES = new ArrayList<>(List.of(0, 5, 10, 15, 28));

    @FXML private Rectangle boardRect;
    @FXML private Line diagLine1;
    @FXML private Line diagLine2;

    public SquareBoardController(GameManager gm) {
        super(gm);
    }

    private void initializeBoardRect() {
        boardRect.setX(START);
        boardRect.setY(START);
        boardRect.setWidth(INNER_SIDE_LENGTH);
        boardRect.setHeight(INNER_SIDE_LENGTH);
    }

    private void initializeDiagLines() {
        diagLine1.setStartX(START);
        diagLine1.setStartY(START);
        diagLine1.setEndX(END);
        diagLine1.setEndY(END);

        diagLine2.setStartX(START);
        diagLine2.setStartY(END);
        diagLine2.setEndX(END);
        diagLine2.setEndY(START);
    }

    private void initializeBoardSpaces() {
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
            Circle circle = new Circle(point.getX(), point.getY(), RADIUS);
            circles.add(circle);
        }

        for (int i : CROSS_INDEXES) {
            circles.get(i).setRadius(RADIUS + 10);
            Circle circle = new Circle(points.get(i).getX(), points.get(i).getY(), RADIUS);
            circles.add(circle);
        }

        mainPanel.getChildren().addAll(circles);
    }

    @FXML
    private void initialize() {
        mainPanel.setPrefSize(SIDE_LENGTH, SIDE_LENGTH);

        initializeBoardRect();
        initializeDiagLines();
        initializeBoardSpaces();
    }



}
