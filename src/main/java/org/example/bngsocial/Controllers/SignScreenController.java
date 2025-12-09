package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bngsocial.Screens.forgotPasswordScreen;
import org.example.bngsocial.Screens.registerScreen;

public class SignScreenController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label forgotPasswordLabel;

    @FXML
    private Label registerLabel;

    private MainController mainController = new MainController();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Link stillerini ayarla
        setupLinkStyle(forgotPasswordLabel);
        setupLinkStyle(registerLabel);
    }

    private void setupLinkStyle(Label label) {
        label.setOnMouseEntered(e ->
                label.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;"));
        label.setOnMouseExited(e ->
                label.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;"));
    }

    @FXML
    private void onSignInButtonClicked() {
        mainController.login(usernameField.getText(), passwordField.getText(), stage);
    }

    @FXML
    private void openForgotPassword() {
        new forgotPasswordScreen().show(stage);
    }

    @FXML
    private void openRegister() {
        new registerScreen().show(stage);
    }

    @FXML
    private void onForgotPasswordMouseEnter() {
        forgotPasswordLabel.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;");
    }

    @FXML
    private void onForgotPasswordMouseExit() {
        forgotPasswordLabel.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;");
    }

    @FXML
    private void onRegisterMouseEnter() {
        registerLabel.setStyle("-fx-text-fill: #1a3d7a; -fx-underline: true; -fx-cursor: hand;");
    }

    @FXML
    private void onRegisterMouseExit() {
        registerLabel.setStyle("-fx-text-fill: #2c5aa0; -fx-underline: false; -fx-cursor: hand;");
    }
}