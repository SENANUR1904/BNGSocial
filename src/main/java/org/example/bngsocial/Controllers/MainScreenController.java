package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.bngsocial.Controllers.MainController;

public class MainScreenController {

    @FXML private Label welcomeLabel;
    @FXML private TextArea postArea;
    @FXML private Button postButton;
    @FXML private Button logoutButton;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Hoş geldin " + username + "!");
        }
    }

    @FXML
    private void handlePost() {
        if (postArea != null && username != null) {
            String postText = postArea.getText();
            if (!postText.trim().isEmpty()) {
                System.out.println(username + " paylaştı: " + postText);

                MainController mainController = new MainController();
                mainController.showAlert(javafx.scene.control.Alert.AlertType.INFORMATION,
                        "Başarılı", "Paylaşımınız yayınlandı!");

                postArea.clear();
            }
        }
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        MainController mainController = new MainController();
        mainController.changeScreen(stage, "/org/example/bngsocial/views/signScreen.fxml", "Giriş Yap");
    }
}