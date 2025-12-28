package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import java.sql.*;

public class SharingController {

    // FXML Değişkenleri
    @FXML private Label welcomeLabel;
    @FXML private TextArea postArea;
    @FXML private ImageView imgOnizleme;
    @FXML private Label lblDurum;
    @FXML private Label lblDosyaAdi;
    @FXML private Button btnResimSec;  // FXML'de fx:id="btnResimSec" olmalı
    @FXML private Button postButton;   // FXML'de fx:id="postButton" olmalı
    @FXML private Button logoutButton; // FXML'de fx:id="logoutButton" olmalı
    @FXML private VBox feedContainer;  // Postların dizileceği alan

    // Değişkenler
    private String username;
    private int currentUserId;
    private String selectedImagePath = null;

    // Veritabanı Bağlantısı
    private static final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    // --- 1. SET USER DATA METODU (MainController'dan çağrılır) ---
    // setUsername YERİNE BUNU KULLANIYORUZ
    public void setUserData(int userId, String username) {
        this.currentUserId = userId;
        this.username = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Hoş geldin, " + username);
        }

        // DÜZELTME BURADA:
        // Eğer feedContainer null değilse postları yükle.
        // (Sadece paylaşım yapma ekranında feedContainer olmayabilir, bu yüzden kontrol ediyoruz)
        if (feedContainer != null) {
            loadPosts();
        }
    }

    // --- 2. RESİM SEÇME METODU ---
    @FXML
    private void handleResimSec() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Fotoğraf Seç");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg")
        );

        // Pencereyi btnResimSec üzerinden buluyoruz
        Stage stage = (Stage) btnResimSec.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            if (lblDosyaAdi != null) lblDosyaAdi.setText(file.getName());

            imgOnizleme.setImage(new Image(file.toURI().toString()));
            imgOnizleme.setVisible(true);
            imgOnizleme.setManaged(true);
        }
    }

    // --- 3. PAYLAŞIM YAPMA METODU ---
    @FXML
    private void handlePost() {
        String postText = postArea.getText().trim();

        if (postText.isEmpty() && selectedImagePath == null) {
            lblDurum.setText("Lütfen yazı yazın veya fotoğraf ekleyin!");
            lblDurum.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "INSERT INTO Gonderiler (user_id, content_text, image_path) VALUES (?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, postText);
            pstmt.setString(3, selectedImagePath);


            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                lblDurum.setText("Paylaşıldı!");
                lblDurum.setStyle("-fx-text-fill: green;");

                // Temizlik
                postArea.clear();
                imgOnizleme.setImage(null);
                imgOnizleme.setVisible(false);
                imgOnizleme.setManaged(false);
                if (lblDosyaAdi != null) lblDosyaAdi.setText("");
                selectedImagePath = null;

                // Akışı yenile
                loadPosts();
            }
        } catch (SQLException e) {
            lblDurum.setText("Hata: " + e.getMessage());
            e.printStackTrace();
        }


    }

    // --- 5. GÖNDERİLERİ YÜKLEME (FEED) ---
    private void loadPosts() {
        // EKSTRA GÜVENLİK ÖNLEMİ:
        if (feedContainer == null) {
            // Eğer kutu yoksa hiçbir şey yapma, metodu bitir.
            return;
        }

        feedContainer.getChildren().clear();

        String sql = "SELECT p.post_id, p.content_text, p.image_path, p.created_at, u.username " +
                "FROM Gonderiler p " +
                "JOIN Kullanicilar u ON p.user_id = u.user_id " +
                "WHERE p.user_id = ? " +
                "ORDER BY p.created_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentUserId);

            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                feedContainer.getChildren().add(createCard(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Post Kartı Tasarımı
    private VBox createCard(ResultSet rs) throws SQLException {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        // Kullanıcı Adı
        Label name = new Label(rs.getString("username"));
        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3;");

        // İçerik
        Label content = new Label(rs.getString("content_text"));
        content.setWrapText(true);

        card.getChildren().addAll(name, content);

        // Resim Varsa Ekle
        String imgPath = rs.getString("image_path");
        if (imgPath != null) {
            try {
                File f = new File(imgPath);
                if (f.exists()) {
                    ImageView iv = new ImageView(new Image(f.toURI().toString()));
                    iv.setFitWidth(400);
                    iv.setPreserveRatio(true);
                    card.getChildren().add(iv);
                }
            } catch (Exception e) {}
        }

        // BUTONLAR (Veritabanından veri çekmeden statik olarak ekliyoruz)
        // Eğer butonları hiç istemiyorsan bu kısmı komple silebilirsin.
        HBox actions = new HBox(10);
        actions.getChildren().addAll(new Button("👍 Beğen"), new Button("👎 Beğenme"));
        card.getChildren().add(actions);

        return card;
    }
}