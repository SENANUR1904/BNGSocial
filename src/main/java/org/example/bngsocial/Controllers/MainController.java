package org.example.bngsocial.Controllers;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.bngsocial.Models.User;
import org.example.bngsocial.Models.UserDatabase;
import org.example.bngsocial.Screens.mainScreen;
import org.example.bngsocial.Screens.signScreen;

public class MainController {

    public void login(String username, String password, Stage stage) {
        User user = UserDatabase.login(username, password);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Hatalı kullanıcı adı veya şifre!");
            return;
        }

        new mainScreen().show(stage, user.getName());
    }

    public void register(String name, String username, String email, String password1, String password2, Stage stage) {
        if (!password1.equals(password2)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Şifreler aynı değil!");
            return;
        }

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password1.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Tüm alanları doldurun!");
            return;
        }

        boolean success = UserDatabase.addUser(new User(name, username, email, password1));

        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı adı veya email zaten kayıtlı!");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kayıt başarılı! Giriş yapabilirsiniz.");
        new signScreen().show(stage);
    }

    public void resetPassword(String username, String email, String newPassword, String confirmPassword, Stage stage) {
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Yeni şifreler aynı değil!");
            return;
        }

        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Tüm alanları doldurun!");
            return;
        }

        boolean success = UserDatabase.resetPassword(username, email, newPassword);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Şifre başarıyla güncellendi!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı adı veya email hatalı!");
        }

        new signScreen().show(stage);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}