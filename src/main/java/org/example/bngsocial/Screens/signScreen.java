package org.example.bngsocial.Screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.bngsocial.Controllers.MainController;

public class signScreen {

    public void show(Stage stage) {

        Label title = new Label("Giriş Yap");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("Kullanıcı Adı");
        username.setPrefWidth(260);

        PasswordField password = new PasswordField();
        password.setPromptText("Şifre");
        password.setPrefWidth(260);

        Button loginBtn = new Button("Giriş Yap");
        loginBtn.setPrefWidth(260);
        loginBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        // SOL TARAF: "Kayıt Ol" linki
        Label registerLink = new Label("Kayıt Ol");
        registerLink.setStyle("-fx-text-fill: #2c5aa0; -fx-cursor: hand;");
        registerLink.setOnMouseEntered(e ->
                registerLink.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;"));
        registerLink.setOnMouseExited(e ->
                registerLink.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;"));
        registerLink.setOnMouseClicked(e -> new registerScreen().show(stage));

        // SAĞ TARAF: "Şifremi Unuttum" linki
        Label forgotLink = new Label("Şifremi Unuttum");
        forgotLink.setStyle("-fx-text-fill: #2c5aa0; -fx-cursor: hand;");
        forgotLink.setOnMouseEntered(e ->
                forgotLink.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;"));
        forgotLink.setOnMouseExited(e ->
                forgotLink.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;"));
        forgotLink.setOnMouseClicked(e -> new forgotPasswordScreen().show(stage));


        HBox bottomLinks = new HBox();
        bottomLinks.setAlignment(Pos.CENTER);
        bottomLinks.setSpacing(120); // İki link arasında boşluk

        // Linkleri HBox'a ekleme - SOL: Kayıt Ol, SAĞ: Şifremi Unuttum
        bottomLinks.getChildren().addAll(registerLink, forgotLink);

        MainController controller = new MainController();

        loginBtn.setOnAction(e -> controller.login(username.getText(), password.getText(), stage));

        VBox root = new VBox(15, title, username, password, loginBtn, bottomLinks);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 20, 40, 20));

        stage.setScene(new Scene(root, 350, 350));
        stage.setTitle("Giriş");
        stage.show();
    }
}