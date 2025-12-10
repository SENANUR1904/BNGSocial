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

    // DÜZELTİLDİ: MainController olacak, MainScreenController değil
    private final MainController mainController = new MainController();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Butonlara CSS class'larını ekle
        if (resetPasswordButton != null) {
            resetPasswordButton.getStyleClass().add("warning-button");
        }
        if (backButton != null) {
            backButton.getStyleClass().add("default-button");
        }
    }

    @FXML
    private void handleResetPassword() {
        if (stage == null && resetPasswordButton != null) {
            stage = (Stage) resetPasswordButton.getScene().getWindow();
        }

        // MainController'daki resetPassword metodunu çağır
        mainController.resetPassword(
                usernameField.getText(),
                emailField.getText(),
                newPasswordField.getText(),
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
            new org.example.bngsocial.Screens.signScreen().show(stage);
        }
    }
}