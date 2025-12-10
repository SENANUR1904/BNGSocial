package org.example.bngsocial.Screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.bngsocial.Controllers.SignScreenController;

public class signScreen {

    public void show(Stage stage) {
        try {
            // Her seferinde YENİ bir loader oluştur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/signScreen.fxml"));
            // YENİ bir root oluştur
            Parent root = loader.load();

            SignScreenController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }

            // YENİ bir Scene oluştur
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            // Stage'e YENİ Scene'i set et
            stage.setScene(scene);
            stage.setTitle("Giriş Yap");
            stage.setWidth(400);
            stage.setHeight(500);

        } catch (Exception e) {
            e.printStackTrace();
            showManualScreen(stage);
        }
    }

    private void showManualScreen(Stage stage) {
        // Manuel ekran için de YENİ elementler oluştur
        javafx.scene.control.Label title = new javafx.scene.control.Label("Giriş Yap");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        javafx.scene.control.TextField username = new javafx.scene.control.TextField();
        username.setPromptText("Kullanıcı Adı");
        username.setPrefWidth(260);

        javafx.scene.control.PasswordField password = new javafx.scene.control.PasswordField();
        password.setPromptText("Şifre");
        password.setPrefWidth(260);

        javafx.scene.control.Button loginBtn = new javafx.scene.control.Button("Giriş Yap");
        loginBtn.setPrefWidth(260);
        loginBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        javafx.scene.control.Label registerLink = new javafx.scene.control.Label("Kayıt Ol");
        registerLink.setStyle("-fx-text-fill: #2c5aa0; -fx-cursor: hand;");
        registerLink.setOnMouseEntered(e ->
                registerLink.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;"));
        registerLink.setOnMouseExited(e ->
                registerLink.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;"));
        registerLink.setOnMouseClicked(e -> new registerScreen().show(stage));

        javafx.scene.control.Label forgotLink = new javafx.scene.control.Label("Şifremi Unuttum");
        forgotLink.setStyle("-fx-text-fill: #2c5aa0; -fx-cursor: hand;");
        forgotLink.setOnMouseEntered(e ->
                forgotLink.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;"));
        forgotLink.setOnMouseExited(e ->
                forgotLink.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;"));
        forgotLink.setOnMouseClicked(e -> new forgotPasswordScreen().show(stage));

        javafx.scene.layout.HBox bottomLinks = new javafx.scene.layout.HBox();
        bottomLinks.setAlignment(javafx.geometry.Pos.CENTER);
        bottomLinks.setSpacing(120);
        bottomLinks.getChildren().addAll(registerLink, forgotLink);

        org.example.bngsocial.Controllers.MainController controller = new org.example.bngsocial.Controllers.MainController();
        loginBtn.setOnAction(e -> controller.login(username.getText(), password.getText(), stage));

        // YENİ bir VBox oluştur
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(15, title, username, password, loginBtn, bottomLinks);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(40, 20, 40, 20));

        // YENİ bir Scene oluştur
        stage.setScene(new Scene(root, 350, 350));
        stage.setTitle("Giriş (Manuel)");
    }
}