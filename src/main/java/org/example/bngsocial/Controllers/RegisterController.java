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

    @FXML
    private void handleRegister() {
        Stage stage = (Stage) registerButton.getScene().getWindow();

        boolean success = mainController.register(
                nameField.getText(),
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                stage
        );

        if (success) {
            mainController.changeScreen(stage, "/org/example/bngsocial/views/signScreen.fxml", "Giriş Yap");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        mainController.changeScreen(stage, "/org/example/bngsocial/views/signScreen.fxml", "Giriş Yap");
    }
}