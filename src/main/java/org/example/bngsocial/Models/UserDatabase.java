package org.example.bngsocial.Models;

import java.util.ArrayList;

public class UserDatabase {

    public static ArrayList<User> users = new ArrayList<>();

    public static boolean addUser(User u) {
        for (User x : users) {
            if (x.getUsername().equals(u.getUsername()) || x.getEmail().equals(u.getEmail())) {
                return false;
            }
        }
        users.add(u);
        return true;
    }

    public static User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public static boolean resetPassword(String username, String email, String newPassword) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getEmail().equals(email)) {
                u.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }
}