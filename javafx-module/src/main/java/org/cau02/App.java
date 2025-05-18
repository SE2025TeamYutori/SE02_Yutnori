package org.cau02;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.cau02.view.StartPanel;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        StartPanel root = new StartPanel();

        Scene scene = new Scene(root.getRoot());
        scene.setRoot(root.getRoot());

        String cssPath = getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setTitle("윷놀이 게임!");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/favicon.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


