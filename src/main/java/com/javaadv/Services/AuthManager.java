package com.javaadv.Services;

public class AuthManager {
    private static String accessToken = null;

    // Lấy token hiện tại
    public static String getAccessToken() {
        return accessToken;
    }

    // Cập nhật token
    public static void setAccessToken(String token) {
        accessToken = token;
    }

    // Kiểm tra xem đã có token hay chưa
    public static boolean isAuthenticated() {
        return accessToken != null && !accessToken.isEmpty();
    }
}
