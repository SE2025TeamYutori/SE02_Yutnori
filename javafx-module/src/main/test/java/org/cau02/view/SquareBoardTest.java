package org.cau02.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cau02.model.GameManager;
import org.cau02.view.boardView.SquareBoardPanel;

public class SquareBoardTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        SquareBoardPanel startPanel = new SquareBoardPanel(new GameManager(4, 2, 5));

        Scene scene = new Scene(startPanel.getRoot());
        scene.setRoot(startPanel.getRoot());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void test() {
        launch();
    }
}
