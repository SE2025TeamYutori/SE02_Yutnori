package org.cau02.controller;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import org.cau02.model.GameManager;
import org.cau02.model.Yut;
import org.cau02.view.YutSelectPanel;

public class YutSelectPanelController {
    GameManager gm;
    YutSelectPanel panel;
    MainPanelController mainPanelController;

    public YutSelectPanelController(GameManager gm, YutSelectPanel panel, MainPanelController mainPanelController) {
        this.gm = gm;
        this.panel = panel;
        this.mainPanelController = mainPanelController;
    }

    public void throwSelectYut(MouseEvent mouseEvent) {
        Yut yut = Yut.valueOf(((Circle)(mouseEvent.getSource())).getId().toUpperCase());

        gm.throwSelectedYut(yut);

        mainPanelController.closeYutSelectPanel(panel.getRoot());
        mainPanelController.showYutImage(yut);
    }
}
