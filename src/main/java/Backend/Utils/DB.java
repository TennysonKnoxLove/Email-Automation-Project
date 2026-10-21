package Backend.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DB {
    public static final String URL = "jdbc:mysql://localhost/users";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "";

    
    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found.", e);
        }
        return DriverManager.getConnection(DB.URL, DB.DB_USER, DB.DB_PASS);
    }
}