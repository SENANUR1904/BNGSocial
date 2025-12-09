package org.example.bngsocial.Screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.bngsocial.Controllers.MainController;

public class forgotPasswordScreen {

    public void show(Stage stage) {

        Label title = new Label("Şifre Sıfırla");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("Kullanıcı Adı");
        username.setPrefWidth(260);

        TextField email = new TextField();
        email.setPromptText("Email");
        email.setPrefWidth(260);

        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Yeni Şifre");
        newPassword.setPrefWidth(260);

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Yeni Şifre (Tekrar)");
        confirmPassword.setPrefWidth(260);

        Button resetBtn = new Button("Şifreyi Güncelle");
        resetBtn.setPrefWidth(260);
        resetBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        Button backBtn = new Button("Geri");
        backBtn.setPrefWidth(260);

        MainController controller = new MainController();

        resetBtn.setOnAction(e -> controller.resetPassword(username.getText(), email.getText(),
                newPassword.getText(), confirmPassword.getText(), stage));

        backBtn.setOnAction(e -> new signScreen().show(stage));

        VBox root = new VBox(12, title, username, email, newPassword, confirmPassword, resetBtn, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 380));
        stage.setTitle("Şifre Sıfırlama");
        stage.show();
    }
}