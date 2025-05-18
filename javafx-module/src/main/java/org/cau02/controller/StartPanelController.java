package org.cau02.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import org.cau02.model.GameManager;
import org.cau02.view.MainPanel;

public class StartPanelController {
    @FXML private ComboBox<String> gameBoardCombobox;
    @FXML private Spinner<Integer> playerCountSpinner;
    @FXML private Spinner<Integer> pieceCountSpinner;

    @FXML
    private void startGame(ActionEvent actionEvent) {
        int boardAngle = switch (gameBoardCombobox.getValue()) {
            case "사각형" -> 4;
            case "오각형" -> 5;
            case "육각형" -> 6;
            default -> -1;
        };
        int playerCount = playerCountSpinner.getValue();
        int pieceCount = pieceCountSpinner.getValue();

        GameManager gameManager = new GameManager(boardAngle, playerCount, pieceCount);

        MainPanel mainPanel = new MainPanel(gameManager);
        ((Node)(actionEvent.getSource())).getScene().setRoot(mainPanel.getRoot());

        gameManager.startGame();
    }
}
