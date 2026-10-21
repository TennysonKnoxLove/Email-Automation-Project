package UI.Panels;

import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import UI.Components.Components;
import UI.Utils.Auth;
import UI.Utils.Page.Layout;

public class LoginPanel {
    public static JPanel buildLoginPanel() {
        JPanel page = Components.centeredCardPage();
        JPanel card = Components.formCard();
        JLabel heading = Components.heading("Sign in");
        Auth.loginUser = Components.field();
        Auth.loginPass = Components.passwordField();
        Auth.loginStatus = Components.statusLabel();
        JButton loginBtn = Components.primaryButton("Log in");

        loginBtn.addActionListener(e -> Backend.Auth.doLogin());

        JButton createLink = Components.linkButton("Create account");
        createLink.addActionListener(e -> {
            if (Auth.regStatus != null) {
                Auth.regStatus.setText(" ");
            }
            Layout.cards.show(Layout.root, "register");
        });

        JButton forgotUser = Components.linkButton("Forgot username");
        forgotUser.addActionListener(e -> Backend.Auth.forgotUsername());
        JButton forgotPass = Components.linkButton("Forgot password");
        forgotPass.addActionListener(e -> Backend.Auth.forgotPassword());

        GroupLayout layout = new GroupLayout(card);
        card.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel userLbl = new JLabel("Username");
        JLabel passLbl = new JLabel("Password");
        JPanel links = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        links.setOpaque(false);
        links.add(createLink);
        links.add(forgotUser);
        links.add(forgotPass);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(heading)
                .addComponent(userLbl, GroupLayout.Alignment.LEADING)
                .addComponent(Auth.loginUser)
                .addComponent(passLbl, GroupLayout.Alignment.LEADING)
                .addComponent(Auth.loginPass)
                .addComponent(loginBtn, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
                .addComponent(links)
                .addComponent(Auth.loginStatus));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(heading)
                .addGap(12)
                .addComponent(userLbl)
                .addComponent(Auth.loginUser, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addComponent(passLbl)
                .addComponent(Auth.loginPass, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(loginBtn, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                .addComponent(links)
                .addComponent(Auth.loginStatus));

        page.add(card);
        return page;
    }
}
