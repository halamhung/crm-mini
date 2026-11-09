package com.crm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Mot noi duy nhat de cau hinh ket noi MySQL.
public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = getSetting("DB_URL", "jdbc:mysql://localhost:3307/CRM?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh");
        String username = getSetting("DB_USER", "crm");
        String password = getSetting("DB_PASSWORD", "");
        return DriverManager.getConnection(url, username, password);
    }

    private static String getSetting(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        // System property duoc dung trong cac bai kiem thu H2.
        return System.getProperty(name, defaultValue);
    }
}
