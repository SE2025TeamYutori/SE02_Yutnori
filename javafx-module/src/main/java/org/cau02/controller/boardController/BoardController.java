package org.cau02.controller.boardController;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import org.cau02.model.*;

import java.util.ArrayList;
import java.util.List;

public abstract class BoardController {
    protected final GameManager gm;

    protected final List<Point2D> points = new ArrayList<>();
    protected final List<Circle> circles = new ArrayList<>();

    private final List<Button> markers = new ArrayList<>();
    private final List<Node> pieces = new ArrayList<>();

    @FXML
    protected AnchorPane mainPanel;

    BoardController(GameManager gm) {
        this.gm = gm;
    }

    public void showPossibleLocationsOfNewPiece() {
        mainPanel.getChildren().removeAll(markers);
        markers.clear();

        List<BoardSpace> locations = gm.getPossibleLocationsOfNewPiece();

        for (int i = 0; i < 6; i++) {
            if (locations.get(i) != null) {
                int index = gm.getBoard().getSpaces().indexOf(locations.get(i));

                Button button = new Button("여기로 이동");
                AnchorPane.setTopAnchor(button, points.get(index).getY());
                AnchorPane.setLeftAnchor(button, points.get(index).getX() - 50);
                button.setId("possibleLocation");
                int finalI = i;
                button.setOnAction(event -> {
                    gm.moveNewPiece(Yut.values()[finalI]);
                    mainPanel.getChildren().removeAll(markers);
                    markers.clear();
                    updateBoard();
                });

                markers.add(button);
                mainPanel.getChildren().add(button);
            }
        }
    }

    public void updateBoard() {
        mainPanel.getChildren().removeAll(pieces);
        pieces.clear();

        for (int i = 0; i < gm.getPlayerCount(); i++) {
            for (Piece p : gm.getActivePieces(i)) {
                if (p.getState() == PieceState.ACTIVE) {
                    int index = gm.getBoard().getSpaces().indexOf(p.getLocation());

                    Circle circle = new Circle(points.get(index).getX(), points.get(index).getY(), 20);
                    circle.setId("player" + i);
                    circle.setOnMouseClicked(event -> {
                        if (Integer.parseInt(circle.getId().replace("player", "")) == gm.getCurrentPlayer()) {
                            showPossibleLocations(p);
                        }
                    });

                    pieces.add(circle);

                    if (!p.getCarries().isEmpty()) {
                        Label label = new Label("x" + (p.getCarries().size() + 1));
                        AnchorPane.setTopAnchor(label, points.get(index).getY());
                        AnchorPane.setLeftAnchor(label, points.get(index).getX());
                        pieces.add(label);
                    }
                }
            }
        }

        mainPanel.getChildren().addAll(pieces);
    }

    private void showPossibleLocations(Piece piece) {
        mainPanel.getChildren().removeAll(markers);
        markers.clear();

        List<BoardSpace> locations = gm.getPossibleLocations(piece);

        for (int i = 0; i < 6; i++) {
            if (locations.get(i) != null) {
                int index = gm.getBoard().getSpaces().indexOf(locations.get(i));

                Button button;
                if (index == -1) {
                    button = new Button("도착!");
                    AnchorPane.setTopAnchor(button, 400.0);
                    AnchorPane.setLeftAnchor(button, 250.0);
                    button.setId("possibleLocation");
                } else {
                    button = new Button("여기로 이동");
                    AnchorPane.setTopAnchor(button, points.get(index).getY());
                    AnchorPane.setLeftAnchor(button, points.get(index).getX() - 50);
                    button.setId("possibleLocation");
                }

                int finalI = i;
                button.setOnAction(event -> {
                    gm.movePiece(piece, Yut.values()[finalI]);
                    mainPanel.getChildren().removeAll(markers);
                    markers.clear();
                    updateBoard();
                });
                markers.add(button);
                mainPanel.getChildren().add(button);
            }
        }
    }
}
