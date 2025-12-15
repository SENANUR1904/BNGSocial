package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.sql.*;

public class HomeController {

    @FXML private VBox feedContainer;
    private int currentUserId;

    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadPosts();
    }

    private void loadPosts() {
        if (feedContainer == null) return;
        feedContainer.getChildren().clear();

        String sql = "SELECT p.post_id, p.content_text, p.image_path, p.created_at, u.username, u.profile_photo " +
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

    private VBox createCard(ResultSet rs) throws SQLException {
        VBox card = new VBox(10);
        // Kartın arka planı için inline style kalabilir, sorun yok
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        // --- 1. BÖLÜM: BAŞLIK ---
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(20);
        avatar.setStroke(Color.LIGHTGRAY);
        String profilePhotoPath = rs.getString("profile_photo");
        String username = rs.getString("username");

        boolean imageLoaded = false;
        if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
            File photoFile = new File(profilePhotoPath);
            if (photoFile.exists()) {
                avatar.setFill(new ImagePattern(new Image(photoFile.toURI().toString())));
                imageLoaded = true;
            }
        }
        if (!imageLoaded) avatar.setFill(Color.web("#bdc3c7"));

        Label nameLabel = new Label(username);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");
        header.getChildren().addAll(avatar, nameLabel);
        card.getChildren().add(header);

        // --- 2. BÖLÜM: RESİM ---
        String postImagePath = rs.getString("image_path");
        if (postImagePath != null && !postImagePath.isEmpty()) {
            try {
                File f = new File(postImagePath);
                if (f.exists()) {
                    ImageView iv = new ImageView(new Image(f.toURI().toString()));
                    iv.setFitWidth(400);
                    iv.setPreserveRatio(true);
                    card.getChildren().add(iv);
                }
            } catch (Exception e) {}
        }

        // --- 3. BÖLÜM: METİN ---
        String textContent = rs.getString("content_text");
        if (textContent != null && !textContent.isEmpty()) {
            Label content = new Label(textContent);
            content.setWrapText(true);
            content.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e; -fx-padding: 5 0 0 0;");
            card.getChildren().add(content);
        }

        // --- 4. BÖLÜM: BUTONLAR ---
        HBox actions = new HBox(15);
        int postId = rs.getInt("post_id");

        // Sayıları ve durumu çek
        int likeCount = getReactionCount(postId, 1);
        int dislikeCount = getReactionCount(postId, 2);
        int userState = getUserReactionState(postId);

        Button btnLike = new Button("👍 " + likeCount);
        Button btnDislike = new Button("👎 " + dislikeCount);
        Button btnComment = new Button("💬 Yorum");

        // --- DÜZELTME BURASI: SADECE CSS SINIFI EKLİYORUZ ---
        // setStyle(...) KODLARINI TAMAMEN SİLDİM.
        btnLike.getStyleClass().add("reaction-button");
        btnDislike.getStyleClass().add("reaction-button");
        btnComment.getStyleClass().add("reaction-button");

        // Başlangıç renklerini ayarla
        updateButtonStyles(btnLike, btnDislike, userState);

        // --- TIKLAMA OLAYLARI ---
        btnLike.setOnAction(e -> {
            handleReaction(postId, 1);

            // Güncel verileri çek
            int newLike = getReactionCount(postId, 1);
            int newDislike = getReactionCount(postId, 2);
            int newState = getUserReactionState(postId);

            // Arayüzü güncelle
            btnLike.setText("👍 " + newLike);
            btnDislike.setText("👎 " + newDislike);
            updateButtonStyles(btnLike, btnDislike, newState);
        });

        btnDislike.setOnAction(e -> {
            handleReaction(postId, 2);

            int newLike = getReactionCount(postId, 1);
            int newDislike = getReactionCount(postId, 2);
            int newState = getUserReactionState(postId);

            btnLike.setText("👍 " + newLike);
            btnDislike.setText("👎 " + newDislike);
            updateButtonStyles(btnLike, btnDislike, newState);
        });

        btnComment.setOnAction(e -> System.out.println("Yorum: " + postId));

        actions.getChildren().addAll(btnLike, btnDislike, btnComment);
        card.getChildren().add(actions);

        return card;
    }

    // --- YARDIMCI METOTLAR ---

    private int getReactionCount(int postId, int reactionType) {
        String sql = "SELECT COUNT(*) FROM Begeniler WHERE post_id = ? AND reaction_type = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, reactionType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void handleReaction(int postId, int reactionType) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String checkSql = "SELECT reaction_type FROM Begeniler WHERE user_id = ? AND post_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, currentUserId);
            checkPs.setInt(2, postId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int existingType = rs.getInt("reaction_type");
                if (existingType == reactionType) {
                    // Aynı şeye bastı -> SİL (Toggle)
                    String deleteSql = "DELETE FROM Begeniler WHERE user_id = ? AND post_id = ?";
                    PreparedStatement delPs = conn.prepareStatement(deleteSql);
                    delPs.setInt(1, currentUserId);
                    delPs.setInt(2, postId);
                    delPs.executeUpdate();
                } else {
                    // Farklı şeye bastı -> GÜNCELLE
                    String updateSql = "UPDATE Begeniler SET reaction_type = ? WHERE user_id = ? AND post_id = ?";
                    PreparedStatement upPs = conn.prepareStatement(updateSql);
                    upPs.setInt(1, reactionType);
                    upPs.setInt(2, currentUserId);
                    upPs.setInt(3, postId);
                    upPs.executeUpdate();
                }
            } else {
                // Hiç yok -> EKLE
                String insertSql = "INSERT INTO Begeniler (user_id, post_id, reaction_type) VALUES (?, ?, ?)";
                PreparedStatement inPs = conn.prepareStatement(insertSql);
                inPs.setInt(1, currentUserId);
                inPs.setInt(2, postId);
                inPs.setInt(3, reactionType);
                inPs.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserReactionState(int postId) {
        String sql = "SELECT reaction_type FROM Begeniler WHERE user_id = ? AND post_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("reaction_type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateButtonStyles(Button btnLike, Button btnDislike, int userReaction) {
        // 1. Temizle
        btnLike.getStyleClass().removeAll("like-active", "dislike-active");
        btnDislike.getStyleClass().removeAll("like-active", "dislike-active");

        // 2. Ekle
        if (userReaction == 1) {
            btnLike.getStyleClass().add("like-active");
        } else if (userReaction == 2) {
            btnDislike.getStyleClass().add("dislike-active");
        }
    }
}