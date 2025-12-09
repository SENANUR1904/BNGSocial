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

public class signScreen {

    public void show(Stage stage) {

        Label title = new Label("Giriş Yap");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("Kullanıcı Adı");

        PasswordField password = new PasswordField();
        password.setPromptText("Şifre");

        Button loginBtn = new Button("Giriş Yap");
        Button registerBtn = new Button("Kayıt Ol");
        Button forgotBtn = new Button("Şifremi Unuttum");

        MainController controller = new MainController();

        loginBtn.setOnAction(e -> controller.login(username.getText(), password.getText(), stage));
        registerBtn.setOnAction(e -> new registerScreen().show(stage));
        forgotBtn.setOnAction(e -> new forgotPasswordScreen().show(stage));

        VBox root = new VBox(12, title, username, password, loginBtn, registerBtn, forgotBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 350));
        stage.setTitle("Giriş");
        stage.show();
    }
}
