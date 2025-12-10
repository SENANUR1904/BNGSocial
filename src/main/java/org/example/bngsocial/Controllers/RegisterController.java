package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button backButton;

    private final MainController mainController = new MainController();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Butonlara CSS class'larını ekle
        if (registerButton != null) {
            registerButton.getStyleClass().add("secondary-button");
        }
        if (backButton != null) {
            backButton.getStyleClass().add("default-button");
        }
    }

    @FXML
    private void handleRegister() {
        if (stage == null && registerButton != null) {
            stage = (Stage) registerButton.getScene().getWindow();
        }

        // MainController'daki register metodunu çağır
        mainController.register(
                nameField.getText(),
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                stage
        );
    }

    @FXML
    private void handleBack() {
        if (stage == null && backButton != null) {
            stage = (Stage) backButton.getScene().getWindow();
        }
        if (stage != null) {
            // Giriş ekranına dön
            new org.example.bngsocial.Screens.signScreen().show(stage);
        }
    }
}