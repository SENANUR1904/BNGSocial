package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.example.bngsocial.Models.User;
import org.example.bngsocial.Utils.TxtManager;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class NetworkController {

    @FXML private Pane graphPane;
    @FXML private VBox nonFriendsContainer;
    @FXML private ScrollPane nonFriendsScrollPane;
    @FXML private VBox txtRankingContainer;
    @FXML private TabPane networkTabPane;

    private int currentUserId;
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    private boolean txtRankingLoaded = false; // Butona basılıp basılmadığını kontrol etmek için

    public void setUserId(int userId) {
        this.currentUserId = userId;
        // Sadece ilk tab açıldığında arkadaş olmayanları yükle
        if (networkTabPane != null) {
            networkTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
                if (newTab != null) {
                    String tabText = newTab.getText();
                    if (tabText.equals("Arkadaş Ekle")) {
                        loadNonFriendsFromDB();
                    } else if (tabText.equals("TXT İlişki Sıralaması")) {
                        // TXT tab'ı seçildiğinde sadece butona basılmadıysa boş bırak
                        if (!txtRankingLoaded) {
                            clearTxtRankingWithMessage();
                        }
                    }
                }
            });
        }
    }

    // TXT sıralama tab'ını boşalt ve mesaj göster
    private void clearTxtRankingWithMessage() {
        if (txtRankingContainer == null) return;
        txtRankingContainer.getChildren().clear();

        Label instructionLabel = new Label("Lütfen 'genelSiraTxt' butonuna tıklayarak sıralamayı görüntüleyin.");
        instructionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-font-size: 14px; -fx-padding: 20;");
        txtRankingContainer.getChildren().add(instructionLabel);
    }

    // --- ARKADAŞ OLMAYANLARI DB'DEN YÜKLE ---
    private void loadNonFriendsFromDB() {
        if (nonFriendsContainer == null) return;
        nonFriendsContainer.getChildren().clear();

        String sql = "SELECT u.user_id, u.username, u.full_name, u.profile_photo " +
                "FROM Kullanicilar u " +
                "WHERE u.user_id != ? " +
                "AND u.is_deleted = 0 " +
                "AND u.user_id NOT IN (" +
                "    SELECT CASE " +
                "        WHEN sender_id = ? THEN receiver_id " +
                "        ELSE sender_id " +
                "    END " +
                "    FROM Arkadaslik " +
                "    WHERE (sender_id = ? OR receiver_id = ?) AND status = 'ACCEPTED'" +
                ") " +
                "ORDER BY u.username";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setInt(3, currentUserId);
            ps.setInt(4, currentUserId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int targetId = rs.getInt("user_id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String profilePhoto = rs.getString("profile_photo");

                nonFriendsContainer.getChildren().add(
                        createNonFriendRow(targetId, username, fullName, profilePhoto)
                );
            }

            if (nonFriendsContainer.getChildren().isEmpty()) {
                Label noFriendsLabel = new Label("Tüm kullanıcılar arkadaşınız!");
                noFriendsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-padding: 20;");
                nonFriendsContainer.getChildren().add(noFriendsLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Veritabanı bağlantı hatası");
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            nonFriendsContainer.getChildren().add(errorLabel);
        }
    }

    // --- ARKADAŞ OLMAYAN SATIRI OLUŞTUR ---
    private HBox createNonFriendRow(int userId, String username, String fullName, String profilePhoto) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-padding: 10 15; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1); " +
                "-fx-border-color: #eee; -fx-border-width: 1; -fx-border-radius: 8;");
        row.setPrefHeight(50);

        // Profil fotoğrafı
        Circle avatar = new Circle(20);
        boolean imageLoaded = false;

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            File photoFile = new File(profilePhoto);
            if (photoFile.exists()) {
                try {
                    avatar.setFill(new ImagePattern(new Image(photoFile.toURI().toString())));
                    imageLoaded = true;
                } catch (Exception e) {
                    System.err.println("Profil fotoğrafı yüklenemedi: " + profilePhoto);
                }
            }
        }

        if (!imageLoaded) {
            String firstLetter = username.substring(0, 1).toUpperCase();
            Text initialText = new Text(firstLetter);
            initialText.setFont(Font.font("System", FontWeight.BOLD, 14));
            initialText.setFill(Color.WHITE);

            avatar.setFill(Color.web(getColorForUsername(username)));
            StackPane avatarPane = new StackPane(avatar, initialText);
            row.getChildren().add(avatarPane);
        } else {
            row.getChildren().add(avatar);
        }

        // Kullanıcı bilgileri
        VBox userInfo = new VBox(2);
        Label nameLabel = new Label(fullName != null && !fullName.isEmpty() ? fullName : username);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label usernameLabel = new Label("@" + username);
        usernameLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        userInfo.getChildren().addAll(nameLabel, usernameLabel);
        row.getChildren().add(userInfo);

        // Boş alan
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().add(spacer);

        // Ekle butonu
        Button addButton = new Button("Arkadaş Ekle");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 15;");
        addButton.setOnAction(e -> addFriendToDB(userId));

        row.getChildren().add(addButton);

        return row;
    }

    // --- ARKADAŞ EKLE (DB'YE KAYDET) ---
    private void addFriendToDB(int friendId) {
        String sql = "INSERT INTO Arkadaslik (sender_id, receiver_id, status, created_at) " +
                "VALUES (?, ?, 'ACCEPTED', GETDATE())";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentUserId);
            ps.setInt(2, friendId);
            ps.executeUpdate();

            showAlert("Başarılı", "Arkadaş başarıyla eklendi!");
            loadNonFriendsFromDB();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Arkadaş eklenirken hata oluştu: " + e.getMessage());
        }
    }


    // --- GENEL SIRALAMA TXT (Tüm ikili ilişkiler - KARŞILIKLI) ---

    // --- GENEL SIRALAMA TXT (Tüm ikili ilişkiler - KARŞILIKLI) ---
    // --- GENEL SIRALAMA TXT (Tüm ikili ilişkiler - HERKES) ---
    @FXML
    private void handleGeneralRankingTxt() {
        if (txtRankingContainer == null) return;
        txtRankingContainer.getChildren().clear();

        // Bayrağı true yap (butona basıldı)
        txtRankingLoaded = true;

        // TxtManager verilerini yükle
        TxtManager.init();

        List<User> users = TxtManager.txtUsers;
        if (users.isEmpty()) {
            Label noDataLabel = new Label("TXT dosyasından veri bulunamadı.");
            noDataLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-padding: 20;");
            txtRankingContainer.getChildren().add(noDataLabel);
            return;
        }

        // Başlık
        Label title = new Label("📊 TXT Genel İlişki Sıralaması (Tüm Olası Çiftler)");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-padding: 0 0 15 0;");
        txtRankingContainer.getChildren().add(title);

        Label description = new Label("Kişiler arası ilişki durumu ve etkileşim puanları (Yüksekten düşüğe):");
        description.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-padding: 0 0 15 0;");
        txtRankingContainer.getChildren().add(description);

        // Verileri saklayacak listeler
        List<String> user1List = new ArrayList<>();
        List<String> user2List = new ArrayList<>();
        List<Integer> statusList = new ArrayList<>();
        List<Double> scoreList = new ArrayList<>();

        int n = users.size();

        // --- İKİLİ DÖNGÜ: HERKESİN HERKESLE PUANI ---
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Kişi kendisiyle kıyaslanmaz
                if (i == j) continue;

                // NOT: Burada "TxtManager.relationMatrix[i][j] > 0" kontrolünü KALDIRDIK.
                // Böylece arkadaş olmayanlar da listeye girer.

                int user1Id = users.get(i).getId();
                int user2Id = users.get(j).getId();

                // Matris yüklü değilse varsayılan 0 al, yüklüyse durumu al
                int status = 0;
                if (TxtManager.relationMatrix != null &&
                        i < TxtManager.relationMatrix.length &&
                        j < TxtManager.relationMatrix[0].length) {
                    status = TxtManager.relationMatrix[i][j];
                }

                // İlişki puanını hesapla (Arkadaş olmasalar bile etkileşim puanı olabilir)
                double score = TxtManager.iliskiPuaniHesapla(user1Id, user2Id);

                // Listelere ekle
                user1List.add(users.get(i).getName());
                user2List.add(users.get(j).getName());
                statusList.add(status);
                scoreList.add(score);
            }
        }

        if (scoreList.isEmpty()) {
            Label noDataLabel = new Label("Hesaplanacak ilişki verisi bulunamadı.");
            txtRankingContainer.getChildren().add(noDataLabel);
            return;
        }

        // --- BUBBLE SORT (Puanı Yüksek Olan En Üste) ---
        // Sadece List'leri paralel sıralamak için yardımcı metodunu kullanıyoruz
        // NOT: user3List (Direction) parametresini sildiğimiz için swap metodunu aşağıda uyarladım.
        // Veya "directionList" yerine dummy bir liste gönderebilirsin.
        // Burada manuel swap yapıyorum:
        for (int i = 0; i < scoreList.size() - 1; i++) {
            for (int j = 0; j < scoreList.size() - i - 1; j++) {
                if (scoreList.get(j) < scoreList.get(j + 1)) {
                    // Puan Swap
                    double tempScore = scoreList.get(j); scoreList.set(j, scoreList.get(j + 1)); scoreList.set(j + 1, tempScore);
                    // User1 Swap
                    String tempU1 = user1List.get(j); user1List.set(j, user1List.get(j + 1)); user1List.set(j + 1, tempU1);
                    // User2 Swap
                    String tempU2 = user2List.get(j); user2List.set(j, user2List.get(j + 1)); user2List.set(j + 1, tempU2);
                    // Status Swap
                    int tempStat = statusList.get(j); statusList.set(j, statusList.get(j + 1)); statusList.set(j + 1, tempStat);
                }
            }
        }

        // --- UI BAŞLIK SATIRI ---
        HBox headerRow = new HBox(10);
        headerRow.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0;");
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label rankHeader = new Label("#");
        rankHeader.setPrefWidth(30); rankHeader.setStyle("-fx-font-weight: bold;");

        Label usersHeader = new Label("KİMDEN -> KİME");
        usersHeader.setPrefWidth(200); usersHeader.setStyle("-fx-font-weight: bold;");

        Label typeHeader = new Label("DURUM");
        typeHeader.setPrefWidth(120); typeHeader.setStyle("-fx-font-weight: bold;");

        Label scoreHeader = new Label("PUAN");
        scoreHeader.setStyle("-fx-font-weight: bold;");

        headerRow.getChildren().addAll(rankHeader, usersHeader, typeHeader, scoreHeader);
        txtRankingContainer.getChildren().add(headerRow);

        // --- LİSTEYİ EKRAŇA BAS ---
        // Çok fazla veri olabileceği için ilk 100 kaydı veya tümünü gösterelim.
        // ScrollPane içinde olduğu için hepsini basabiliriz ama UI donmaması için dikkatli olunmalı.
        int limit = scoreList.size();

        for (int i = 0; i < limit; i++) {
            HBox scoreRow = new HBox(10);
            scoreRow.setAlignment(Pos.CENTER_LEFT);
            scoreRow.setStyle("-fx-background-color: " + (i % 2 == 0 ? "white" : "#f8f9fa") + "; " +
                    "-fx-padding: 12 10; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

            // Sıra
            Label rankLabel = new Label((i + 1) + ".");
            rankLabel.setPrefWidth(30);
            rankLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");

            // İsimler
            Label nameLabel = new Label(user1List.get(i) + " → " + user2List.get(i));
            nameLabel.setPrefWidth(200);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Durum Rengi ve Metni
            String statusText;
            String statusColor;
            int st = statusList.get(i);

            if (st == 1) {
                statusText = "Arkadaş";
                statusColor = "#3498db"; // Mavi
            } else if (st == 2) {
                statusText = "YAKIN ARKADAŞ";
                statusColor = "#e74c3c"; // Kırmızı
            } else {
                statusText = "Tanışmıyor";
                statusColor = "#95a5a6"; // Gri
            }

            Label statusLabel = new Label(statusText);
            statusLabel.setPrefWidth(120);
            statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold; -fx-font-size: 11px;");

            // Puan
            Label pScoreLabel = new Label(String.format("%.1f", scoreList.get(i)) + " p");
            pScoreLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

            scoreRow.getChildren().addAll(rankLabel, nameLabel, statusLabel, pScoreLabel);
            txtRankingContainer.getChildren().add(scoreRow);
        }

        // Alt Bilgi
        Label footerLabel = new Label("Toplam " + scoreList.size() + " ilişki olasılığı hesaplandı.");
        footerLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px; -fx-padding: 10;");
        txtRankingContainer.getChildren().add(footerLabel);
    }
    @FXML
    // Yardımcı metod: List içinde anahtar var mı kontrol et
    private boolean containsKey(List<String> keys, String key) {
        for (String k : keys) {
            if (k.equals(key)) {
                return true;
            }
        }
        return false;
    }
    @FXML
    // Swap yardımcı metodu (güncellendi)
    private void swapInLists(List<String> list1, List<String> list2, List<String> list3,
                             List<Integer> list4, List<Double> list5, int i, int j) {
        // List1 swap
        String temp1 = list1.get(i);
        list1.set(i, list1.get(j));
        list1.set(j, temp1);

        // List2 swap
        String temp2 = list2.get(i);
        list2.set(i, list2.get(j));
        list2.set(j, temp2);

        // List3 swap (yön listesi)
        String temp3 = list3.get(i);
        list3.set(i, list3.get(j));
        list3.set(j, temp3);

        // List4 swap
        int temp4 = list4.get(i);
        list4.set(i, list4.get(j));
        list4.set(j, temp4);

        // List5 swap
        double temp5 = list5.get(i);
        list5.set(i, list5.get(j));
        list5.set(j, temp5);
    }

    // --- DB GRAF OLUŞTUR ---
    @FXML
    private void grafOlusturDb() {
        graphPane.getChildren().clear();

        // DB'den kullanıcıları ve ilişkileri çek
        List<Integer> userIds = new ArrayList<>();
        List<String> usernames = new ArrayList<>();
        List<String> fullNames = new ArrayList<>();
        List<String> profilePhotos = new ArrayList<>();

        String userSql = "SELECT user_id, username, full_name, profile_photo " +
                "FROM Kullanicilar " +
                "WHERE is_deleted = 0 " +
                "AND user_id IN (" +
                "    SELECT sender_id FROM Arkadaslik WHERE status = 'ACCEPTED' " +
                "    UNION " +
                "    SELECT receiver_id FROM Arkadaslik WHERE status = 'ACCEPTED'" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(userSql)) {

            while(rs.next()) {
                userIds.add(rs.getInt("user_id"));
                usernames.add(rs.getString("username"));
                fullNames.add(rs.getString("full_name"));
                profilePhotos.add(rs.getString("profile_photo"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (userIds.isEmpty()) return;

        // İlişkileri çek
        List<Integer> user1List = new ArrayList<>();
        List<Integer> user2List = new ArrayList<>();
        List<Integer> relationshipTypes = new ArrayList<>(); // 1: Normal, 2: Yakın

        String relationSql = "SELECT a.sender_id, a.receiver_id, " +
                "CASE WHEN y.close_friend_id IS NOT NULL THEN 2 ELSE 1 END as rel_type " +
                "FROM Arkadaslik a " +
                "LEFT JOIN Yakin_Arkadas y ON " +
                "    ((a.sender_id = y.user_id AND a.receiver_id = y.close_friend_id) OR " +
                "     (a.receiver_id = y.user_id AND a.sender_id = y.close_friend_id)) " +
                "WHERE a.status = 'ACCEPTED'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(relationSql)) {

            while(rs.next()) {
                user1List.add(rs.getInt("sender_id"));
                user2List.add(rs.getInt("receiver_id"));
                relationshipTypes.add(rs.getInt("rel_type"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Pane'in merkezini bul
        double centerX = graphPane.getWidth() / 2;
        double centerY = graphPane.getHeight() / 2;
        if(centerX == 0) { centerX = 400; centerY = 300; }

        double radius = Math.min(250, 200 + (userIds.size() * 10)); // Kullanıcı sayısına göre radius

        // --- ADIM 1: Çizgileri Çiz ---
        for (int i = 0; i < user1List.size(); i++) {
            int user1 = user1List.get(i);
            int user2 = user2List.get(i);
            int relType = relationshipTypes.get(i);

            int idx1 = userIds.indexOf(user1);
            int idx2 = userIds.indexOf(user2);

            if (idx1 != -1 && idx2 != -1) {
                double angle1 = 2 * Math.PI * idx1 / userIds.size();
                double x1 = centerX + radius * Math.cos(angle1);
                double y1 = centerY + radius * Math.sin(angle1);

                double angle2 = 2 * Math.PI * idx2 / userIds.size();
                double x2 = centerX + radius * Math.cos(angle2);
                double y2 = centerY + radius * Math.sin(angle2);

                Line line = new Line(x1, y1, x2, y2);
                line.setStrokeWidth(relType == 2 ? 3 : 2); // Yakın arkadaş kalın

                if (relType == 2) {
                    line.setStroke(Color.RED);
                } else {
                    line.setStroke(Color.BLUE);
                }

                graphPane.getChildren().add(line);
            }
        }

        // --- ADIM 2: Düğümleri (Kişileri) Çiz ---
        for (int i = 0; i < userIds.size(); i++) {
            double angle = 2 * Math.PI * i / userIds.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            // StackPane oluştur
            StackPane node = new StackPane();
            node.setLayoutX(x - 25);
            node.setLayoutY(y - 25);

            // Daire
            Circle circle = new Circle(25);

            // Profil fotoğrafı varsa onu kullan, yoksa renkli daire
            String profilePhoto = profilePhotos.get(i);
            boolean hasPhoto = false;

            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                File photoFile = new File(profilePhoto);
                if (photoFile.exists()) {
                    try {
                        circle.setFill(new ImagePattern(new Image(photoFile.toURI().toString())));
                        hasPhoto = true;
                    } catch (Exception e) {
                        System.err.println("Profil fotoğrafı yüklenemedi: " + profilePhoto);
                    }
                }
            }

            if (!hasPhoto) {
                circle.setFill(Color.web(getColorForUsername(usernames.get(i))));

                // İlk harf
                String firstLetter = usernames.get(i).substring(0, 1).toUpperCase();
                Text initial = new Text(firstLetter);
                initial.setFont(Font.font("System", FontWeight.BOLD, 16));
                initial.setFill(Color.WHITE);

                node.getChildren().addAll(circle, initial);
            } else {
                node.getChildren().add(circle);
            }

            // Tooltip (hover'da bilgi göster)
            String displayName = (fullNames.get(i) != null && !fullNames.get(i).isEmpty())
                    ? fullNames.get(i) : usernames.get(i);
            Tooltip.install(node, new Tooltip(displayName + " (ID: " + userIds.get(i) + ")"));

            graphPane.getChildren().add(node);
        }
    }

    // --- TXT GRAF OLUŞTUR (ORJİNAL) ---
    @FXML
    public void grafOlusturTxt() {
        graphPane.getChildren().clear();
        TxtManager.init();

        List<User> users = TxtManager.txtUsers;
        if (users.isEmpty()) return;

        int n = users.size();
        double centerX = graphPane.getWidth() / 2;
        double centerY = graphPane.getHeight() / 2;
        if(centerX == 0) { centerX = 400; centerY = 300; }

        // Kullanıcı sayısına göre radius ayarla
        double radius = Math.min(250, 200 + (n * 5));

        // Node boyutunu kullanıcı sayısına göre ayarla
        double nodeRadius = Math.max(25, Math.min(35, 200 / Math.sqrt(n)));
        double fontSize = Math.max(8, Math.min(12, 100 / Math.sqrt(n)));

        // --- ADIM 1: Çizgileri Çiz ---
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int status = TxtManager.relationMatrix[i][j];

                if (status > 0) {
                    double angle1 = 2 * Math.PI * i / n;
                    double x1 = centerX + radius * Math.cos(angle1);
                    double y1 = centerY + radius * Math.sin(angle1);

                    double angle2 = 2 * Math.PI * j / n;
                    double x2 = centerX + radius * Math.cos(angle2);
                    double y2 = centerY + radius * Math.sin(angle2);

                    Line line = new Line(x1, y1, x2, y2);
                    line.setStrokeWidth(status == 2 ? 3 : 2); // Yakın arkadaş kalın

                    if (status == 2) line.setStroke(Color.RED);
                    else line.setStroke(Color.BLUE);

                    graphPane.getChildren().add(line);
                }
            }
        }

        // --- ADIM 2: Düğümleri Çiz ---
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            User user = users.get(i);
            String name = user.getName();

            // StackPane oluştur
            StackPane node = new StackPane();
            node.setLayoutX(x - nodeRadius);
            node.setLayoutY(y - nodeRadius);

            // Daire
            Circle circle = new Circle(nodeRadius);
            circle.setFill(Color.web(getColorForUsername(name)));
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1);

            // İsim metni - tam ismi göster
            Text text = new Text(name);
            text.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
            text.setFill(Color.BLACK);
            text.setWrappingWidth(nodeRadius * 1.8); // Metin sarmalama genişliği

            // Metni ölç ve eğer çok uzunsa kısalt
            double textWidth = text.getLayoutBounds().getWidth();
            double maxWidth = nodeRadius * 1.8;

            if (textWidth > maxWidth) {
                // İsmi kısalt
                int maxChars = (int) Math.max(1, (maxWidth / (fontSize * 0.6)));
                if (maxChars < name.length()) {
                    String shortened = name.substring(0, maxChars) + ".";
                    text.setText(shortened);
                }
            }

            // Metni ortala
            text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            node.getChildren().addAll(circle, text);

            // Tooltip - hover'da tam ismi göster
            Tooltip tooltip = new Tooltip(name + " (ID:" + user.getId() + ")");
            Tooltip.install(node, tooltip);

            graphPane.getChildren().add(node);
        }
    }

    // --- YARDIMCI METOTLAR ---

    private String getColorForUsername(String username) {
        int hash = username.hashCode();
        String[] colors = {
                "#3498db", "#e74c3c", "#2ecc71", "#f39c12",
                "#9b59b6", "#1abc9c", "#d35400", "#27ae60"
        };
        return colors[Math.abs(hash) % colors.length];
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}