package org.cau02.view.boardView;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.cau02.controller.boardController.BoardController;
import org.cau02.controller.boardController.SquareBoardController;
import org.cau02.model.GameManager;

public class SquareBoardPanel implements BoardPanel {
    private final AnchorPane root;
    private SquareBoardController controller;

    public AnchorPane getRoot() {
        return root;
    }

    @Override
    public BoardController getController() {
        return controller;
    }

    public SquareBoardPanel(GameManager gm) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/board/square_board.fxml"));

        try {
            fxmlLoader.setControllerFactory(param -> {
                this.controller = new SquareBoardController(gm);
                return this.controller;
            });
            this.controller = fxmlLoader.getController();
            this.root = fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load StartPanel FXML", exception);
        }
    }
}
