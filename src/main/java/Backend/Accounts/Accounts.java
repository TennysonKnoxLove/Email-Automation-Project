package Backend.Accounts;

import java.sql.*;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import Backend.Utils.DB;
import Backend.Utils.Email;

public class Accounts {

    public static void retrieveUsername(String email) throws AddressException, MessagingException, IOException, InterruptedException, URISyntaxException, SQLException {
        String username = getUsername(email);
        if (username == null) {
            throw new IOException("No account found for that email.");
        }
        String[] recipient = { email };
        Email.sendUser(recipient, "Forgot Username", "The username assigned to this account is: " + username);
    }

    public static String getUsername(String email) throws SQLException {
        String sql = "SELECT username FROM user WHERE email = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }
        }
        return null;
    }

    public static void forgotPassword(String user) throws AddressException, MessagingException, IOException, InterruptedException, URISyntaxException, SQLException {
        String email = getEmail(user);
        if (email == null) {
            throw new IOException("No account found for that username.");
        }
        String[] recipient = { email };
        Email.sendUser(recipient, "Forgot Password", "Follow instructions to reset your password.");
    }

    public static boolean checkVerificationStatus(String user) throws SQLException {
        String sql = "SELECT verified FROM user WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("verified");
                }
            }
        }
        return false;
    }

    public static void sendVerificationEmail(String email) throws AddressException, MessagingException, IOException, InterruptedException, URISyntaxException {
        String[] recipient = { email };
        Email.sendUser(recipient, "Account Verification", "Hello, this is just to verifiy your accont. Please click here: localhost:8080/verify");
    }

    public static String getEmail(String user) throws SQLException {
        String sql = "SELECT email FROM user WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        }
        return null;
    }

    public static int getID(String user) throws SQLException {
        String sql = "SELECT id FROM user WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return 0;
    }

    public static void verifyActivity(int id, boolean tf) throws SQLException {
        if (id == 0) {
            System.out.println("Something went wrong...");
            return;
        }
        String sql = "UPDATE user SET onlinestatus = ? WHERE id = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, tf);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    public static void logOut(String user) throws SQLException {
        verifyActivity(getID(user), false);
    }

    public static boolean attemptLogin(String user, String pass) throws AddressException, MessagingException, IOException, InterruptedException, URISyntaxException, SQLException {
        String sql = "SELECT id, password, verified, email FROM user WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    System.out.println("Login Failed.");
                    return false;
                }
                if (!resultSet.getBoolean("verified")) {
                    System.out.println("Check your email! Please verify your account!");
                    String email = resultSet.getString("email");
                    if (email != null) {
                        try {
                            sendVerificationEmail(email);
                        } catch (Exception e) {
                            System.out.println("Could not send verification email: " + e.getMessage());
                        }
                    }
                    return false;
                }
                if (resultSet.getString("password").equals(pass)) {
                    verifyActivity(resultSet.getInt("id"), true);
                    System.out.println("Login Successful");
                    return true;
                }
            }
        }

        System.out.println("Login Failed.");
        return false;
    }

    /**
     * @return "ok" on success, otherwise a human-readable error message
     */
    public static String createAccount(String user, String pass, String repeatPass, String email) throws SQLException {
        if (user == null || user.isBlank() || pass == null || pass.isBlank() || email == null || email.isBlank()) {
            return "Fill in all fields.";
        }
        if (!pass.equals(repeatPass)) {
            return "Password does not match!";
        }

        try (Connection connection = DB.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO user (username, password, onlinestatus, logincounter, verified, email) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setBoolean(3, false);
            ps.setInt(4, 0);
            ps.setBoolean(5, true);
            ps.setString(6, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (e.getErrorCode() == 1062 && message.contains("username")) {
                return "Username already taken!";
            }
            throw e;
        }
        try {
            sendVerificationEmail(email);
        } catch (Exception e) {
            System.out.println("Account created, but verification email failed: " + e.getMessage());
        }
        System.out.println("Account Created.");
        return "ok";
    }
}
