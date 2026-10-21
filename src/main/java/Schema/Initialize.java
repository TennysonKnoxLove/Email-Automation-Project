package Schema;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import Backend.Utils.DB;

public class Initialize {

    private static final String[] TABLE_FILES = { "user.sql", "contacts.sql" };

    public static void apply() throws SQLException {
        loadDriver();
        ensureDatabase();
        try (Connection connection = DB.connect();
             Statement statement = connection.createStatement()) {
            for (String fileName : TABLE_FILES) {
                statement.execute(loadStatement(fileName));
            }
        }
    }

    private static void loadDriver() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found.", e);
        }
    }

    private static void ensureDatabase() throws SQLException {
        String name = databaseName();
        try (Connection connection = DriverManager.getConnection(serverUrl(), DB.DB_USER, DB.DB_PASS);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + name + "`");
        }
    }

    private static String databaseName() throws SQLException {
        String url = DB.URL;
        int slash = url.lastIndexOf('/');
        if (slash < 0 || slash == url.length() - 1) {
            throw new SQLException("Database URL has no database name: " + url);
        }
        String name = url.substring(slash + 1);
        int query = name.indexOf('?');
        if (query >= 0) {
            name = name.substring(0, query);
        }
        if (!name.matches("[A-Za-z0-9_]+")) {
            throw new SQLException("Unsupported database name: " + name);
        }
        return name;
    }

    private static String serverUrl() {
        String url = DB.URL;
        int slash = url.lastIndexOf('/');
        return url.substring(0, slash + 1);
    }

    private static String loadStatement(String fileName) throws SQLException {
        Path path = locate(fileName);
        StringBuilder sql = new StringBuilder();
        try {
            for (String line : Files.readAllLines(path)) {
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
            throw new SQLException("Could not read schema file: " + path, e);
        }
        String statement = sql.toString().trim();
        if (statement.endsWith(";")) {
            statement = statement.substring(0, statement.length() - 1).trim();
        }
        if (statement.isEmpty()) {
            throw new SQLException("Schema file is empty: " + path);
        }
        return statement;
    }

    private static Path locate(String fileName) throws SQLException {
        Path fromWorkingDirectory = Path.of("Schema", fileName);
        if (Files.isRegularFile(fromWorkingDirectory)) {
            return fromWorkingDirectory;
        }
        URL resource = Initialize.class.getResource(fileName);
        if (resource != null) {
            try {
                Path besideClass = Path.of(resource.toURI());
                if (Files.isRegularFile(besideClass)) {
                    return besideClass;
                }
            } catch (URISyntaxException e) {
                throw new SQLException("Could not locate schema file: " + fileName, e);
            }
        }
        throw new SQLException("Schema file not found: " + fileName);
    }
}
