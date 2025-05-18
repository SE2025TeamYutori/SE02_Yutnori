package org.cau02.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.cau02.controller.MainPanelController;
import org.cau02.controller.YutSelectPanelController;
import org.cau02.model.GameManager;

public class YutSelectPanel {
    private final VBox root;

    public VBox getRoot() {
        return root;
    }

    public YutSelectPanel(GameManager gm, MainPanelController mainPanelController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/yut_select_panel.fxml"));

        try {
            fxmlLoader.setControllerFactory(param -> new YutSelectPanelController(gm, this, mainPanelController));
            root = fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load StartPanel FXML", exception);
        }
    }
}
