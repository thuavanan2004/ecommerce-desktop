package com.javaadv.Model;

public class User {
    private static User currentUser; // Singleton pattern

    private String username;
    private String password;
    private String fullName;
    private String avatarPath;
    private String accessToken; // Quan trọng

    // Private constructor để đảm bảo singleton
    public User() {}

    // Singleton instance
    public static User getCurrentUser() {
        if (currentUser == null) {
            currentUser = new User();
        }
        return currentUser;
    }

    public User(String accessToken, String avatarPath, String fullName, String password, String username) {
        this.accessToken = accessToken;
        this.avatarPath = avatarPath;
        this.fullName = fullName;
        this.password = password;
        this.username = username;
    }

    // Reset khi logout
    public static void clearCurrentUser() {
        currentUser = null;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter/Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}