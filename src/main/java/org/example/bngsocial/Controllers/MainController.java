package org.example.bngsocial.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.bngsocial.Models.User;
import org.example.bngsocial.Models.UserDatabase;
import org.example.bngsocial.Controllers.RootController;

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

    // MainController.java içinde
    public boolean login(String username, String password, Stage stage) {
        // 1. Kullanıcıyı Veritabanından Doğrula
        User user = UserDatabase.login(username, password);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Hatalı kullanıcı adı veya şifre!");
            return false;
        }

        System.out.println("Giriş Başarılı! ID: " + user.getId());

        try {
            // --- DEĞİŞİKLİK 1: ARTIK ROOTLAYOUT YÜKLENİYOR ---
            String fxmlPath = "/org/example/bngsocial/views/RootLayout.fxml";

            // Dosya var mı kontrol et (Hata ayıklama için önemli)
            if (getClass().getResource(fxmlPath) == null) {
                throw new IOException("FXML dosyası bulunamadı! Yol: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // --- DEĞİŞİKLİK 2: CONTROLLER TÜRÜ DEĞİŞTİ ---
            // Artık MainScreenController değil, RootController kullanıyoruz.
            RootController controller = loader.getController();

            if (controller == null) {
                throw new Exception("RootLayout.fxml dosyasında fx:controller tanımlanmamış!");
            }

            // --- DEĞİŞİKLİK 3: VERİ GÖNDERİMİ ---
            // RootController sadece ID'yi alıp diğer sayfalara dağıtır.
            controller.setUserData(user.getId(),user.getUsername());

            // 4. Sahneyi Göster
            Scene scene = new Scene(root);

            // CSS Yükleme
            try {
                scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/main_styles.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS yüklenemedi: " + e.getMessage());
            }

            stage.setScene(scene);
            stage.setTitle("BNGSocial"); // Başlık artık genel
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Sistem Hatası",
                    "Ana ekran yüklenirken hata oluştu:\n" + e.getMessage());
            return false;
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