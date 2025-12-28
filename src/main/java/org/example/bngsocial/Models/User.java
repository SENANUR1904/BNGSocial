package org.example.bngsocial.Models;

public class User {
    // ORTAK ALANLAR
    private int id;         // DB: user_id, TXT: 101, 102...
    private String name;    // DB: full_name, TXT: İsim
    private String source;  // "DB" (Veritabanı) veya "TXT" (Dosya)

    // SADECE DB'DEN GELENLER (TXT için boş kalır)
    private String username;
    private String email;
    private String password;
    private String profilePhoto;

    // SADECE TXT'DEN GELENLER (DB için boş kalır)
    private String gender;   // E veya K

    // --- 1. CONSTRUCTOR: SQL Veritabanı İçin (Login/Register) ---
    public User(int id, String name, String username, String email, String password, String profilePhoto) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.source = "DB";
        this.gender = "-";
    }

    // Eski kodunla uyum için (Overloading)
    public User(int id, String name, String username, String email, String password) {
        this(id, name, username, email, password, null);
    }

    // Yeni Kayıt için (Register)
    public User(String name, String username, String email, String password) {
        this(0, name, username, email, password, null);
        this.source = "NEW";
    }

    // --- 2. CONSTRUCTOR: TXT Dosyası İçin (Kisiler.txt) ---
    public User(int id, String name, String gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.source = "TXT";

        // TXT'de olmayanlar için varsayılanlar
        this.username = "user_" + id;
        this.email = "-";
        this.password = "-";
    }

    // GETTER & SETTER
    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getProfilePhoto() { return profilePhoto; }
    public String getGender() { return gender; } // TXT için
    public String getSource() { return source; }
}