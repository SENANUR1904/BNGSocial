package org.example.bngsocial.Screens;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class mainScreen {

    public void show(Stage stage, String username) {

        Label welcome = new Label("Hoş geldin " + username);
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox root = new VBox(20, welcome);
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Ana Sayfa");
        stage.show();
    }
}