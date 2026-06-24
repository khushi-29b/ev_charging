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
            try {
                // Strip the scheme (postgres:// or postgresql://)
                String withoutScheme = fullUrl.substring(fullUrl.indexOf("://") + 3);

                // Split off query string if present (e.g. ?sslmode=require)
                String query = "";
                int qIdx = withoutScheme.indexOf('?');
                if (qIdx != -1) {
                    query = withoutScheme.substring(qIdx); // includes leading "?"
                    withoutScheme = withoutScheme.substring(0, qIdx);
                }

                // withoutScheme is now: user:pass@host:port/dbname
                int atIdx = withoutScheme.lastIndexOf('@'); // lastIndexOf in case password contains '@'
                String userInfo = withoutScheme.substring(0, atIdx);
                String hostPortDb = withoutScheme.substring(atIdx + 1);

                int colonIdx = userInfo.indexOf(':');
                String user = userInfo.substring(0, colonIdx);
                String pass = userInfo.substring(colonIdx + 1);

                int slashIdx = hostPortDb.indexOf('/');
                String hostPort = hostPortDb.substring(0, slashIdx);
                String db = hostPortDb.substring(slashIdx + 1);

                int hostColonIdx = hostPort.lastIndexOf(':');
                String host = hostPort.substring(0, hostColonIdx);
                String port = hostPort.substring(hostColonIdx + 1);

                String sslParam = query.contains("sslmode=") ? query : (query.isEmpty() ? "?sslmode=require" : query + "&sslmode=require");
                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db + sslParam;

                System.out.println("[DBConnection] host=" + host + " port=" + port + " db=" + db + " user=" + user);

                return DriverManager.getConnection(jdbcUrl, user, pass);
            } catch (Exception e) {
                throw new SQLException("Failed to parse DATABASE_URL. Length=" + fullUrl.length()
                        + " StartsWith=" + fullUrl.substring(0, Math.min(15, fullUrl.length()))
                        + " Error=" + e.getMessage(), e);
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
