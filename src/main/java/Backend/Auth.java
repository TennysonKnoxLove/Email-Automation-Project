package Backend;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import Backend.Accounts.Accounts;
import Backend.Actions.ContactActions;
import Backend.Contacts.Contacts;
import UI.Utils.EmailEditor;
import UI.Utils.Page.Layout;

public class Auth {
    public static void doLogin() {
        String user = UI.Utils.Auth.loginUser.getText().trim();
        String pass = new String(UI.Utils.Auth.loginPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            UI.Utils.Auth.loginStatus.setText("Enter username and password.");
            return;
        }
        try {
            boolean ok = Accounts.attemptLogin(user, pass);
            if (ok) {
                UI.Utils.Auth.currentUser = user;
                EmailEditor.welcomeLabel.setText("Welcome, " + user);
                ContactActions.refreshContacts();
                clearCompose();
                EmailEditor.mainStatus.setText("Logged in.");
                Layout.cards.show(Layout.root, "main");
            } else {
                UI.Utils.Auth.loginStatus.setText("Login failed — wrong password, or account not verified.");
            }
        } catch (Exception ex) {
            UI.Utils.Auth.loginStatus.setText("Login error: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
            ex.printStackTrace();
        }
    }

    public static void doRegister() {
        String user = UI.Utils.Auth.regUser.getText().trim();
        String pass = new String(UI.Utils.Auth.regPass.getPassword());
        String pass2 = new String(UI.Utils.Auth.regPass2.getPassword());
        String email = UI.Utils.Auth.regEmail.getText().trim();
        try {
            String result = Accounts.createAccount(user, pass, pass2, email);
            if ("ok".equals(result)) {
                UI.Utils.Auth.regStatus.setText("Account created.");
            } else {
                UI.Utils.Auth.regStatus.setText(result);
            }
        } catch (Exception ex) {
            UI.Utils.Auth.regStatus.setText("Could not create account: " + ex.getMessage());
        }
    }

    public static void doLogout() {
        try {
            if (UI.Utils.Auth.currentUser != null) {
                Accounts.logOut(UI.Utils.Auth.currentUser);
            }
            UI.Utils.Auth.loginStatus.setText("Logged out.");
        } catch (Exception ex) {
            UI.Utils.Auth.loginStatus.setText("Logged out. Cannot connect to MySQL.");
        }
        UI.Utils.Auth.currentUser = null;
        UI.Utils.Auth.loginPass.setText("");
        Layout.cards.show(Layout.root, "login");
    }

    public static void forgotUsername() {
        String email = JOptionPane.showInputDialog(Layout.root, "Email on the account:", "Forgot username", JOptionPane.QUESTION_MESSAGE);
        if (email == null || email.isBlank()) return;
        try {
            Accounts.retrieveUsername(email.trim());
            UI.Utils.Auth.loginStatus.setText("If that email exists, username was sent.");
        } catch (Exception ex) {
            UI.Utils.Auth.loginStatus.setText("Could not send username: " + ex.getMessage());
        }
    }

    public static void forgotPassword() {
        String user = JOptionPane.showInputDialog(Layout.root, "Username:", "Forgot password", JOptionPane.QUESTION_MESSAGE);
        if (user == null || user.isBlank()) return;
        try {
            Accounts.forgotPassword(user.trim());
            UI.Utils.Auth.loginStatus.setText("Password reset email sent (if user exists).");
        } catch (Exception ex) {
            UI.Utils.Auth.loginStatus.setText("Could not send reset email: " + ex.getMessage());
        }
    }

    private static void refreshContacts() {
        EmailEditor.contactModel.clear();
        if (UI.Utils.Auth.currentUser == null) return;
        try {
            List<Contacts.Contact> contacts = Contacts.getContacts(UI.Utils.Auth.currentUser);
            for (Contacts.Contact c : contacts) {
                EmailEditor.contactModel.addElement(c);
            }
        } catch (SQLException e) {
            EmailEditor.mainStatus.setText("Could not load contacts: " + e.getMessage());
        }
    }

  private static void clearCompose() {
        if (EmailEditor.toField != null) {
            EmailEditor.toField.setText("");
        }
        if (EmailEditor.subjectField != null) {
            EmailEditor.subjectField.setText("");
        }
        if (EmailEditor.bodyArea != null) {
            EmailEditor.bodyArea.setText("");
        }
        if (EmailEditor.contactList != null) {
            EmailEditor.contactList.clearSelection();
        }
        
    }
}
