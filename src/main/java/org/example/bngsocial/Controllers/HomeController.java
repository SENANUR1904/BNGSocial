package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;

import java.time.LocalDateTime;

public class HomeController {

    @FXML private VBox feedContainer;
    private int currentUserId;

    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadAllPosts();
    }

    private void loadAllPosts() {
        if (feedContainer == null) return;
        feedContainer.getChildren().clear();

        String sql = "SELECT p.post_id, p.content_text, p.image_path, p.created_at, " +
                "u.user_id, u.username, u.full_name, u.profile_photo " +
                "FROM Gonderiler p " +
                "JOIN Kullanicilar u ON p.user_id = u.user_id " +
                "WHERE u.is_deleted = 0 " +
                "ORDER BY p.created_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                feedContainer.getChildren().add(createPostCard(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Veritabanı Hatası", "Gönderiler yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private HBox createPostCard(ResultSet rs) throws SQLException {
        HBox mainContainer = new HBox(15);
        mainContainer.setAlignment(Pos.TOP_LEFT);
        mainContainer.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        mainContainer.setMaxWidth(Double.MAX_VALUE); // Konteynerin tam genişliği kullanmasını sağla

        int postId = rs.getInt("post_id");

        // --- SOL TARAF: GÖNDERİ İÇERİĞİ ---
        VBox postContent = new VBox(10);
        postContent.setMinWidth(300); // Minimum genişlik
        postContent.setPrefWidth(300); // Tercih edilen genişlik
        postContent.setMaxWidth(350); // Maksimum genişlik

        // Başlık (Profil bilgileri)
        HBox header = createPostHeader(rs);
        postContent.getChildren().add(header);

        // Gönderi metni
        String textContent = rs.getString("content_text");
        if (textContent != null && !textContent.isEmpty()) {
            Label content = new Label(textContent);
            content.setWrapText(true);
            content.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-padding: 5 0;");
            postContent.getChildren().add(content);
        }

        // Gönderi resmi
        String postImagePath = rs.getString("image_path");
        if (postImagePath != null && !postImagePath.isEmpty()) {
            try {
                File f = new File(postImagePath);
                if (f.exists()) {
                    ImageView iv = new ImageView(new Image(f.toURI().toString()));
                    iv.setFitWidth(280); // Resim genişliğini ayarla
                    iv.setPreserveRatio(true);
                    iv.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
                    postContent.getChildren().add(iv);
                }
            } catch (Exception e) {
                System.err.println("Resim yüklenemedi: " + postImagePath);
            }
        }

        // Etkileşim butonları
        HBox actions = createActionButtons(postId);
        postContent.getChildren().add(actions);

        // --- SAĞ TARAF: YORUMLAR ---
        VBox commentsSection = createCommentsSection(postId);
        commentsSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 8;");

        // Yorumlar bölümünün kalan tüm alanı kaplamasını sağla
        HBox.setHgrow(commentsSection, javafx.scene.layout.Priority.ALWAYS);
        commentsSection.setMaxWidth(Double.MAX_VALUE);

        // İki bölümü yan yana ekle
        mainContainer.getChildren().addAll(postContent, commentsSection);

        return mainContainer;
    }

    private VBox createCommentsSection(int postId) {
        VBox commentsSection = new VBox(10);

        // Başlık
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label commentsTitle = new Label("💬 Yorumlar");
        commentsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        titleRow.getChildren().add(commentsTitle);
        commentsSection.getChildren().add(titleRow);

        // Yorumlar container (scrollable)
        ScrollPane commentsScroll = new ScrollPane();
        commentsScroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        commentsScroll.setFitToWidth(true);
        commentsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // ScrollPane'in tüm alanı kaplamasını sağla
        VBox.setVgrow(commentsScroll, javafx.scene.layout.Priority.ALWAYS);
        commentsScroll.setMaxHeight(Double.MAX_VALUE);

        VBox commentsContainer = new VBox(8);
        commentsContainer.setPadding(new Insets(5));
        commentsScroll.setContent(commentsContainer);

        // Yorumları yükle
        loadCommentsIntoContainer(postId, commentsContainer);

        // Eğer yorum yoksa
        if (commentsContainer.getChildren().isEmpty()) {
            Label noComments = new Label("Henüz yorum yok.\nİlk yorumu siz yapın!");
            noComments.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 12px; -fx-text-alignment: center;");
            noComments.setWrapText(true);
            commentsContainer.getChildren().add(noComments);
        }

        commentsSection.getChildren().add(commentsScroll);

        // Yorum yap butonu
        Button addCommentBtn = new Button("+ Yorum Ekle");
        addCommentBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 15;");
        addCommentBtn.setOnAction(e -> openCommentDialog(postId, commentsContainer));

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(addCommentBtn);
        commentsSection.getChildren().add(buttonContainer);

        return commentsSection;
    }

    private void loadCommentsIntoContainer(int postId, VBox commentsContainer) {
        commentsContainer.getChildren().clear();
        commentsContainer.setUserData(postId); // Post ID'yi sakla

        String sql = "SELECT c.comment_id, c.content_text, c.created_at, " +
                "u.user_id, u.username, u.full_name, u.profile_photo " +
                "FROM Yorumlar c " +
                "JOIN Kullanicilar u ON c.user_id = u.user_id " +
                "WHERE c.post_id = ? AND u.is_deleted = 0 " +
                "ORDER BY c.created_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                HBox commentRow = createCommentRow(rs);
                commentRow.setUserData(postId); // Her yorum satırına post ID'sini ekle
                commentsContainer.getChildren().add(commentRow);
            }

            if (commentsContainer.getChildren().isEmpty()) {
                Label noComments = new Label("Henüz yorum yok.\nİlk yorumu siz yapın!");
                noComments.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 12px; -fx-text-alignment: center;");
                noComments.setWrapText(true);
                commentsContainer.getChildren().add(noComments);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Yorumlar yüklenemedi.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            commentsContainer.getChildren().add(errorLabel);
        }
    }

    private HBox createPostHeader(ResultSet rs) throws SQLException {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(20);
        String profilePhotoPath = rs.getString("profile_photo");
        String username = rs.getString("username");
        String fullName = rs.getString("full_name");

        boolean imageLoaded = false;
        if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
            File photoFile = new File(profilePhotoPath);
            if (photoFile.exists()) {
                try {
                    avatar.setFill(new ImagePattern(new Image(photoFile.toURI().toString())));
                    imageLoaded = true;
                } catch (Exception e) {
                    System.err.println("Profil fotoğrafı yüklenemedi: " + profilePhotoPath);
                }
            }
        }

        if (!imageLoaded) {
            String firstLetter = username.substring(0, 1).toUpperCase();
            Label initialLabel = new Label(firstLetter);
            initialLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            initialLabel.setTextFill(Color.WHITE);

            avatar.setFill(Color.web(getColorForUsername(username)));
            StackPane avatarPane = new StackPane(avatar, initialLabel);
            header.getChildren().add(avatarPane);
        } else {
            header.getChildren().add(avatar);
        }

        VBox userInfo = new VBox(2);
        String displayName = (fullName != null && !fullName.isEmpty()) ? fullName : username;
        Label nameLabel = new Label(displayName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        Timestamp createdAt = rs.getTimestamp("created_at");
        String timeAgo = getTimeAgo(createdAt);
        Label timeLabel = new Label(timeAgo);
        timeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        userInfo.getChildren().addAll(nameLabel, timeLabel);
        header.getChildren().add(userInfo);

        return header;
    }

    private HBox createActionButtons(int postId) {
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_LEFT);

        int likeCount = getReactionCount(postId, 1);
        int dislikeCount = getReactionCount(postId, 2);
        int userState = getUserReactionState(postId);

        Button btnLike = new Button("👍 " + likeCount);
        btnLike.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd; -fx-border-radius: 15; " +
                "-fx-padding: 5 15; -fx-font-size: 13px;");

        Button btnDislike = new Button("👎 " + dislikeCount);
        btnDislike.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd; -fx-border-radius: 15; " +
                "-fx-padding: 5 15; -fx-font-size: 13px;");

        Button btnComment = new Button("💬 Yorum");
        btnComment.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd; -fx-border-radius: 15; " +
                "-fx-padding: 5 15; -fx-font-size: 13px;");

        updateButtonStyles(btnLike, btnDislike, userState);

        btnLike.setOnAction(e -> {
            handleReaction(postId, 1);
            refreshPost(postId);
        });

        btnDislike.setOnAction(e -> {
            handleReaction(postId, 2);
            refreshPost(postId);
        });

        btnComment.setOnAction(e -> openCommentDialog(postId, null));

        actions.getChildren().addAll(btnLike, btnDislike, btnComment);
        return actions;
    }

    private HBox createCommentRow(ResultSet rs) throws SQLException {
        HBox commentRow = new HBox(8);
        commentRow.setAlignment(Pos.TOP_LEFT);
        commentRow.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-background-radius: 5; " +
                "-fx-border-color: #eee; -fx-border-width: 1; -fx-border-radius: 5;");

        int commentId = rs.getInt("comment_id");
        int commentUserId = rs.getInt("user_id"); // Yorumu yapan kullanıcı ID'si
        String profilePhotoPath = rs.getString("profile_photo");
        String username = rs.getString("username");
        String fullName = rs.getString("full_name");
        String commentText = rs.getString("content_text");

        // Profil fotoğrafı
        Circle avatar = new Circle(12);
        boolean imageLoaded = false;
        if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
            File photoFile = new File(profilePhotoPath);
            if (photoFile.exists()) {
                try {
                    avatar.setFill(new ImagePattern(new Image(photoFile.toURI().toString())));
                    imageLoaded = true;
                } catch (Exception e) {
                    System.err.println("Yorum profil fotoğrafı yüklenemedi: " + profilePhotoPath);
                }
            }
        }

        if (!imageLoaded) {
            String firstLetter = username.substring(0, 1).toUpperCase();
            Label initialLabel = new Label(firstLetter);
            initialLabel.setFont(Font.font("System", FontWeight.BOLD, 8));
            initialLabel.setTextFill(Color.WHITE);

            avatar.setFill(Color.web(getColorForUsername(username)));
            StackPane avatarPane = new StackPane(avatar, initialLabel);
            commentRow.getChildren().add(avatarPane);
        } else {
            commentRow.getChildren().add(avatar);
        }

        // Yorum içeriği
        VBox commentContent = new VBox(2);
        commentContent.setPrefWidth(230);

        HBox userInfo = new HBox(5);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        String displayName = (fullName != null && !fullName.isEmpty()) ? fullName : "@" + username;
        Label nameLabel = new Label(displayName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #2c3e50;");

        Timestamp createdAt = rs.getTimestamp("created_at");
        String timeAgo = getTimeAgo(createdAt);
        Label timeLabel = new Label("• " + timeAgo);
        timeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 9px;");

        userInfo.getChildren().addAll(nameLabel, timeLabel);

        // Yorum metni (düzenlenebilir olacak)
        Label commentLabel = new Label(commentText);
        commentLabel.setWrapText(true);
        commentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e; -fx-padding: 2 0 0 0;");
        commentLabel.setUserData(commentText); // Orijinal yorumu sakla

        commentContent.getChildren().addAll(userInfo, commentLabel);
        commentRow.getChildren().add(commentContent);

        // Eğer yorum kullanıcıya aitse, düzenle/sil butonlarını göster
        if (commentUserId == currentUserId) {
            HBox buttonContainer = new HBox(5);
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);
            buttonContainer.setStyle("-fx-padding: 0 0 0 10;");

            // Düzenle butonu
            Button editBtn = new Button("Düzenle");
            editBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                    "-fx-font-size: 10px; -fx-padding: 2 5; -fx-cursor: hand;");
            editBtn.setTooltip(new Tooltip("Yorumu düzenle"));
            editBtn.setOnAction(e -> editComment(commentId, commentLabel, commentRow, commentContent));

            // Sil butonu
            Button deleteBtn = new Button("Sil");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                    "-fx-font-size: 10px; -fx-padding: 2 5; -fx-cursor: hand;");
            deleteBtn.setTooltip(new Tooltip("Yorumu sil"));
            deleteBtn.setOnAction(e -> deleteComment(commentId, commentRow));

            buttonContainer.getChildren().addAll(editBtn, deleteBtn);
            commentRow.getChildren().add(buttonContainer);
        }

        return commentRow;
    }

    private void editComment(int commentId, Label commentLabel, HBox commentRow, VBox commentContent) {
        // TextField oluştur
        TextField editField = new TextField(commentLabel.getText());
        editField.setStyle("-fx-font-size: 12px; -fx-padding: 2 5; -fx-background-radius: 3; -fx-border-radius: 3;");

        // Butonlar
        HBox editButtons = new HBox(5);

        Button saveBtn = new Button("Kaydet");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10px; " +
                "-fx-padding: 3 8; -fx-background-radius: 3;");
        saveBtn.setOnAction(e -> {
            String newText = editField.getText().trim();
            if (!newText.isEmpty() && !newText.equals(commentLabel.getUserData())) {
                updateCommentInDatabase(commentId, newText);
                commentLabel.setText(newText);
                commentLabel.setUserData(newText);
                // TextField'ı kaldır, Label'ı geri getir
                commentContent.getChildren().set(1, commentLabel);
            }
        });

        Button cancelBtn = new Button("İptal");
        cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; " +
                "-fx-padding: 3 8; -fx-background-radius: 3;");
        cancelBtn.setOnAction(e -> {
            // Düzenlemeyi iptal et, Label'ı geri getir
            commentContent.getChildren().set(1, commentLabel);
        });

        editButtons.getChildren().addAll(saveBtn, cancelBtn);

        // Label'ı TextField ve butonlarla değiştir
        VBox editContainer = new VBox(5);
        editContainer.getChildren().addAll(editField, editButtons);
        commentContent.getChildren().set(1, editContainer);

        // TextField'a odaklan
        editField.requestFocus();
        editField.selectAll();
    }

    private void updateCommentInDatabase(int commentId, String newText) {
        String sql = "UPDATE Yorumlar SET content_text = ? WHERE comment_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newText);
            ps.setInt(2, commentId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Yorum güncellendi: " + commentId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Hata", "Yorum güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    private void deleteComment(int commentId, HBox commentRow) {
        // Silme onayı
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Yorum Sil");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bu yorumu silmek istediğinize emin misiniz? Bu işlem geri alınamaz.");

        ButtonType yesButton = new ButtonType("Evet", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Hayır", ButtonBar.ButtonData.NO);
        confirmAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                // Veritabanından sil
                String sql = "DELETE FROM Yorumlar WHERE comment_id = ?";

                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, commentId);
                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Yorum silindi: " + commentId);
                        // UI'dan kaldır
                        commentRow.setVisible(false);
                        commentRow.setManaged(false);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Hata", "Yorum silinirken hata oluştu: " + e.getMessage());
                }
            }
        });
    }

    private void openCommentDialog(int postId, VBox commentsContainer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Yorum Yap");
        dialog.setResizable(false);

        VBox dialogContent = new VBox(15);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setStyle("-fx-background-color: white;");

        Label title = new Label("Yorumunuzu yazın:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Yorumunuzu buraya yazın...");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(4);
        commentArea.setStyle("-fx-font-size: 13px;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("İptal");
        cancelBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-padding: 8 15;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button submitBtn = new Button("Yorum Yap");
        submitBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        submitBtn.setOnAction(e -> {
            String commentText = commentArea.getText().trim();
            if (!commentText.isEmpty()) {
                submitComment(postId, commentText);
                dialog.close();

                // Yorumları yenile
                if (commentsContainer != null) {
                    loadCommentsIntoContainer(postId, commentsContainer);
                }
            } else {
                showErrorAlert("Hata", "Lütfen yorumunuzu yazın.");
            }
        });

        buttons.getChildren().addAll(cancelBtn, submitBtn);
        dialogContent.getChildren().addAll(title, commentArea, buttons);

        javafx.scene.Scene scene = new javafx.scene.Scene(dialogContent, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void submitComment(int postId, String commentText) {
        String sql = "INSERT INTO Yorumlar (post_id, user_id, content_text, created_at) VALUES (?, ?, ?, GETDATE())";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, currentUserId);
            ps.setString(3, commentText);
            ps.executeUpdate();

            System.out.println("Yorum eklendi: PostID=" + postId);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Hata", "Yorum kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    private void refreshPost(int postId) {
        // Tüm gönderileri yeniden yükle (basit çözüm)
        loadAllPosts();
    }

    // --- YARDIMCI METOTLAR (değişmeden kaldı) ---

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
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, currentUserId);
                checkPs.setInt(2, postId);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    int existingType = rs.getInt("reaction_type");
                    if (existingType == reactionType) {
                        String deleteSql = "DELETE FROM Begeniler WHERE user_id = ? AND post_id = ?";
                        try (PreparedStatement delPs = conn.prepareStatement(deleteSql)) {
                            delPs.setInt(1, currentUserId);
                            delPs.setInt(2, postId);
                            delPs.executeUpdate();
                        }
                    } else {
                        String updateSql = "UPDATE Begeniler SET reaction_type = ? WHERE user_id = ? AND post_id = ?";
                        try (PreparedStatement upPs = conn.prepareStatement(updateSql)) {
                            upPs.setInt(1, reactionType);
                            upPs.setInt(2, currentUserId);
                            upPs.setInt(3, postId);
                            upPs.executeUpdate();
                        }
                    }
                } else {
                    String insertSql = "INSERT INTO Begeniler (user_id, post_id, reaction_type, reaction_date) VALUES (?, ?, ?, GETDATE())";
                    try (PreparedStatement inPs = conn.prepareStatement(insertSql)) {
                        inPs.setInt(1, currentUserId);
                        inPs.setInt(2, postId);
                        inPs.setInt(3, reactionType);
                        inPs.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Hata", "Tepki kaydedilirken hata oluştu: " + e.getMessage());
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
        btnLike.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd; -fx-border-radius: 15; " +
                "-fx-padding: 5 15; -fx-font-size: 13px;");
        btnDislike.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd; -fx-border-radius: 15; " +
                "-fx-padding: 5 15; -fx-font-size: 13px;");

        if (userReaction == 1) {
            btnLike.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196f3; -fx-border-radius: 15; " +
                    "-fx-padding: 5 15; -fx-font-size: 13px; -fx-text-fill: #1976d2;");
        } else if (userReaction == 2) {
            btnDislike.setStyle("-fx-background-color: #ffebee; -fx-border-color: #f44336; -fx-border-radius: 15; " +
                    "-fx-padding: 5 15; -fx-font-size: 13px; -fx-text-fill: #d32f2f;");
        }
    }

    private String getTimeAgo(Timestamp timestamp) {
        if (timestamp == null) return "";

        LocalDateTime postTime = timestamp.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long diffMinutes = java.time.Duration.between(postTime, now).toMinutes();

        if (diffMinutes < 1) return "şimdi";
        if (diffMinutes < 60) return diffMinutes + " dk önce";
        if (diffMinutes < 1440) return (diffMinutes / 60) + " saat önce";
        return (diffMinutes / 1440) + " gün önce";
    }

    private String getColorForUsername(String username) {
        int hash = username.hashCode();
        String[] colors = {
                "#3498db", "#e74c3c", "#2ecc71", "#f39c12",
                "#9b59b6", "#1abc9c", "#d35400", "#27ae60"
        };
        return colors[Math.abs(hash) % colors.length];
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}