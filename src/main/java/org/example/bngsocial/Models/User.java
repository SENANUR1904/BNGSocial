package org.example.bngsocial.Models;

public class User {
    private int id; // EKLENDİ: Veritabanındaki user_id
    private String name;
    private String username;
    private String email;
    private String password;

    // 1. CONSTRUCTOR: Veritabanından veri çekerken (Login) kullanılır. ID bellidir.
    public User(int id, String name, String username, String email, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // 2. CONSTRUCTOR: Yeni kayıt oluştururken (Register) kullanılır. ID henüz yoktur (0).
    public User(String name, String username, String email, String password) {
        this(0, name, username, email, password);
    }

    // Getter Metodu EKLENDİ
    public int getId() {
        return id;
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }
}