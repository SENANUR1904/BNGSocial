package org.example.bngsocial.Screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

        Button resetBtn = new Button("Sıfırla");
        Button backBtn = new Button("Geri");

        MainController controller = new MainController();

        resetBtn.setOnAction(e -> controller.resetPassword(username.getText(), stage));
        backBtn.setOnAction(e -> new signScreen().show(stage));

        VBox root = new VBox(12, title, username, resetBtn, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 320));
        stage.setTitle("Şifre Sıfırlama");
        stage.show();
    }
}
