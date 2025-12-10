package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.bngsocial.Screens.signScreen;

public class MainScreenController {

    @FXML private Label welcomeLabel;
    @FXML private TextArea postArea;
    @FXML private Button postButton;
    @FXML private Button logoutButton;

    private String username;
    private Stage stage;

    public void setUsername(String username) {
        this.username = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Hoş geldin " + username + "!");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handlePost() {
        String postText = postArea.getText();
        if (!postText.trim().isEmpty()) {
            System.out.println(username + " paylaştı: " + postText);
            postArea.clear();
        }
    }

    @FXML
    private void handleLogout() {
        if (stage == null) {
            stage = (Stage) logoutButton.getScene().getWindow();
        }
        new signScreen().show(stage);
    }
}