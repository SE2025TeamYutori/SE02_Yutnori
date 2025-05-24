package org.cau02.controller.boardController;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import org.cau02.model.GameManager;

public abstract class RegularBoardController extends BoardController {
    protected static final double SIDE_LENGTH = 600.0;
    protected static final double BOARDSPACE_RADIUS = 20.0;

    @FXML protected AnchorPane mainPanel;
    @FXML protected Shape boardShape;

    RegularBoardController(GameManager gm) {
        super(gm);
    }

    protected abstract void initializeBoardPolygon();
    protected abstract void initializeDiagLines();
    protected abstract void initializeBoardSpaces();

    @FXML
    private void initialize() {
        mainPanel.setMinSize(SIDE_LENGTH, SIDE_LENGTH);
        mainPanel.setMaxSize(SIDE_LENGTH, SIDE_LENGTH);

        initializeBoardPolygon();
        initializeDiagLines();
        initializeBoardSpaces();
    }

}
