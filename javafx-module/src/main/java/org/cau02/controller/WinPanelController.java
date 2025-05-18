package org.cau02.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.cau02.view.StartPanel;

public class WinPanelController {
    private int winnerId;

    @FXML private Label winnerTitleLabel;

    public WinPanelController(int winnerId) {
        this.winnerId = winnerId;
    }

    @FXML
    private void initialize() {
        winnerTitleLabel.setText("플레이어 " + (winnerId + 1) + " 승리!");
    }

    public void resetGame(ActionEvent actionEvent) {
        StartPanel startPanel = new StartPanel();
        ((Node)(actionEvent.getSource())).getScene().setRoot(startPanel.getRoot());
    }

    public void endGame(ActionEvent actionEvent) {
        Platform.exit();
    }
}
