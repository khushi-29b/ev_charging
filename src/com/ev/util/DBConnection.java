package com.ev.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String fullUrl = System.getenv("DATABASE_URL");

        if (fullUrl != null && !fullUrl.isEmpty()) {
            // Render/Heroku-style: postgres://user:pass@host:port/dbname
            try {
                java.net.URI uri = new java.net.URI(fullUrl);
                String userInfo = uri.getUserInfo();
                String user = "postgres";
                String pass = "";
                if (userInfo != null) {
                    String[] parts = userInfo.split(":", 2);
                    user = parts[0];
                    pass = parts.length > 1 ? parts[1] : "";
                }
                String db = uri.getPath().startsWith("/") ? uri.getPath().substring(1) : uri.getPath();
                String sep = fullUrl.contains("sslmode=") ? "" : "?sslmode=require";
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + "/" + db + sep;

                return DriverManager.getConnection(jdbcUrl, user, pass);
            } catch (Exception e) {
                throw new SQLException("Invalid DATABASE_URL format: " + fullUrl, e);
            }
        }

        // Fallback: individual env vars, or local defaults for running on your own machine
        String host = getEnvOrDefault("DB_HOST", "localhost");
        String port = getEnvOrDefault("DB_PORT", "5432");
        String name = getEnvOrDefault("DB_NAME", "ev_charging_network");
        String user = getEnvOrDefault("DB_USER", "postgres");
        String pass = getEnvOrDefault("DB_PASS", "root");

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + name;
        return DriverManager.getConnection(jdbcUrl, user, pass);
    }

    private static String getEnvOrDefault(String key, String def) {
        String val = System.getenv(key);
        return (val != null && !val.isEmpty()) ? val : def;
    }
}
