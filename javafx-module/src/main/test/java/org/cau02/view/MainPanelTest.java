package org.cau02.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cau02.model.GameManager;

public class MainPanelTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainPanel mainPanel = new MainPanel(new GameManager(4, 2, 2));

        Scene scene = new Scene(mainPanel.getRoot());
        scene.setRoot(mainPanel.getRoot());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void test() {
        launch();
    }
}
