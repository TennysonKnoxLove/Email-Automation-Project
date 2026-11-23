package email.automation.backend.accounts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import email.automation.backend.utils.DB;
import email.automation.backend.utils.Email;
import email.automation.backend.utils.Passwords;

public class Accounts {

    public enum LoginResult {
        SUCCESS, BAD_CREDENTIALS, UNVERIFIED
    }

    public static void retrieveUsername(String email) throws AddressException, MessagingException, IOException, InterruptedException, URISyntaxException, SQLException {
        String username = getUsername(email);
        if (username == null) {
            throw new IOException("No account found for that email.");
        }
        String[] recipient = { email };
        Email.sendFixed(recipient, "Forgot Username", "The username assigned to this account is: " + username);
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

    public static void sendPasswordCode(String user) throws AddressException, MessagingException, IOException, SQLException {
        String email = getEmail(user);
        if (email == null) {
            throw new IOException("No account found for that username.");
        }
        String code = Passwords.newCode();
        storeColumn("reset_code", user, Passwords.hash(code));
        Email.sendFixed(new String[] { email }, "Password reset code", "Your password reset code is: " + code);
    }

    public static void resetPassword(String user, String code, String newPassword) throws SQLException, IOException {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IOException("Enter a new password.");
        }
        String stored = readColumn("reset_code", user);
        if (stored == null || code == null || !Passwords.verify(code.trim(), stored)) {
            throw new IOException("Verification code does not match.");
        }
        String sql = "UPDATE user SET password = ?, reset_code = NULL WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Passwords.hash(newPassword));
            statement.setString(2, user);
            if (statement.executeUpdate() != 1) {
                throw new IOException("No account found for that username.");
            }
        }
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

    public static void sendVerificationCode(String user) throws AddressException, MessagingException, IOException, SQLException {
        String email = getEmail(user);
        if (email == null) {
            throw new IOException("No account found for that username.");
        }
        if (checkVerificationStatus(user)) {
            throw new IOException("Account is already verified.");
        }
        String code = Passwords.newCode();
        storeColumn("verification_code", user, Passwords.hash(code));
        Email.sendFixed(new String[] { email }, "Verify your account",
                "Your Email Automation verification code is: " + code + ". Enter it after you log in.");
    }

    public static void confirmVerification(String user, String code) throws SQLException, IOException {
        if (code == null || code.isBlank()) {
            throw new IOException("Enter the verification code.");
        }
        String stored = readColumn("verification_code", user);
        if (stored == null || !Passwords.verify(code.trim(), stored)) {
            throw new IOException("Verification code does not match.");
        }
        String sql = "UPDATE user SET verified = TRUE, verification_code = NULL WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            if (statement.executeUpdate() != 1) {
                throw new IOException("No account found for that username.");
            }
        }
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

    public static void markOnline(String user) throws SQLException {
        verifyActivity(getID(user), true);
    }

    public static LoginResult attemptLogin(String user, String pass) throws SQLException {
        String sql = "SELECT id, password, verified FROM user WHERE username = ?";
        int id;
        String stored;
        boolean verified;
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return LoginResult.BAD_CREDENTIALS;
                }
                id = resultSet.getInt("id");
                stored = resultSet.getString("password");
                verified = resultSet.getBoolean("verified");
            }
        }
        if (!passwordMatches(stored, pass)) {
            return LoginResult.BAD_CREDENTIALS;
        }
        if (!Passwords.isHashed(stored)) {
            storeColumn("password", user, Passwords.hash(pass));
        }
        if (!verified) {
            return LoginResult.UNVERIFIED;
        }
        verifyActivity(id, true);
        return LoginResult.SUCCESS;
    }

    /**
     * @return "ok" when the account and verification email are ready,
     *         "email-failed" when the account exists but the email did not send,
     *         otherwise a human-readable error message
     */
    public static String createAccount(String user, String pass, String repeatPass, String email) throws SQLException {
        if (user == null || user.isBlank() || pass == null || pass.isBlank() || email == null || email.isBlank()) {
            return "Fill in all fields.";
        }
        if (!pass.equals(repeatPass)) {
            return "Password does not match!";
        }

        String code = Passwords.newCode();
        try (Connection connection = DB.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO user (username, password, onlinestatus, logincounter, verified, email, verification_code) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, user);
            ps.setString(2, Passwords.hash(pass));
            ps.setBoolean(3, false);
            ps.setInt(4, 0);
            ps.setBoolean(5, false);
            ps.setString(6, email);
            ps.setString(7, Passwords.hash(code));
            ps.executeUpdate();
        } catch (SQLException e) {
            String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (e.getErrorCode() == 1062 && message.contains("uq_user_email")) {
                return "An account with that email already exists.";
            }
            if (e.getErrorCode() == 1062 && message.contains("uq_user_username")) {
                return "Username already taken!";
            }
            throw e;
        }
        try {
            Email.sendFixed(new String[] { email }, "Verify your account",
                    "Your Email Automation verification code is: " + code + ". Enter it after you log in.");
        } catch (Exception e) {
            return "email-failed";
        }
        return "ok";
    }

    private static boolean passwordMatches(String stored, String pass) {
        if (Passwords.isHashed(stored)) {
            return Passwords.verify(pass, stored);
        }
        return stored != null && stored.equals(pass);
    }

    private static void storeColumn(String column, String user, String value) throws SQLException {
        String sql = "UPDATE user SET " + codeColumn(column) + " = ? WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setString(2, user);
            statement.executeUpdate();
        }
    }

    private static String readColumn(String column, String user) throws SQLException {
        String sql = "SELECT " + codeColumn(column) + " FROM user WHERE username = ?";
        try (Connection connection = DB.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            }
        }
        return null;
    }

    private static String codeColumn(String column) {
        if ("verification_code".equals(column) || "reset_code".equals(column) || "password".equals(column)) {
            return column;
        }
        throw new IllegalArgumentException("Unsupported column.");
    }
}
