package org.cau02.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.cau02.controller.MainPanelController;
import org.cau02.controller.WinPanelController;

public class WinPanel {
    private final VBox root;

    public VBox getRoot() {
        return root;
    }

    public WinPanel(int winnerId) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/win_panel.fxml"));

        try {
            fxmlLoader.setControllerFactory(param -> new WinPanelController(winnerId));
            root = fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load StartPanel FXML", exception);
        }
    }
}
