package org.example.bngsocial.Controllers;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.bngsocial.Models.User;
import org.example.bngsocial.Models.UserDatabase;
import org.example.bngsocial.Screens.mainScreen;
import org.example.bngsocial.Screens.signScreen;

public class MainController {

    public void login(String username, String password, Stage stage) {

        User u = UserDatabase.login(username, password);

        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Hatalı kullanıcı adı veya şifre!");
            alert.show();
            return;
        }

        new mainScreen().show(stage, u.getName());
    }

    public void register(String name, String username, String email, String p1, String p2, Stage stage) {

        if (!p1.equals(p2)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Şifreler aynı değil!");
            a.show();
            return;
        }

        if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Tüm alanları doldurun!");
            a.show();
            return;
        }

        boolean success = UserDatabase.addUser(new User(name, username, email, p1));

        if (!success) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Kullanıcı adı veya email zaten kayıtlı!");
            a.show();
            return;
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Kayıt başarılı!");
        a.show();

        new signScreen().show(stage);
    }

    public void resetPassword(String username, String email, String newPassword, String confirmPassword, Stage stage) {

        if (!newPassword.equals(confirmPassword)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Yeni şifreler aynı değil!");
            a.show();
            return;
        }

        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Tüm alanları doldurun!");
            a.show();
            return;
        }

        boolean success = UserDatabase.resetPassword(username, email, newPassword);

        Alert a = new Alert(Alert.AlertType.INFORMATION);

        if (success) {
            a.setContentText("Şifre başarıyla güncellendi!");
        } else {
            a.setContentText("Kullanıcı adı veya email hatalı!");
        }

        a.show();
        new signScreen().show(stage);
    }
}