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

public class registerScreen {

    public void show(Stage stage) {

        Label title = new Label("Kayıt Ol");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("Kullanıcı Adı");

        PasswordField pass1 = new PasswordField();
        pass1.setPromptText("Şifre");

        PasswordField pass2 = new PasswordField();
        pass2.setPromptText("Şifre (Tekrar)");

        Button registerBtn = new Button("Kayıt Ol");
        Button backBtn = new Button("Geri");

        MainController controller = new MainController();

        registerBtn.setOnAction(e ->
                controller.register(username.getText(), pass1.getText(), pass2.getText(), stage)
        );

        backBtn.setOnAction(e -> new signScreen().show(stage));

        VBox root = new VBox(12, title, username, pass1, pass2, registerBtn, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 350));
        stage.setTitle("Kayıt");
        stage.show();
    }
}
