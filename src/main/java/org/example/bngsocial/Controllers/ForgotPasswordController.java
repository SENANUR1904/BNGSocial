package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button resetPasswordButton;
    @FXML private Button backButton;

    private final MainController mainController = new MainController();

    @FXML
    private void handleResetPassword() {
        Stage stage = (Stage) resetPasswordButton.getScene().getWindow();

        boolean success = mainController.resetPassword(
                usernameField.getText(),
                emailField.getText(),
                newPasswordField.getText(),
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