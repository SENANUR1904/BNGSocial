package org.example.bngsocial;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.bngsocial.Screens.signScreen;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new signScreen().show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
