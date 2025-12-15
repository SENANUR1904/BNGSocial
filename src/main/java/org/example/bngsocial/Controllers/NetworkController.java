package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NetworkController {

    @FXML private Pane graphPane;
    private int currentUserId;
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    // Kullanıcı ID'si ile ekrandaki Daire objesini eşleştirmek için Map
    private Map<Integer, StackPane> userNodes = new HashMap<>();

    public void setUserId(int userId) {
        this.currentUserId = userId;
        drawGraph();
    }

    private void drawGraph() {
        graphPane.getChildren().clear();
        userNodes.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // 1. ADIM: Tüm Kullanıcıları Daire Olarak Çiz
            String sqlUsers = "SELECT user_id, username FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlUsers);

            Random rand = new Random();

            while (rs.next()) {
                int uid = rs.getInt("user_id");
                String uName = rs.getString("username");

                // Rastgele Konum (Pane boyutlarına göre)
                double x = 50 + rand.nextDouble() * 700;
                double y = 50 + rand.nextDouble() * 500;

                StackPane node = createNode(uName, x, y, uid == currentUserId);
                graphPane.getChildren().add(node);
                userNodes.put(uid, node); // Map'e kaydet ki sonra çizgi çekebilelim
            }

            // 2. ADIM: Normal Arkadaşlıkları Çiz (Mavi Çizgi)
            String sqlFriends = "SELECT sender_id, receiver_id FROM friendships WHERE status='ACCEPTED'";
            ResultSet rsF = stmt.executeQuery(sqlFriends);
            while(rsF.next()) {
                int u1 = rsF.getInt("sender_id");
                int u2 = rsF.getInt("receiver_id");
                drawLine(u1, u2, Color.BLUE, 2);
            }

            // 3. ADIM: Yakın Arkadaşları Çiz (Kırmızı Kalın Çizgi)
            String sqlClose = "SELECT user_id, close_friend_id FROM close_friends";
            ResultSet rsC = stmt.executeQuery(sqlClose);
            while(rsC.next()) {
                int u1 = rsC.getInt("user_id");
                int u2 = rsC.getInt("close_friend_id");
                drawLine(u1, u2, Color.RED, 4); // Daha kalın
            }

            // Dairelerin çizgilerin üstünde durması için tekrar öne getir
            for (StackPane node : userNodes.values()) {
                node.toFront();
            }

        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Daire ve İsim Oluşturucu
    private StackPane createNode(String name, double x, double y, boolean isMe) {
        StackPane stack = new StackPane();
        stack.setLayoutX(x);
        stack.setLayoutY(y);

        Circle c = new Circle(25);
        // Giriş yapan kullanıcıysa Yeşil, değilse Gri yap
        c.setFill(isMe ? Color.LIGHTGREEN : Color.LIGHTGRAY);
        c.setStroke(Color.BLACK);

        // İsmin baş harfi veya tamamı
        Label l = new Label(name.length() > 2 ? name.substring(0, 2).toUpperCase() : name);
        l.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));

        stack.getChildren().addAll(c, l);
        return stack;
    }

    // İki ID arasına çizgi çeken metot
    private void drawLine(int id1, int id2, Color color, double width) {
        StackPane node1 = userNodes.get(id1);
        StackPane node2 = userNodes.get(id2);

        if (node1 != null && node2 != null) {
            // Node'ların merkezinden merkezine çizgi
            Line line = new Line(
                    node1.getLayoutX() + 25, node1.getLayoutY() + 25,
                    node2.getLayoutX() + 25, node2.getLayoutY() + 25
            );
            line.setStroke(color);
            line.setStrokeWidth(width);
            graphPane.getChildren().add(line);
        }
    }
}