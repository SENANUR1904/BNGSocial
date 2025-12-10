package org.example.bngsocial.Screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.bngsocial.Controllers.RegisterController;

public class registerScreen {

    public void show(Stage stage) {
        try {
            // YENİ loader ve root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/registerScreen.fxml"));
            Parent root = loader.load();

            RegisterController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }

            // YENİ Scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Kayıt Ol");
            stage.setWidth(400);
            stage.setHeight(550);

        } catch (Exception e) {
            e.printStackTrace();
            showManualScreen(stage);
        }
    }

    private void showManualScreen(Stage stage) {
        // YENİ elementler
        javafx.scene.control.Label title = new javafx.scene.control.Label("Kayıt Ol");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        javafx.scene.control.TextField name = new javafx.scene.control.TextField();
        name.setPromptText("İsim");
        name.setPrefWidth(260);

        javafx.scene.control.TextField username = new javafx.scene.control.TextField();
        username.setPromptText("Kullanıcı Adı");
        username.setPrefWidth(260);

        javafx.scene.control.TextField email = new javafx.scene.control.TextField();
        email.setPromptText("Email");
        email.setPrefWidth(260);

        javafx.scene.control.PasswordField pass1 = new javafx.scene.control.PasswordField();
        pass1.setPromptText("Şifre");
        pass1.setPrefWidth(260);

        javafx.scene.control.PasswordField pass2 = new javafx.scene.control.PasswordField();
        pass2.setPromptText("Şifre (Tekrar)");
        pass2.setPrefWidth(260);

        javafx.scene.control.Button registerBtn = new javafx.scene.control.Button("Kayıt Ol");
        registerBtn.setPrefWidth(260);
        registerBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        javafx.scene.control.Button backBtn = new javafx.scene.control.Button("Geri");
        backBtn.setPrefWidth(260);

        org.example.bngsocial.Controllers.MainController controller = new org.example.bngsocial.Controllers.MainController();

        registerBtn.setOnAction(e -> controller.register(
                name.getText(),
                username.getText(),
                email.getText(),
                pass1.getText(),
                pass2.getText(),
                stage
        ));

        backBtn.setOnAction(e -> new signScreen().show(stage));

        // YENİ VBox
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(12, title, name, username, email, pass1, pass2, registerBtn, backBtn);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(20));

        // YENİ Scene
        stage.setScene(new Scene(root, 350, 420));
        stage.setTitle("Kayıt (Manuel)");
    }
}