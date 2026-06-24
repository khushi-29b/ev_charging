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
        // Prefer individual env vars first - simplest and most reliable.
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String name = System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        if (host != null && !host.isEmpty()) {
            String jdbcUrl = "jdbc:postgresql://" + host + ":" +
                    (port == null || port.isEmpty() ? "5432" : port) +
                    "/" + (name == null || name.isEmpty() ? "ev_charging_network" : name) +
                    "?sslmode=require";

            System.out.println("[DBConnection] Using individual env vars. host=" + host + " port=" + port + " db=" + name + " user=" + user);

            return DriverManager.getConnection(jdbcUrl, user, pass);
        }

        // Fallback: parse DATABASE_URL if individual vars aren't set
        String fullUrl = System.getenv("DATABASE_URL");
        if (fullUrl != null && !fullUrl.isEmpty()) {
            try {
                int schemeEnd = fullUrl.indexOf("://");
                String withoutScheme = fullUrl.substring(schemeEnd + 3);

                int qIdx = withoutScheme.indexOf('?');
                String query = "";
                if (qIdx != -1) {
                    query = withoutScheme.substring(qIdx);
                    withoutScheme = withoutScheme.substring(0, qIdx);
                }

                int atIdx = withoutScheme.lastIndexOf('@');
                String userInfo = withoutScheme.substring(0, atIdx);
                String hostPortDb = withoutScheme.substring(atIdx + 1);

                int colonIdx = userInfo.indexOf(':');
                String u = userInfo.substring(0, colonIdx);
                String p = userInfo.substring(colonIdx + 1);

                int slashIdx = hostPortDb.indexOf('/');
                String hostPort = hostPortDb.substring(0, slashIdx);
                String db = hostPortDb.substring(slashIdx + 1);

                int hostColonIdx = hostPort.lastIndexOf(':');
                String h = hostPort.substring(0, hostColonIdx);
                String pt = hostPort.substring(hostColonIdx + 1);

                String sslParam = query.contains("sslmode=") ? query : "?sslmode=require";
                String jdbcUrl = "jdbc:postgresql://" + h + ":" + pt + "/" + db + sslParam;

                System.out.println("[DBConnection] Using DATABASE_URL. host=" + h + " port=" + pt + " db=" + db);

                return DriverManager.getConnection(jdbcUrl, u, p);
            } catch (Exception e) {
                throw new SQLException("Could not connect using DB_HOST or DATABASE_URL. "
                        + "DB_HOST was empty/missing, and parsing DATABASE_URL failed: " + e.getMessage(), e);
            }
        }

        // Last resort: local defaults for your own machine
        String jdbcUrl = "jdbc:postgresql://localhost:5432/ev_charging_network";
        return DriverManager.getConnection(jdbcUrl, "postgres", "root");
    }
}
