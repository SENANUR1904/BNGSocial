package org.example.bngsocial.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.bngsocial.Models.User;
import org.example.bngsocial.Models.UserDatabase;

import java.io.IOException;

public class MainController {

    public void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void changeScreen(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(title);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Ekran yüklenemedi!");
        }
    }

    public boolean login(String username, String password, Stage stage) {
        User user = UserDatabase.login(username, password);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Hatalı kullanıcı adı veya şifre!");
            return false;
        }

        // Ana ekrana geç
        changeScreen(stage, "/org/example/bngsocial/views/mainScreen.fxml", "Ana Sayfa - " + user.getName());

        // MainScreenController'a kullanıcı adını ilet
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/mainScreen.fxml"));
            Parent root = loader.load();
            MainScreenController controller = loader.getController();
            controller.setUsername(user.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean register(String name, String username, String email, String password1, String password2, Stage stage) {
        if (!password1.equals(password2)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Şifreler aynı değil!");
            return false;
        }

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password1.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Tüm alanları doldurun!");
            return false;
        }

        boolean success = UserDatabase.addUser(new User(name, username, email, password1));

        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı adı veya email zaten kayıtlı!");
            return false;
        }

        showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kayıt başarılı! Giriş yapabilirsiniz.");
        return true;
    }

    public boolean resetPassword(String username, String email, String newPassword, String confirmPassword, Stage stage) {
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Yeni şifreler aynı değil!");
            return false;
        }

        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Tüm alanları doldurun!");
            return false;
        }

        boolean success = UserDatabase.resetPassword(username, email, newPassword);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Şifre başarıyla güncellendi!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı adı veya email hatalı!");
        }

        return success;
    }
}