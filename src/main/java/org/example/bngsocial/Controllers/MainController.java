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

        new mainScreen().show(stage, username);
    }

    public void register(String username, String p1, String p2, Stage stage) {

        if (!p1.equals(p2)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Şifreler aynı değil!");
            a.show();
            return;
        }

        boolean success = UserDatabase.addUser(new User(username, p1));

        if (!success) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Kullanıcı adı zaten var!");
            a.show();
            return;
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Kayıt başarılı!");
        a.show();

        new signScreen().show(stage);
    }

    public void resetPassword(String username, Stage stage) {

        boolean found = UserDatabase.resetPassword(username);

        Alert a = new Alert(Alert.AlertType.INFORMATION);

        if (found) {
            a.setContentText("Şifre sıfırlama isteği kaydedildi.");
        } else {
            a.setContentText("Kullanıcı bulunamadı!");
        }

        a.show();
        new signScreen().show(stage);
    }
}
