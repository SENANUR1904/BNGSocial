package org.example.bngsocial.Screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.bngsocial.Controllers.MainScreenController;

public class mainScreen {

    public void show(Stage stage, String username) {
        try {
            // YENİ loader ve root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/mainScreen.fxml"));
            Parent root = loader.load();

            MainScreenController controller = loader.getController();
            if (controller != null) {
                controller.setUsername(username);
                controller.setStage(stage);
            }

            // YENİ Scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Ana Sayfa - " + username);
            stage.setWidth(500);
            stage.setHeight(500);

        } catch (Exception e) {
            e.printStackTrace();
            showManualScreen(stage, username);
        }
    }

    private void showManualScreen(Stage stage, String username) {
        // YENİ elementler
        javafx.scene.control.Label welcome = new javafx.scene.control.Label("Hoş geldin " + username);
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        javafx.scene.control.TextArea postArea = new javafx.scene.control.TextArea();
        postArea.setPromptText("Bir şey paylaş...");
        postArea.setPrefRowCount(4);
        postArea.setPrefWidth(300);

        javafx.scene.control.Button postButton = new javafx.scene.control.Button("Paylaş");
        postButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        javafx.scene.control.Button logoutButton = new javafx.scene.control.Button("Çıkış Yap");
        logoutButton.setOnAction(e -> new signScreen().show(stage));

        postButton.setOnAction(e -> {
            String postText = postArea.getText();
            if (!postText.trim().isEmpty()) {
                System.out.println(username + " paylaştı: " + postText);
                postArea.clear();
            }
        });

        // YENİ VBox
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(20, welcome, postArea, postButton, logoutButton);
        root.setAlignment(javafx.geometry.Pos.CENTER);

        // YENİ Scene
        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Ana Sayfa (Manuel) - " + username);
    }
}