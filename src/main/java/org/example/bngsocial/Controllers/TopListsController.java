package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.sql.*;

public class TopListsController {
    @FXML private VBox topLikesList;
    @FXML private VBox topCommentsList;
    private int currentUserId;
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadStats();
    }

    private void loadStats() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // En çok beğeni alan 3 kullanıcı
            String sqlLikes = "SELECT TOP 3 u.username, COUNT(r.reaction_type) as total FROM Kullanicilar u " +
                    "JOIN Gonderiler p ON u.user_id = p.user_id " +
                    "JOIN reactions r ON p.post_id = r.post_id WHERE r.reaction_type = 1 " +
                    "GROUP BY u.username ORDER BY total DESC";

            fillList(conn, sqlLikes, topLikesList, "Beğeni");

            // En çok yorum yapan 3 kullanıcı
            String sqlComments = "SELECT TOP 3 u.username, COUNT(c.comment_id) as total FROM Kullanicilar u " +
                    "JOIN comments c ON u.user_id = c.user_id " +
                    "GROUP BY u.username ORDER BY total DESC";

            fillList(conn, sqlComments, topCommentsList, "Yorum");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void fillList(Connection conn, String sql, VBox container, String suffix) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        int rank = 1;
        while(rs.next()) {
            Label l = new Label(rank + ". " + rs.getString("username") + " (" + rs.getInt("total") + " " + suffix + ")");
            l.setStyle("-fx-font-size: 14px;");
            container.getChildren().add(l);
            rank++;
        }
    }
}