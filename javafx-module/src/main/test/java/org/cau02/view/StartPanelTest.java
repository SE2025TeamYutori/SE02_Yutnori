package org.cau02.view;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartPanelTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        StartPanel startPanel = new StartPanel();

        Scene scene = new Scene(startPanel.getRoot());
        scene.setRoot(startPanel.getRoot());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void test() {
        launch();
    }
}