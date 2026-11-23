package email.automation.backend;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import email.automation.backend.accounts.Accounts;
import email.automation.backend.actions.ContactActions;
import email.automation.backend.contacts.Contacts;
import email.automation.ui.utils.EmailEditor;
import email.automation.ui.utils.Notice;
import email.automation.ui.utils.Page.Layout;

public class Auth {
    public static void doLogin() {
        String user = email.automation.ui.utils.Auth.loginUser.getText().trim();
        String pass = new String(email.automation.ui.utils.Auth.loginPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            Notice.show("Enter username and password.");
            return;
        }
        try {
            Accounts.LoginResult result = Accounts.attemptLogin(user, pass);
            if (result == Accounts.LoginResult.UNVERIFIED) {
                if (!promptVerification(user)) {
                    return;
                }
                Accounts.markOnline(user);
                result = Accounts.LoginResult.SUCCESS;
            }
            if (result == Accounts.LoginResult.SUCCESS) {
                openSession(user);
            } else {
                Notice.show("Login failed. Wrong username or password.");
            }
        } catch (Exception ex) {
            Notice.show("Login error: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
            ex.printStackTrace();
        }
    }

    public static void doRegister() {
        String user = email.automation.ui.utils.Auth.regUser.getText().trim();
        String pass = new String(email.automation.ui.utils.Auth.regPass.getPassword());
        String pass2 = new String(email.automation.ui.utils.Auth.regPass2.getPassword());
        String address = email.automation.ui.utils.Auth.regEmail.getText().trim();
        try {
            String result = Accounts.createAccount(user, pass, pass2, address);
            if ("ok".equals(result) || "email-failed".equals(result)) {
                Layout.cards.show(Layout.root, "login");
                if ("ok".equals(result)) {
                    Notice.show("Account created. Check your email for a verification code, then log in.");
                } else {
                    Notice.show("Account created, but the verification email could not be sent. Log in and choose Resend code.");
                }
            } else {
                Notice.show(result);
            }
        } catch (Exception ex) {
            Notice.show("Could not create account: " + ex.getMessage());
        }
    }

    public static void doLogout() {
        String message = null;
        try {
            if (email.automation.ui.utils.Auth.currentUser != null) {
                Accounts.logOut(email.automation.ui.utils.Auth.currentUser);
            }
        } catch (Exception ex) {
            message = "Logged out. Cannot connect to MySQL.";
        }
        email.automation.ui.utils.Auth.currentUser = null;
        email.automation.ui.utils.Auth.loginPass.setText("");
        Layout.cards.show(Layout.root, "login");
        if (message != null) {
            Notice.show(message);
        }
    }

    public static void forgotUsername() {
        String email = JOptionPane.showInputDialog(Layout.root, "Email on the account:", "Forgot username", JOptionPane.QUESTION_MESSAGE);
        if (email == null || email.isBlank()) return;
        try {
            Accounts.retrieveUsername(email.trim());
            Notice.show("If that email exists, username was sent.");
        } catch (Exception ex) {
            Notice.show("Could not send username: " + ex.getMessage());
        }
    }

    public static void forgotPassword() {
        String user = JOptionPane.showInputDialog(Layout.root, "Username:", "Forgot password", JOptionPane.QUESTION_MESSAGE);
        if (user == null || user.isBlank()) return;
        user = user.trim();
        try {
            Accounts.sendPasswordCode(user);
        } catch (Exception ex) {
            Notice.show("Could not send verification code: " + ex.getMessage());
            return;
        }
        Notice.show("Verification code sent.");

        JTextField codeField = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Verification code"));
        form.add(codeField);
        form.add(new JLabel("New password"));
        form.add(pass);
        form.add(new JLabel("Repeat password"));
        form.add(pass2);
        int choice = JOptionPane.showConfirmDialog(Layout.root, form, "Reset password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        String code = codeField.getText().trim();
        String newPass = new String(pass.getPassword());
        String again = new String(pass2.getPassword());
        if (code.isEmpty() || newPass.isEmpty()) {
            Notice.show("Enter the verification code and a new password.");
            return;
        }
        if (!newPass.equals(again)) {
            Notice.show("Password does not match!");
            return;
        }
        try {
            Accounts.resetPassword(user, code, newPass);
            Notice.show("Password updated.");
        } catch (Exception ex) {
            Notice.show(ex.getMessage() == null ? "Could not reset password." : ex.getMessage());
        }
    }

    private static void openSession(String user) {
        email.automation.ui.utils.Auth.currentUser = user;
        EmailEditor.welcomeLabel.setText("Welcome, " + user);
        ContactActions.refreshContacts();
        clearCompose();
        Layout.cards.show(Layout.root, "main");
    }

    private static boolean promptVerification(String user) {
        JTextField codeField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Enter the verification code sent to your email."));
        form.add(codeField);
        Object[] options = { "Verify", "Resend code", "Cancel" };
        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    Layout.root,
                    form,
                    "Verify account",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (choice == 1) {
                try {
                    Accounts.sendVerificationCode(user);
                    Notice.show("Verification code sent.");
                } catch (Exception ex) {
                    Notice.show("Could not send verification code: " + ex.getMessage());
                }
                continue;
            }
            if (choice != 0) {
                return false;
            }
            try {
                Accounts.confirmVerification(user, codeField.getText());
                return true;
            } catch (Exception ex) {
                Notice.show(ex.getMessage() == null ? "Could not verify account." : ex.getMessage());
            }
        }
    }

    private static void refreshContacts() {
        EmailEditor.contactModel.clear();
        if (email.automation.ui.utils.Auth.currentUser == null) return;
        try {
            List<Contacts.Contact> contacts = Contacts.getContacts(email.automation.ui.utils.Auth.currentUser);
            for (Contacts.Contact c : contacts) {
                EmailEditor.contactModel.addElement(c);
            }
        } catch (SQLException e) {
            Notice.show("Could not load contacts: " + e.getMessage());
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
