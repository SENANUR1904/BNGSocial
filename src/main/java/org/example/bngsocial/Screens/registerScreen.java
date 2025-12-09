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

        TextField name = new TextField();
        name.setPromptText("İsim");
        name.setPrefWidth(260);

        TextField username = new TextField();
        username.setPromptText("Kullanıcı Adı");
        username.setPrefWidth(260);

        TextField email = new TextField();
        email.setPromptText("Email");
        email.setPrefWidth(260);

        PasswordField pass1 = new PasswordField();
        pass1.setPromptText("Şifre");
        pass1.setPrefWidth(260);

        PasswordField pass2 = new PasswordField();
        pass2.setPromptText("Şifre (Tekrar)");
        pass2.setPrefWidth(260);

        Button registerBtn = new Button("Kayıt Ol");
        registerBtn.setPrefWidth(260);
        registerBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button backBtn = new Button("Geri");
        backBtn.setPrefWidth(260);

        MainController controller = new MainController();

        registerBtn.setOnAction(e ->
                controller.register(name.getText(), username.getText(), email.getText(),
                        pass1.getText(), pass2.getText(), stage)
        );

        backBtn.setOnAction(e -> new signScreen().show(stage));

        VBox root = new VBox(12, title, name, username, email, pass1, pass2, registerBtn, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 420));
        stage.setTitle("Kayıt");
        stage.show();
    }
}