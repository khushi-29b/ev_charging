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
                int atIdx = fullUrl.lastIndexOf('@');
                int schemeEnd = fullUrl.indexOf("://");

                if (atIdx == -1 || schemeEnd == -1 || atIdx < schemeEnd) {
                    StringBuilder masked = new StringBuilder();
                    for (int i = 0; i < fullUrl.length(); i++) {
                        char c = fullUrl.charAt(i);
                        if (c == ':' || c == '/' || c == '@' || c == '?' || c == '&' || c == '=' || (Character.isLetter(c) && i < schemeEnd + 3)) {
                            masked.append(c);
                        } else if (Character.isDigit(c)) {
                            masked.append('#');
                        } else {
                            masked.append('*');
                        }
                    }
                    throw new SQLException("DATABASE_URL structure unexpected. atIdx=" + atIdx
                            + " schemeEnd=" + schemeEnd + " len=" + fullUrl.length()
                            + " masked=" + masked.toString());
                }

                String withoutScheme = fullUrl.substring(schemeEnd + 3);
                atIdx = withoutScheme.lastIndexOf('@');

                String query = "";
                int qIdx = withoutScheme.indexOf('?');
                if (qIdx != -1 && qIdx > atIdx) {
                    query = withoutScheme.substring(qIdx);
                    withoutScheme = withoutScheme.substring(0, qIdx);
                }

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
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new SQLException("Failed to parse DATABASE_URL. Length=" + fullUrl.length()
                        + " Error=" + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            }
        }

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
