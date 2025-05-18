package org.cau02.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class StartPanel {
    private final VBox root;

    public VBox getRoot() {
        return root;
    }

    public StartPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/start_panel.fxml"));

        try {
            root = fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load StartPanel FXML", exception);
        }
    }
}
