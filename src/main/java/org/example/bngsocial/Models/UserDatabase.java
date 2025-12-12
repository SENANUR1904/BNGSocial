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

    // --- YENİ EKLENEN ŞİFRELEME METODU (SHA-256) ---
    // Bu metot girilen düz metni (örn: "1234") alır ve şifreli hale (hash) çevirir.
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
    // ------------------------------------------------

    // KULLANICI EKLEME (Register)
    public static boolean addUser(User u) {
        String checkQuery = "SELECT COUNT(*) FROM kullanicilar WHERE username = ? OR email = ?";
        String insertQuery = "INSERT INTO kullanicilar (full_name, username, email, password_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, u.getUsername());
            checkStmt.setString(2, u.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }

            insertStmt.setString(1, u.getName());
            insertStmt.setString(2, u.getUsername());
            insertStmt.setString(3, u.getEmail());

            // DİKKAT: Şifreyi doğrudan değil, hash'leyip kaydediyoruz!
            insertStmt.setString(4, hashPassword(u.getPassword()));

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // KULLANICI GİRİŞİ (Login)
    public static User login(String username, String password) {
        String query = "SELECT * FROM kullanicilar WHERE username = ? AND password_hash = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            // DİKKAT: Giriş yaparken girilen şifreyi hashleyip veritabanındaki hash ile kıyaslıyoruz!
            stmt.setString(2, hashPassword(password));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbName = rs.getString("full_name");
                String dbUsername = rs.getString("username");
                String dbEmail = rs.getString("email");
                // Veritabanından gelen şifre zaten hash'lidir
                String dbPassword = rs.getString("password_hash");

                return new User(dbName, dbUsername, dbEmail, dbPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ŞİFRE GÜNCELLEME (Reset Password)
    public static boolean resetPassword(String username, String email, String newPassword) {
        String query = "UPDATE kullanicilar SET password_hash = ? WHERE username = ? AND email = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // DİKKAT: Yeni şifreyi de hash'leyip güncelliyoruz!
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