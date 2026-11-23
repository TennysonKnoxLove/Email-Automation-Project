package email.automation.schema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import email.automation.backend.utils.DB;

public class Initialize {

    private static final String[] TABLE_FILES = { "user.sql", "contacts.sql" };
    private static final String[] ADD_COLUMNS = {
            "ALTER TABLE `user` ADD COLUMN verification_code VARCHAR(255) NULL",
            "ALTER TABLE `user` ADD COLUMN reset_code VARCHAR(255) NULL"
    };

    public static void apply() throws SQLException {
        try (Connection connection = DB.connect();
             Statement statement = connection.createStatement()) {
            for (String fileName : TABLE_FILES) {
                statement.execute(loadStatement(fileName));
            }
            for (String sql : ADD_COLUMNS) {
                try {
                    statement.execute(sql);
                } catch (SQLException e) {
                    if (e.getErrorCode() != 1060 && !"42S21".equals(e.getSQLState())) {
                        throw e;
                    }
                }
            }
            requireUniqueEmail(statement);
        }
    }

    private static void requireUniqueEmail(Statement statement) throws SQLException {
        try {
            statement.execute("ALTER TABLE `user` ADD UNIQUE KEY uq_user_email (email)");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new SQLException("Account emails must be unique, and this database already has duplicate addresses.", e);
            }
            if (e.getErrorCode() != 1061) {
                throw e;
            }
        }
        try {
            statement.execute("ALTER TABLE `user` DROP INDEX idx_user_email");
        } catch (SQLException e) {
            if (e.getErrorCode() != 1091) {
                throw e;
            }
        }
    }

    private static String loadStatement(String fileName) throws SQLException {
        String resource = "/schema/" + fileName;
        InputStream in = Initialize.class.getResourceAsStream(resource);
        if (in == null) {
            throw new SQLException("Schema file not found on the classpath: " + resource);
        }
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                if (sql.length() > 0) {
                    sql.append(' ');
                }
                sql.append(trimmed);
            }
        } catch (IOException e) {
            throw new SQLException("Could not read schema file: " + resource, e);
        }
        String statement = sql.toString().trim();
        if (statement.endsWith(";")) {
            statement = statement.substring(0, statement.length() - 1).trim();
        }
        if (statement.isEmpty()) {
            throw new SQLException("Schema file is empty: " + resource);
        }
        return statement;
    }
}
