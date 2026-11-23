package email.automation.ui.panels;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import email.automation.ui.components.Components;
import email.automation.ui.utils.Auth;
import email.automation.ui.utils.Page.Layout;

public class RegisterPanel {
    public static JPanel buildRegisterPanel() {
        JPanel page = Components.centeredCardPage();
        JPanel card = Components.formCard();
        Auth.regUser = Components.field();
        Auth.regPass = Components.passwordField();
        Auth.regPass2 = Components.passwordField();
        Auth.regEmail = Components.field();

        JButton createBtn = Components.primaryButton("Create account");
        createBtn.addActionListener(e -> email.automation.backend.Auth.doRegister());
        JButton back = Components.linkButton("Back to sign in");
        back.addActionListener(e -> Layout.cards.show(Layout.root, "login"));

        Components.layoutForm(
                card,
                "Create account",
                new String[] { "Username", "Password", "Repeat password", "Email" },
                new JComponent[] { Auth.regUser, Auth.regPass, Auth.regPass2, Auth.regEmail },
                createBtn,
                back,
                null);
        page.add(card);
        return page;
    }
}
