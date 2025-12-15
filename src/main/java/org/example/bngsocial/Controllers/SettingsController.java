package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SettingsController {

    // --- FXML TANIMLAMALARI ---
    @FXML private Circle profileCircle;     // Profil Resmi Yuvarlağı
    @FXML private Label lblInitials;        // Baş Harf (Resim yoksa)
    @FXML private Label lblUsername;        // Kullanıcı Adı
    @FXML private Label lblFullName;        // Ad Soyad
    @FXML private Label lblEmail;           // E-posta
    @FXML private Hyperlink lnkRemovePhoto; // "Kaldır" Linki
    @FXML private Label lblPostCount;       // Gönderi Sayısı
    @FXML private Button btnLogout;         // Çıkış Butonu

    // --- DEĞİŞKENLER ---
    private int currentUserId;
    private String currentPhotoPath = null; // Fotoğrafı büyütmek için yolu hafızada tutuyoruz

    // Veritabanı Bağlantısı
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    // --- ANA GİRİŞ NOKTASI ---
    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadUserProfile();
    }

    // --- 1. PROFİL BİLGİLERİNİ YÜKLEME ---
    private void loadUserProfile() {
        // SQL: Kullanıcı bilgilerini ve gönderi sayısını (post_count) tek seferde çekiyoruz
        String sql = "SELECT username, full_name, email, profile_photo, " +
                "(SELECT COUNT(*) FROM Gonderiler WHERE user_id = ?) as post_count " +
                "FROM Kullanicilar WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentUserId); // Alt sorgu için
            ps.setInt(2, currentUserId); // Ana sorgu için

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Metin Bilgileri
                String username = rs.getString("username");
                lblUsername.setText("@" + username);
                lblFullName.setText(rs.getString("full_name"));
                lblEmail.setText(rs.getString("email"));

                // İstatistik
                lblPostCount.setText(String.valueOf(rs.getInt("post_count")));

                // Fotoğraf Yolu
                this.currentPhotoPath = rs.getString("profile_photo");

                // Fotoğraf Kontrolü
                if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                    File file = new File(currentPhotoPath);
                    if (file.exists()) {
                        // Fotoğraf var: Daireye döşe, Harfi gizle, Kaldır butonunu aç
                        Image img = new Image(file.toURI().toString());
                        profileCircle.setFill(new ImagePattern(img));
                        lblInitials.setVisible(false);
                        lnkRemovePhoto.setVisible(true);
                    } else {
                        // Yol var ama dosya silinmişse varsayılana dön
                        setDefaultAvatar(username);
                    }
                } else {
                    // Fotoğraf hiç yoksa varsayılan
                    setDefaultAvatar(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Varsayılan Avatar (Gri Arkaplan + Baş Harf)
    private void setDefaultAvatar(String username) {
        profileCircle.setFill(Color.web("#bdc3c7")); // Gri renk
        lblInitials.setVisible(true);
        lnkRemovePhoto.setVisible(false); // Foto yoksa 'Kaldır' butonu gizli
        currentPhotoPath = null;

        if (username != null && !username.isEmpty()) {
            lblInitials.setText(username.substring(0, 1).toUpperCase());
        } else {
            lblInitials.setText("?");
        }
    }

    // --- 2. FOTOĞRAFI BÜYÜTME (POP-UP) ---
    @FXML
    private void handleViewPhoto() {
        // Eğer fotoğraf yoksa açma
        if (currentPhotoPath == null || currentPhotoPath.isEmpty()) return;

        File file = new File(currentPhotoPath);
        if (!file.exists()) return;

        // Yeni şeffaf pencere oluştur
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Arkaya tıklamayı engelle
        popupStage.initStyle(StageStyle.TRANSPARENT);

        // Resmi Göster
        ImageView imageView = new ImageView(new Image(file.toURI().toString()));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(500);
        imageView.setFitWidth(500);

        // Kapsayıcı (Siyah yarı saydam arka plan)
        StackPane root = new StackPane(imageView);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 20;");

        // Tıklayınca kapat
        root.setOnMouseClicked(event -> popupStage.close());

        Scene scene = new Scene(root, Color.TRANSPARENT);
        popupStage.setScene(scene);

        // Ana pencerenin ortasında aç
        if (profileCircle.getScene() != null && profileCircle.getScene().getWindow() != null) {
            popupStage.initOwner(profileCircle.getScene().getWindow());
        }

        popupStage.centerOnScreen();
        popupStage.showAndWait();
    }

    // --- 3. FOTOĞRAF YÜKLEME (DEĞİŞTİR) ---
    @FXML
    private void handlePhotoUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Profil Fotoğrafı Seç");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) btnLogout.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            updateProfilePhotoInDB(file.getAbsolutePath());
        }
    }

    // --- 4. FOTOĞRAF KALDIRMA ---
    @FXML
    private void handlePhotoRemove() {
        // Veritabanına NULL göndererek fotoğrafı kaldırıyoruz
        updateProfilePhotoInDB(null);
    }

    // Ortak Güncelleme Metodu
    private void updateProfilePhotoInDB(String path) {
        String sql = "UPDATE Kullanicilar SET profile_photo = ? WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, path); // null ise DB'ye NULL gider
            ps.setInt(2, currentUserId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                loadUserProfile(); // Ekranı yenile
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "İşlem sırasında hata oluştu.");
            alert.show();
        }
    }

    // --- 5. ÇIKIŞ YAPMA ---
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Çıkış yapmak istediğinize emin misiniz?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            goToLoginScreen();
        }
    }

    // --- 6. HESAP SİLME ---
    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Hesabınız kalıcı olarak silinecek!\nBu işlem geri alınamaz. Emin misiniz?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Hesap Silme Onayı");

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                // Kullanıcıyı sil (Varsa ilişkili veriler de silinmeli - Cascade yoksa manuel silinmeli)
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Kullanicilar WHERE user_id = ?");
                ps.setInt(1, currentUserId);
                ps.executeUpdate();

                System.out.println("Hesap silindi.");
                goToLoginScreen();

            } catch (SQLException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR, "Hesap silinirken hata oluştu: " + e.getMessage());
                error.show();
            }
        }
    }

    // --- GİRİŞ EKRANINA DÖNÜŞ ---
    private void goToLoginScreen() {
        try {
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/signScreen.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());
            } catch (Exception e) { /* CSS yoksa devam et */ }

            currentStage.setScene(scene);
            currentStage.setTitle("Giriş Yap");
            currentStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}