package com.example.demo3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            Parent root =FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/demo3/loging.fxml")));
            Scene scene = new Scene(root);



            primaryStage.setTitle("Fish Info Hub");
            primaryStage.setScene(scene);
          //  primaryStage.setMaximized(true);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
