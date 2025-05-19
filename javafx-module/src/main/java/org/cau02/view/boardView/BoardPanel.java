package org.cau02.view.boardView;

import javafx.scene.Node;
import org.cau02.controller.boardController.BoardController;

public interface BoardPanel {
    Node getRoot();
    BoardController getController();
}
