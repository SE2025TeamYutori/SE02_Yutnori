package org.cau02.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.cau02.controller.PlayerPanelController;
import org.cau02.controller.boardController.BoardController;
import org.cau02.model.GameManager;

public class PlayerPanel {
    private final VBox root;
    private PlayerPanelController controller;

    public VBox getRoot() {
        return root;
    }

    public PlayerPanelController getController() {
        return controller;
    }

    public PlayerPanel(GameManager gm, int playerId, BoardController boardController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/player_panel.fxml"));

        try {
            fxmlLoader.setControllerFactory(param -> {
                this.controller = new PlayerPanelController(gm, playerId, boardController);
                return this.controller;
            });
            root = fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load StartPanel FXML", exception);
        }
    }
}
