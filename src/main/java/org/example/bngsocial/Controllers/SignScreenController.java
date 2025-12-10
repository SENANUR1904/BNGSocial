package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignScreenController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button signInButton;
    @FXML private Label forgotPasswordLabel;
    @FXML private Label registerLabel;

    // DÜZELTİLDİ: MainController olacak, MainScreenController değil
    private final MainController mainController = new MainController();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private void initialize() {
        setupLinkStyle(forgotPasswordLabel);
        setupLinkStyle(registerLabel);

        // Butona CSS class'ını ekle
        if (signInButton != null) {
            signInButton.getStyleClass().add("primary-button");
        }
    }

    private void setupLinkStyle(Label label) {
        label.getStyleClass().add("link-label");

        label.setOnMouseEntered(e ->
                label.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;")
        );

        label.setOnMouseExited(e ->
                label.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;")
        );
    }

    @FXML
    private void handleSignIn() {
        if (stage == null && usernameField != null) {
            stage = (Stage) usernameField.getScene().getWindow();
        }

        // MainController'daki login metodunu çağır
        mainController.login(usernameField.getText(), passwordField.getText(), stage);
    }

    @FXML
    private void openForgotPassword() {
        if (stage == null && forgotPasswordLabel != null) {
            stage = (Stage) forgotPasswordLabel.getScene().getWindow();
        }
        if (stage != null) {
            new org.example.bngsocial.Screens.forgotPasswordScreen().show(stage);
        }
    }

    @FXML
    private void openRegister() {
        if (stage == null && registerLabel != null) {
            stage = (Stage) registerLabel.getScene().getWindow();
        }
        if (stage != null) {
            // YENİ bir registerScreen oluştur
            new org.example.bngsocial.Screens.registerScreen().show(stage);
        }
    }
    @FXML
    private void onForgotPasswordMouseEnter() {
        if (forgotPasswordLabel != null) {
            forgotPasswordLabel.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;");
        }
    }

    @FXML
    private void onForgotPasswordMouseExit() {
        if (forgotPasswordLabel != null) {
            forgotPasswordLabel.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;");
        }
    }

    @FXML
    private void onRegisterMouseEnter() {
        if (registerLabel != null) {
            registerLabel.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;");
        }
    }

    @FXML
    private void onRegisterMouseExit() {
        if (registerLabel != null) {
            registerLabel.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;");
        }
    }
}