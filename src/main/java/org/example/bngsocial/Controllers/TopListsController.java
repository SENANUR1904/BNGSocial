package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.bngsocial.Utils.TxtManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TopListsController {

    @FXML private VBox topLikesList;    // En Çok Beğeni Alanlar Kutusu
    @FXML private VBox topCommentsList; // En Çok Yorum Alanlar Kutusu

    private int currentUserId;
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadStats();
    }

    private void loadStats() {
        TxtManager.init();

        // 1. En Çok Beğeni Alanları
        List<String> likesFromDB = getTopLikesFromDB();
        List<String> likesFromTxt = TxtManager.enCokEtkilesimAlan("LIKE");
        List<String> combinedLikes = combineLists(likesFromDB, likesFromTxt, "beğeni");

        // Sadece ilk 3'ü al
        List<String> top3Likes = new ArrayList<>();
        for (int i = 0; i < Math.min(combinedLikes.size(), 3); i++) {
            top3Likes.add(combinedLikes.get(i));
        }

        displayTopList(topLikesList, top3Likes, "❤️ En Çok Beğeni Alanlar");

        // 2. En Çok Yorum Alanları
        List<String> commentsFromDB = getTopCommentsFromDB();
        List<String> commentsFromTxt = TxtManager.enCokEtkilesimAlan("COMMENT");
        List<String> combinedComments = combineLists(commentsFromDB, commentsFromTxt, "yorum");

        // Sadece ilk 3'ü al
        List<String> top3Comments = new ArrayList<>();
        for (int i = 0; i < Math.min(combinedComments.size(), 3); i++) {
            top3Comments.add(combinedComments.get(i));
        }

        displayTopList(topCommentsList, top3Comments, "💬 En Çok Yorum Alanlar");
    }

    // DB'den en çok beğeni alanları getir
    private List<String> getTopLikesFromDB() {
        List<String> results = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Önce reaction_type sütununun tipini kontrol et
            System.out.println("=== REACTION_TYPE KONTROLÜ ===");
            String checkSql = "SELECT TOP 1 reaction_type FROM Begeniler";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next()) {
                    Object reactionType = rs.getObject("reaction_type");
                    System.out.println("reaction_type tipi: " +
                            (reactionType != null ? reactionType.getClass().getName() : "NULL") +
                            ", değer: " + reactionType);
                }
            }

            // DB'de reaction_type INT olduğu için 1=LIKE kabul ediyoruz
            String sql = "SELECT u.username, COUNT(b.user_id) as like_count " +
                    "FROM Kullanicilar u " +
                    "LEFT JOIN Gonderiler g ON u.user_id = g.user_id " +
                    "LEFT JOIN Begeniler b ON g.post_id = b.post_id " +
                    "WHERE u.is_deleted = 0 " +
                    "AND (b.reaction_type = 1 OR b.reaction_type IS NULL) " +
                    "GROUP BY u.user_id, u.username " +
                    "HAVING COUNT(b.user_id) > 0 " +
                    "ORDER BY like_count DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String username = rs.getString("username");
                    int count = rs.getInt("like_count");
                    if (count > 0) {
                        results.add(username + " - " + count + " beğeni");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DB beğeni hatası: " + e.getMessage());
        }

        return results;
    }

    // DB'den en çok yorum alanları getir
    private List<String> getTopCommentsFromDB() {
        List<String> results = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Her kullanıcının toplam yorum sayısını getir
            String sql = "SELECT u.username, " +
                    "(SELECT COUNT(*) FROM Yorumlar y " +
                    " INNER JOIN Gonderiler g ON y.post_id = g.post_id " +
                    " WHERE g.user_id = u.user_id) as comment_count " +
                    "FROM Kullanicilar u " +
                    "WHERE u.is_deleted = 0 " +
                    "AND (SELECT COUNT(*) FROM Yorumlar y " +
                    "     INNER JOIN Gonderiler g ON y.post_id = g.post_id " +
                    "     WHERE g.user_id = u.user_id) > 0 " +
                    "ORDER BY comment_count DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String username = rs.getString("username");
                    int count = rs.getInt("comment_count");
                    if (count > 0) {
                        results.add(username + " - " + count + " yorum");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DB yorum hatası: " + e.getMessage());
        }

        return results;
    }

    // İki listeyi birleştir (DB + TXT)
    private List<String> combineLists(List<String> dbList, List<String> txtList, String type) {
        List<UserStat> allStats = new ArrayList<>();

        // Önce DB listesini işle
        for (String dbItem : dbList) {
            String[] parts = dbItem.split(" - ");
            if (parts.length >= 2) {
                String username = parts[0].trim();
                String countText = parts[1].trim();
                int count = extractNumber(countText);

                if (count > 0) {
                    allStats.add(new UserStat(username, count));
                }
            }
        }

        // Sonra TXT listesini işle - Parantez formatı: "Remziye (6)"
        for (String txtItem : txtList) {
            // Format: "Remziye (6)" - parantez içindeki sayıyı al
            String username = "";
            int count = 0;

            // Parantezleri bul
            int openParen = txtItem.indexOf('(');
            int closeParen = txtItem.indexOf(')');

            if (openParen != -1 && closeParen != -1 && closeParen > openParen) {
                // Kullanıcı adı: parantezden öncesi
                username = txtItem.substring(0, openParen).trim();

                // Sayı: parantez içi
                String countStr = txtItem.substring(openParen + 1, closeParen).trim();
                count = extractNumber(countStr);
            }

            if (count > 0 && !username.isEmpty()) {
                // Bu kullanıcı zaten listede var mı?
                boolean found = false;
                for (UserStat stat : allStats) {
                    if (stat.username.equals(username)) {
                        // Var, sayıyı topla
                        stat.count += count;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Yok, yeni ekle
                    allStats.add(new UserStat(username, count));
                }
            }
        }

        // Listeyi sırala (büyükten küçüğe)
        bubbleSort(allStats);

        // İlk 10'u al ve formatla
        List<String> result = new ArrayList<>();
        int limit = Math.min(allStats.size(), 10);
        for (int i = 0; i < limit; i++) {
            UserStat stat = allStats.get(i);
            result.add(stat.username + " - " + stat.count + " " + type);
        }

        return result;
    }

    // Bubble sort ile sırala
    private void bubbleSort(List<UserStat> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).count < list.get(j + 1).count) {
                    // Swap
                    UserStat temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // Listeyi ekranda göster
    private void displayTopList(VBox container, List<String> items, String title) {
        container.getChildren().clear();

        // Başlık
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        container.getChildren().add(titleLabel);

        if (items.isEmpty()) {
            Label noData = new Label("Veri yok.");
            noData.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-padding: 10;");
            container.getChildren().add(noData);
        } else {
            for (int i = 0; i < items.size(); i++) {
                String row = (i + 1) + ". " + items.get(i);
                Label label = new Label(row);

                // İlk 3'e özel stil
                if (i < 3) {
                    String color = i == 0 ? "#FFD700" :
                            i == 1 ? "#C0C0C0" :
                                    "#CD7F32";
                    label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                            "-fx-text-fill: " + color + "; -fx-padding: 8 5; " +
                            "-fx-background-color: #f8f9fa; -fx-background-radius: 5;");
                } else {
                    label.setStyle("-fx-font-size: 14px; -fx-padding: 8 5; " +
                            "-fx-border-color: #eee; -fx-border-width: 0 0 1 0;");
                }

                container.getChildren().add(label);
            }

            // Toplam
            Label countLabel = new Label("Top " + items.size() + " kullanıcı gösteriliyor");
            countLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-style: italic; -fx-padding: 10 0 0 0;");
            container.getChildren().add(countLabel);
        }
    }

    // Metinden sayı çıkar
    private int extractNumber(String text) {
        try {
            StringBuilder numStr = new StringBuilder();
            for (char c : text.toCharArray()) {
                if (Character.isDigit(c)) {
                    numStr.append(c);
                }
            }
            if (numStr.length() > 0) {
                return Integer.parseInt(numStr.toString());
            }
        } catch (Exception e) {
            // Hata durumunda 0 dön
        }
        return 0;
    }

    // İç sınıf: Kullanıcı istatistiği
    private static class UserStat {
        String username;
        int count;

        UserStat(String username, int count) {
            this.username = username;
            this.count = count;
        }
    }
}