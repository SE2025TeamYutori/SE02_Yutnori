package org.cau02;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.cau02.view.StartPanel;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        StartPanel root = new StartPanel();

        Scene scene = new Scene(root.getRoot());
        scene.setRoot(root.getRoot());

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        Font.loadFont(getClass().getResourceAsStream("/fonts/MaplestoryLight.ttf"), 24);
        Font.loadFont(getClass().getResourceAsStream("/fonts/MaplestoryBold.ttf"), 24);

        primaryStage.setTitle("윷놀이 게임!");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/favicon.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


