package com.ev.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBConnection {

    // Matches: postgres(ql)://user:password@host:port/dbname
    private static final Pattern DB_URL_PATTERN =
        Pattern.compile("postgres(?:ql)?://([^:]+):([^@]+)@([^:/]+):(\\d+)/([^?]+)");

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
            Matcher m = DB_URL_PATTERN.matcher(fullUrl);
            if (!m.matches()) {
                throw new SQLException("Could not parse DATABASE_URL (unexpected format). Value length: " + fullUrl.length());
            }

            String user = m.group(1);
            String pass = m.group(2);
            String host = m.group(3);
            String port = m.group(4);
            String db   = m.group(5);

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?sslmode=require";

            System.out.println("[DBConnection] Connecting to host=" + host + " port=" + port + " db=" + db + " user=" + user);

            return DriverManager.getConnection(jdbcUrl, user, pass);
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
