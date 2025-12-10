package org.example.bngsocial.Screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.bngsocial.Controllers.ForgotPasswordController;

public class forgotPasswordScreen {

    public void show(Stage stage) {
        try {
            // YENİ loader ve root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/forgotPasswordScreen.fxml"));
            Parent root = loader.load();

            ForgotPasswordController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }

            // YENİ Scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Şifre Sıfırlama");
            stage.setWidth(400);
            stage.setHeight(500);

        } catch (Exception e) {
            e.printStackTrace();
            showManualScreen(stage);
        }
    }

    private void showManualScreen(Stage stage) {
        // YENİ elementler
        javafx.scene.control.Label title = new javafx.scene.control.Label("Şifre Sıfırla");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        javafx.scene.control.TextField username = new javafx.scene.control.TextField();
        username.setPromptText("Kullanıcı Adı");
        username.setPrefWidth(260);

        javafx.scene.control.TextField email = new javafx.scene.control.TextField();
        email.setPromptText("Email");
        email.setPrefWidth(260);

        javafx.scene.control.PasswordField newPassword = new javafx.scene.control.PasswordField();
        newPassword.setPromptText("Yeni Şifre");
        newPassword.setPrefWidth(260);

        javafx.scene.control.PasswordField confirmPassword = new javafx.scene.control.PasswordField();
        confirmPassword.setPromptText("Yeni Şifre (Tekrar)");
        confirmPassword.setPrefWidth(260);

        javafx.scene.control.Button resetBtn = new javafx.scene.control.Button("Şifreyi Güncelle");
        resetBtn.setPrefWidth(260);
        resetBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        javafx.scene.control.Button backBtn = new javafx.scene.control.Button("Geri");
        backBtn.setPrefWidth(260);

        org.example.bngsocial.Controllers.MainController controller = new org.example.bngsocial.Controllers.MainController();

        resetBtn.setOnAction(e -> controller.resetPassword(
                username.getText(),
                email.getText(),
                newPassword.getText(),
                confirmPassword.getText(),
                stage
        ));

        backBtn.setOnAction(e -> new signScreen().show(stage));

        // YENİ VBox
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(12, title, username, email, newPassword, confirmPassword, resetBtn, backBtn);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(20));

        // YENİ Scene
        stage.setScene(new Scene(root, 350, 380));
        stage.setTitle("Şifre Sıfırlama (Manuel)");
    }
}