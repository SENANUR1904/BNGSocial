package org.example.bngsocial;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/signScreen.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("BngSocial - Giriş");
            primaryStage.setWidth(400);
            primaryStage.setHeight(500);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorScreen(primaryStage);
        }
    }

    private void showErrorScreen(Stage stage) {
        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "❌ Uygulama başlatılamadı!\n\n" +
                        "Lütfen konsoldaki hataları kontrol edin."
        );
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-padding: 20px;");

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(errorLabel);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        stage.setScene(new Scene(vbox, 500, 300));
        stage.setTitle("Hata!");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}