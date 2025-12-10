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
    @FXML private Label registerLabel;        // SOL: Kayıt Ol
    @FXML private Label forgotPasswordLabel;  // SAĞ: Şifremi Unuttum

    private final MainController mainController = new MainController();

    @FXML
    private void handleSignIn() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        mainController.login(usernameField.getText(), passwordField.getText(), stage);
    }

    @FXML
    private void openRegister() {
        Stage stage = (Stage) registerLabel.getScene().getWindow();
        mainController.changeScreen(stage, "/org/example/bngsocial/views/registerScreen.fxml", "Kayıt Ol");
    }

    @FXML
    private void openForgotPassword() {
        Stage stage = (Stage) forgotPasswordLabel.getScene().getWindow();
        mainController.changeScreen(stage, "/org/example/bngsocial/views/forgotPasswordScreen.fxml", "Şifre Sıfırlama");
    }

}