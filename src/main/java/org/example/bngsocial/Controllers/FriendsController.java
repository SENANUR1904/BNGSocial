package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.sql.*;

public class FriendsController {

    @FXML private VBox myFriendsContainer;
    @FXML private VBox allUsersContainer;

    private int currentUserId;
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadMyFriends();
        loadAllUsers();
    }

    // TAB 1: Arkadaşlarımı Listele
    private void loadMyFriends() {
        myFriendsContainer.getChildren().clear();
        // Hem gönderen hem alan olabilirim
        String sql = "SELECT u.user_id, u.username FROM users u " +
                "JOIN friendships f ON (u.user_id = f.sender_id OR u.user_id = f.receiver_id) " +
                "WHERE (f.sender_id = ? OR f.receiver_id = ?) AND u.user_id != ? AND f.status = 'ACCEPTED'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId); ps.setInt(2, currentUserId); ps.setInt(3, currentUserId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int friendId = rs.getInt("user_id");
                String name = rs.getString("username");
                myFriendsContainer.getChildren().add(createRow(name, "Arkadaşlıktan Çıkar", "red", e -> removeFriend(friendId)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // TAB 2: Arkadaş Olmadığım Kişileri Listele
    private void loadAllUsers() {
        allUsersContainer.getChildren().clear();
        // Kendim hariç ve zaten arkadaş olduklarım hariç herkesi getir
        String sql = "SELECT user_id, username FROM users WHERE user_id != ? " +
                "AND user_id NOT IN (SELECT sender_id FROM friendships WHERE receiver_id = ? AND status='ACCEPTED') " +
                "AND user_id NOT IN (SELECT receiver_id FROM friendships WHERE sender_id = ? AND status='ACCEPTED')";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId); ps.setInt(2, currentUserId); ps.setInt(3, currentUserId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int targetId = rs.getInt("user_id");
                String name = rs.getString("username");
                allUsersContainer.getChildren().add(createRow(name, "Arkadaş Ekle", "green", e -> addFriend(targetId)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Yardımcı Metot: Liste satırı oluşturur
    private HBox createRow(String name, String btnText, String colorClass, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 2, 0, 0, 1);");

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblName.setPrefWidth(200);

        Button btn = new Button(btnText);
        if(colorClass.equals("red")) btn.setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c62828;");
        else btn.setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #2e7d32;");

        btn.setOnAction(action);

        row.getChildren().addAll(lblName, btn);
        return row;
    }

    private void addFriend(int targetId) {
        // Basitlik için direkt ACCEPTED ekliyoruz (İstek mantığı istenirse PENDING yapılabilir)
        String sql = "INSERT INTO friendships (sender_id, receiver_id, status) VALUES (?, ?, 'ACCEPTED')";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId); ps.setInt(2, targetId);
            ps.executeUpdate();
            loadAllUsers(); // Listeyi yenile
            loadMyFriends();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void removeFriend(int friendId) {
        String sql = "DELETE FROM friendships WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId); ps.setInt(2, friendId);
            ps.setInt(3, friendId); ps.setInt(4, currentUserId);
            ps.executeUpdate();
            loadMyFriends(); // Listeyi yenile
            loadAllUsers();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}