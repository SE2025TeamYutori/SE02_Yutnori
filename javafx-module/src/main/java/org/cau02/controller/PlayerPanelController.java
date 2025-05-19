package org.cau02.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.cau02.controller.boardController.BoardController;
import org.cau02.model.GameManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerPanelController {
    private final GameManager gm;
    private final int playerId;
    private final BoardController boardController;

    @FXML private HBox readyPieceBox;

    @FXML private Rectangle playerColorRectangle;
    @FXML private Label playerLabel;
    @FXML private Button moveNewPieceButton;

    private final List<Circle> ReadyPieces = new ArrayList<>(5);

    public PlayerPanelController(GameManager gm, int playerId, BoardController boardController) {
        this.gm = gm;
        this.playerId = playerId;
        this.boardController = boardController;
    }

    private void initializePiece() {
        for (int i = 0; i < gm.getPieceCount(); i++) {
            Circle circle = new Circle(0, 0, 20);
            circle.setId("player" + playerId);
            ReadyPieces.add(circle);
        }

        readyPieceBox.getChildren().addAll(ReadyPieces);
    }

    @FXML
    private void initialize() {
        playerColorRectangle.setId("player" + playerId);
        playerLabel.setText("플레이어 " + (playerId + 1));

        initializePiece();
    }

    @FXML
    private void showPossibleLocationsWithNewPiece() {
        boardController.showPossibleLocations();
    }

    public void enableMoveNewPieceButton() {
        moveNewPieceButton.setVisible(true);
    }

    public void disableMoveNewPieceButton() {
        moveNewPieceButton.setVisible(false);
    }

    public void updateReadyPieces() {
        readyPieceBox.getChildren().clear();
        readyPieceBox.getChildren().addAll(ReadyPieces.subList(0, gm.getReadyPiecesCount(playerId)));
    }
}
