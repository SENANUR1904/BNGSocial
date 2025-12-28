package org.example.bngsocial.Models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabase {

    private static final String CONNECTION_URL = "jdbc:sqlserver://DESKTOP-1JTGLF8;databaseName=BNGSocialDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("SQL Driver bulunamadı!");
        }
        return DriverManager.getConnection(CONNECTION_URL);
    }

    // Şifreleme Metodu (SHA-256)
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // KULLANICI EKLEME (Register)
    public static boolean addUser(User u) {
        // Tablo adınızın 'kullanicilar' olduğunu varsayıyoruz.
        String checkQuery = "SELECT COUNT(*) FROM Kullanicilar WHERE username = ? OR email = ?";
        String insertQuery = "INSERT INTO Kullanicilar (full_name, username, email, password_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, u.getUsername());
            checkStmt.setString(2, u.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Kullanıcı zaten var
            }

            insertStmt.setString(1, u.getName());
            insertStmt.setString(2, u.getUsername());
            insertStmt.setString(3, u.getEmail());
            // Şifreyi hashleyerek kaydediyoruz
            insertStmt.setString(4, hashPassword(u.getPassword()));

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UserDatabase.java içinde
    public static User login(String username, String password) {
        // Tablo adın hata mesajına göre 'Kullanicilar' olmalı
        String query = "SELECT * FROM Kullanicilar WHERE username = ? AND password_hash = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // --- KRİTİK KISIM BAŞLANGICI ---
                // Veritabanındaki ID sütununun tam adını yazmalısın.
                // Hata mesajında column 'user_id' dediği için buraya "user_id" yazıyoruz.
                int dbId = rs.getInt("user_id");
                // --- KRİTİK KISIM BİTİŞİ ---

                String dbName = rs.getString("full_name");
                String dbUsername = rs.getString("username");
                String dbEmail = rs.getString("email");
                String dbPassword = rs.getString("password_hash");

                // ID'yi de kurucu metoda (Constructor) gönderiyoruz
                return new User(dbId, dbName, dbUsername, dbEmail, dbPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ŞİFRE GÜNCELLEME
    public static boolean resetPassword(String username, String email, String newPassword) {
        String query = "UPDATE Kullanicilar SET password_hash = ? WHERE username = ? AND email = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hashPassword(newPassword));
            stmt.setString(2, username);
            stmt.setString(3, email);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}