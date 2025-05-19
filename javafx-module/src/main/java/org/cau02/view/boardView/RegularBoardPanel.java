package org.cau02.view.boardView;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.cau02.controller.boardController.HexagonBoardController;
import org.cau02.controller.boardController.PentagonBoardController;
import org.cau02.controller.boardController.RegularBoardController;
import org.cau02.controller.boardController.SquareBoardController;
import org.cau02.model.GameManager;

public class RegularBoardPanel implements BoardPanel {
    private final AnchorPane root;
    private RegularBoardController controller;

    @Override
    public AnchorPane getRoot() {
        return root;
    }

    @Override
    public RegularBoardController getController() {
        return controller;
    }

    public RegularBoardPanel(GameManager gm, int boardAngle) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/regular_board.fxml"));
        try {
            fxmlLoader.setControllerFactory(param -> {
                switch (boardAngle) {
                    case 4:
                        this.controller = new SquareBoardController(gm);
                        break;
                    case 5:
                        this.controller = new PentagonBoardController(gm);
                        break;
                    case 6:
                        this.controller = new HexagonBoardController(gm);
                        break;
                }
                return this.controller;
            });
            this.controller = fxmlLoader.getController();
            this.root = fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load StartPanel FXML", exception);
        }
    }
}
