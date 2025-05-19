package org.cau02.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import org.cau02.controller.MainPanelController;
import org.cau02.model.GameManager;

public class MainPanel {
    private final StackPane root;

    public StackPane getRoot() {
        return root;
    }

    public MainPanel(GameManager gm) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_panel.fxml"));

        try {
            fxmlLoader.setControllerFactory(param -> new MainPanelController(gm));
            root = fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load StartPanel FXML", exception);
        }
    }
}
