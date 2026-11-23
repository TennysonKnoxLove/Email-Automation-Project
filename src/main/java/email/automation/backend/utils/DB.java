package email.automation.backend.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DB {
    private DB() {}

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found.", e);
        }
        return DriverManager.getConnection(jdbcUrl(), required("DB_USER"), required("DB_PASSWORD"));
    }

    private static String jdbcUrl() throws SQLException {
        return "jdbc:mysql://" + host() + ":" + port() + "/" + databaseName()
                + "?sslMode=DISABLED&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    }

    private static String databaseName() throws SQLException {
        String name = optional("DB_NAME", "users");
        if (!name.matches("[A-Za-z0-9_]+")) {
            throw new SQLException("Unsupported DB_NAME.");
        }
        return name;
    }

    private static String host() throws SQLException {
        String host = optional("DB_HOST", "localhost");
        if (!host.matches("[A-Za-z0-9._-]+")) {
            throw new SQLException("Unsupported DB_HOST.");
        }
        return host;
    }

    private static String port() throws SQLException {
        String port = optional("DB_PORT", "3306");
        if (!port.matches("[0-9]+")) {
            throw new SQLException("Unsupported DB_PORT.");
        }
        return port;
    }

    private static String required(String name) throws SQLException {
        try {
            return Env.requiredEnv(name);
        } catch (IOException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    private static String optional(String name, String fallback) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
