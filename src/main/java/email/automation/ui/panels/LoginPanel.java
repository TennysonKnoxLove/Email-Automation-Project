package email.automation.ui.panels;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import email.automation.ui.components.Components;
import email.automation.ui.utils.Auth;
import email.automation.ui.utils.Page.Layout;

public class LoginPanel {
    public static JPanel buildLoginPanel() {
        JPanel page = Components.centeredCardPage();
        JPanel card = Components.formCard();
        Auth.loginUser = Components.field();
        Auth.loginPass = Components.passwordField();

        JButton loginBtn = Components.primaryButton("Log in");
        loginBtn.addActionListener(e -> email.automation.backend.Auth.doLogin());

        JButton createLink = Components.linkButton("Create account");
        createLink.addActionListener(e -> Layout.cards.show(Layout.root, "register"));
        JButton forgotUser = Components.linkButton("Forgot username");
        forgotUser.addActionListener(e -> email.automation.backend.Auth.forgotUsername());
        JButton forgotPass = Components.linkButton("Forgot password");
        forgotPass.addActionListener(e -> email.automation.backend.Auth.forgotPassword());

        Components.layoutForm(
                card,
                "Sign in",
                new String[] { "Username", "Password" },
                new JComponent[] { Auth.loginUser, Auth.loginPass },
                loginBtn,
                Components.linkColumn(createLink, forgotUser, forgotPass),
                null);
        page.add(card);
        return page;
    }
}
