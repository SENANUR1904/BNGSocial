package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.bngsocial.Models.User;
import org.example.bngsocial.Utils.TxtManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendsController {

    // --- FXML BİLEŞENLERİ (FriendsView.fxml ile eşleşmeli) ---
    @FXML private TextField txtSourceId;
    @FXML private TextField txtTargetId;
    @FXML private Label lblTxtStatus;
    @FXML private TextField txtProcessId;
    @FXML private VBox txtResultBox;

    // DB için yeni FXML bileşeni
    @FXML private VBox dbFriendsContainer;

    private int currentUserId;
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public void setUserId(int userId) {
        this.currentUserId = userId;

        // TXT verilerini yükle
        TxtManager.init();

        // DB arkadaşlarını yükle
        loadDbFriends();
    }

    // YENİ METOT: DB'den arkadaşları yükle
    private void loadDbFriends() {
        dbFriendsContainer.getChildren().clear();

        // 1. Önce YAKIN ARKADAŞLARI getir
        String closeFriendsSql = "SELECT u.user_id, u.username, u.full_name " +
                "FROM Kullanicilar u " +
                "JOIN Yakin_Arkadas ya ON u.user_id = ya.close_friend_id " +
                "WHERE ya.user_id = ? " +
                "ORDER BY u.username";

        // 2. Sonra NORMAL ARKADAŞLARI getir (Yakın arkadaş olmayanlar)
        String normalFriendsSql = "SELECT DISTINCT u.user_id, u.username, u.full_name " +
                "FROM Kullanicilar u " +
                "JOIN Arkadaslik f ON (u.user_id = f.sender_id OR u.user_id = f.receiver_id) " +
                "LEFT JOIN Yakin_Arkadas ya ON (u.user_id = ya.close_friend_id AND ya.user_id = ?) " +
                "WHERE ((f.sender_id = ? AND f.receiver_id = u.user_id) " +
                "       OR (f.receiver_id = ? AND f.sender_id = u.user_id)) " +
                "AND f.status = 'ACCEPTED' " +
                "AND u.user_id != ? " +
                "AND ya.close_friend_id IS NULL " +  // Yakın arkadaş olmayanlar
                "ORDER BY u.username";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // YAKIN ARKADAŞLAR
            try (PreparedStatement ps = conn.prepareStatement(closeFriendsSql)) {
                ps.setInt(1, currentUserId);
                ResultSet rs = ps.executeQuery();

                if (rs.isBeforeFirst()) {
                    // Başlık ekle
                    Label closeFriendsTitle = new Label("★ YAKIN ARKADAŞLAR");
                    closeFriendsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e67e22; -fx-padding: 0 0 10 0;");
                    dbFriendsContainer.getChildren().add(closeFriendsTitle);

                    while (rs.next()) {
                        int friendId = rs.getInt("user_id");
                        String username = rs.getString("username");
                        String fullName = rs.getString("full_name");
                        String displayText = fullName != null && !fullName.isEmpty() ?
                                fullName + " (@" + username + ")" :
                                "@" + username;

                        dbFriendsContainer.getChildren().add(
                                createDbFriendRow(friendId, displayText, true)
                        );
                    }

                    // Ayırıcı ekle
                    dbFriendsContainer.getChildren().add(new Separator());
                }
            }

            // NORMAL ARKADAŞLAR
            try (PreparedStatement ps = conn.prepareStatement(normalFriendsSql)) {
                ps.setInt(1, currentUserId);
                ps.setInt(2, currentUserId);
                ps.setInt(3, currentUserId);
                ps.setInt(4, currentUserId);
                ResultSet rs = ps.executeQuery();

                if (rs.isBeforeFirst()) {
                    // Başlık ekle
                    Label normalFriendsTitle = new Label("✓ ARKADAŞLAR");
                    normalFriendsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #27ae60; -fx-padding: 10 0 10 0;");
                    dbFriendsContainer.getChildren().add(normalFriendsTitle);

                    while (rs.next()) {
                        int friendId = rs.getInt("user_id");
                        String username = rs.getString("username");
                        String fullName = rs.getString("full_name");
                        String displayText = fullName != null && !fullName.isEmpty() ?
                                fullName + " (@" + username + ")" :
                                "@" + username;

                        dbFriendsContainer.getChildren().add(
                                createDbFriendRow(friendId, displayText, false)
                        );
                    }
                }
            }

            // Eğer hiç arkadaş yoksa
            if (dbFriendsContainer.getChildren().isEmpty()) {
                Label noFriendsLabel = new Label("Henüz arkadaşınız bulunmuyor.");
                noFriendsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-padding: 20;");
                dbFriendsContainer.getChildren().add(noFriendsLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Veritabanı bağlantı hatası: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            dbFriendsContainer.getChildren().add(errorLabel);
        }
    }

    // Yardımcı Metot: DB arkadaş satırı oluşturur
    private HBox createDbFriendRow(int friendId, String name, boolean isCloseFriend) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(50);
        row.setStyle("-fx-background-color: white; -fx-padding: 10 15; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1); " +
                "-fx-border-color: #eee; -fx-border-width: 1; -fx-border-radius: 8;");

        // İkon
        Label icon = new Label(isCloseFriend ? "★" : "✓");
        icon.setStyle("-fx-font-size: 16px; -fx-text-fill: " +
                (isCloseFriend ? "#e67e22;" : "#27ae60;"));
        icon.setMinWidth(25);

        // Arkadaş adı
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblName.setPrefWidth(250);

        // Butonlar için container
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setStyle("-fx-padding: 0 0 0 20;");

        // Çıkar butonu
        Button btnRemove = new Button("Çıkar");
        btnRemove.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #d32f2f; -fx-font-weight: bold; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 5 15;");
        btnRemove.setOnAction(e -> removeDbFriend(friendId, isCloseFriend));

        // Yakın arkadaş durumunu değiştir butonu
        String statusButtonText = isCloseFriend ? "Normal Yap" : "Yakın Yap";
        Button btnStatus = new Button(statusButtonText);
        btnStatus.setStyle("-fx-background-color: " +
                (isCloseFriend ? "#fff3e0;" : "#e8f5e8;") +
                " -fx-text-fill: " +
                (isCloseFriend ? "#f57c00;" : "#388e3c;") +
                " -fx-font-weight: bold; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 5 15;");
        btnStatus.setOnAction(e -> toggleCloseFriend(friendId, isCloseFriend));

        buttonContainer.getChildren().addAll(btnStatus, btnRemove);

        // HBox'i doldur
        row.getChildren().addAll(icon, lblName, buttonContainer);

        // HBox'in genişlemesini sağla
        HBox.setHgrow(lblName, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(buttonContainer, javafx.scene.layout.Priority.ALWAYS);

        return row;
    }

    // DB'den arkadaş çıkarma
    private void removeDbFriend(int friendId, boolean isCloseFriend) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // Önce yakın arkadaş ise onu sil
            if (isCloseFriend) {
                String deleteCloseFriendSql = "DELETE FROM Yakin_Arkadas WHERE user_id = ? AND close_friend_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteCloseFriendSql)) {
                    ps.setInt(1, currentUserId);
                    ps.setInt(2, friendId);
                    ps.executeUpdate();
                }
            }

            // Sonra arkadaşlığı sil
            String deleteFriendshipSql = "DELETE FROM Arkadaslik WHERE " +
                    "(sender_id = ? AND receiver_id = ?) OR " +
                    "(sender_id = ? AND receiver_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(deleteFriendshipSql)) {
                ps.setInt(1, currentUserId);
                ps.setInt(2, friendId);
                ps.setInt(3, friendId);
                ps.setInt(4, currentUserId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Arkadaşlık silindi: " + friendId);
                }
            }

            // Listeyi yenile
            loadDbFriends();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Arkadaş silinirken hata oluştu: " + e.getMessage());
        }
    }

    // Yakın arkadaş durumunu değiştir
    private void toggleCloseFriend(int friendId, boolean currentlyCloseFriend) {
        String sql;

        if (currentlyCloseFriend) {
            // Yakın arkadaşlığı kaldır (KARŞILIKLI)
            sql = "DELETE FROM Yakin_Arkadas WHERE " +
                    "(user_id = ? AND close_friend_id = ?) OR " +
                    "(user_id = ? AND close_friend_id = ?)";
        } else {
            // Normal arkadaşı yakın arkadaş yap (KARŞILIKLI)
            sql = "INSERT INTO Yakin_Arkadas (user_id, close_friend_id, added_at) VALUES " +
                    "(?, ?, GETDATE()), " +
                    "(?, ?, GETDATE())";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (currentlyCloseFriend) {
                // Silme işlemi için parametreleri ayarla
                ps.setInt(1, currentUserId);
                ps.setInt(2, friendId);
                ps.setInt(3, friendId);
                ps.setInt(4, currentUserId);
            } else {
                // Ekleme işlemi için parametreleri ayarla
                ps.setInt(1, currentUserId);
                ps.setInt(2, friendId);
                ps.setInt(3, friendId);
                ps.setInt(4, currentUserId);
            }

            int rowsAffected = ps.executeUpdate();

            String message = currentlyCloseFriend ?
                    "Arkadaş normal arkadaş olarak işaretlendi! (Karşılıklı)" :
                    "Arkadaş yakın arkadaş olarak işaretlendi! (Karşılıklı)";

            showAlert("Başarılı", message);
            loadDbFriends(); // Listeyi yenile

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "İşlem sırasında hata oluştu: " + e.getMessage());
        }
    }

    // Yardımcı metot: Alert gösterme
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- AŞAĞIDAKİ METOTLAR DEĞİŞMEDEN KALACAK ---
    @FXML
    private void handleCheckFriendship() {
        // Aynı kalsın
        try {
            int src = Integer.parseInt(txtSourceId.getText());
            int trg = Integer.parseInt(txtTargetId.getText());
            String status = TxtManager.arkadasMi(src, trg);
            lblTxtStatus.setText(status);
            if(status.contains("Değil")) lblTxtStatus.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            else lblTxtStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } catch (NumberFormatException e) {
            lblTxtStatus.setText("Lütfen sadece sayı giriniz!");
        }
    }

    @FXML
    private void handleShowFriends() {
        txtResultBox.getChildren().clear();
        try {
            int id = Integer.parseInt(txtProcessId.getText());
            int idx = TxtManager.getUserIndex(id);
            if(idx == -1) {
                txtResultBox.getChildren().add(new Label("❌ Kullanıcı bulunamadı (ID: " + id + ")"));
                return;
            }

            Label title = new Label("ARKADAŞ LİSTESİ (" + id + ")");
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            txtResultBox.getChildren().add(title);

            // Tüm arkadaşları topla
            List<FriendScore> allFriends = new ArrayList<>();

            for(int i = 0; i < TxtManager.txtUsers.size(); i++) {
                int relationType = TxtManager.relationMatrix[idx][i];
                if(relationType == 1 || relationType == 2) {
                    User u = TxtManager.txtUsers.get(i);
                    double score = TxtManager.iliskiPuaniHesapla(u.getId(), id);
                    boolean isClose = (relationType == 2);
                    allFriends.add(new FriendScore(u.getName(), score, isClose));
                }
            }

            if(allFriends.isEmpty()) {
                txtResultBox.getChildren().add(new Label("Arkadaş bulunamadı."));
                return;
            }

            // Tüm arkadaşları puanlarına göre sırala (büyükten küçüğe)
            bubbleSortFriends(allFriends);

            // YAKIN ARKADAŞLARI göster
            txtResultBox.getChildren().add(new Label("--- Yakın Arkadaşlar ---"));
            boolean hasCloseFriends = false;
            for(FriendScore fs : allFriends) {
                if(fs.isCloseFriend) {
                    addResultLabel("★ " + fs.name + " (Puan: " + (int)fs.score + ")");
                    hasCloseFriends = true;
                }
            }
            if(!hasCloseFriends) {
                txtResultBox.getChildren().add(new Label("(Yok)"));
            }

            // NORMAL ARKADAŞLARI göster
            txtResultBox.getChildren().add(new Label("\n--- Arkadaşlar ---"));
            boolean hasNormalFriends = false;
            for(FriendScore fs : allFriends) {
                if(!fs.isCloseFriend) {
                    addResultLabel("• " + fs.name + " (Puan: " + (int)fs.score + ")");
                    hasNormalFriends = true;
                }
            }
            if(!hasNormalFriends) {
                txtResultBox.getChildren().add(new Label("(Yok)"));
            }

        } catch (NumberFormatException e) {
            txtResultBox.getChildren().add(new Label("Hata: ID giriniz."));
        }
    }

    @FXML
    private void handleSuggestFriends() {
        // Aynı kalsın
        txtResultBox.getChildren().clear();
        try {
            int id = Integer.parseInt(txtProcessId.getText());
            Label title = new Label("ÖNERİLEN ARKADAŞLAR");
            title.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
            txtResultBox.getChildren().add(title);
            List<String> results = TxtManager.arkadasOner(id);
            if(results.isEmpty()) {
                txtResultBox.getChildren().add(new Label("Öneri bulunamadı."));
            } else {
                for (String s : results) addResultLabel(s);
            }
        } catch (Exception e) {
            txtResultBox.getChildren().add(new Label("Hata: ID giriniz."));
        }
    }

    @FXML
    private void handleSuggestRemove() {
        // Aynı kalsın
        txtResultBox.getChildren().clear();
        try {
            int id = Integer.parseInt(txtProcessId.getText());
            Label title = new Label("ÇIKARILMASI ÖNERİLENLER");
            title.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
            txtResultBox.getChildren().add(title);
            List<String> results = TxtManager.arkadasCikarmaOner(id);
            if(results.isEmpty()) {
                txtResultBox.getChildren().add(new Label("Öneri yok (Arkadaş listesi boş olabilir)."));
            } else {
                for (String s : results) addResultLabel(s);
            }
        } catch (Exception e) {
            txtResultBox.getChildren().add(new Label("Hata: ID giriniz."));
        }
    }

    private void addResultLabel(String text) {
        // Aynı kalsın
        Label l = new Label(text);
        l.setStyle("-fx-padding: 5; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");
        l.setMaxWidth(Double.MAX_VALUE);
        txtResultBox.getChildren().add(l);
    }

    // --- YENİ EKLENEN YARDIMCI SINIF VE METOTLAR ---

    // Bubble sort ile arkadaşları puanlarına göre sırala (büyükten küçüğe)
    private void bubbleSortFriends(List<FriendScore> friends) {
        int n = friends.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (friends.get(j).score < friends.get(j + 1).score) {
                    // Swap
                    FriendScore temp = friends.get(j);
                    friends.set(j, friends.get(j + 1));
                    friends.set(j + 1, temp);
                }
            }
        }
    }

    // İç sınıf: Arkadaş ve puan bilgisi
    private static class FriendScore {
        String name;
        double score;
        boolean isCloseFriend;

        FriendScore(String name, double score, boolean isCloseFriend) {
            this.name = name;
            this.score = score;
            this.isCloseFriend = isCloseFriend;
        }
    }
}