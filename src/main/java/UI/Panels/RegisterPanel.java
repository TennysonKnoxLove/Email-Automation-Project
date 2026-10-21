package UI.Panels;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import UI.Components.Components;
import UI.Utils.Auth;
import UI.Utils.Page.Layout;

public class RegisterPanel {
    public static JPanel buildRegisterPanel() {
        JPanel page = Components.centeredCardPage();
        JPanel card = Components.formCard();

        JLabel heading = Components.heading("Create account");
        Auth.regUser = Components.field();
        Auth.regPass = Components.passwordField();
        Auth.regPass2 = Components.passwordField();
        Auth.regEmail = Components.field();
        Auth.regStatus = Components.statusLabel();

        JButton createBtn = Components.primaryButton("Create account");
        createBtn.addActionListener(e -> Backend.Auth.doRegister());
        JButton back = Components.linkButton("Back to sign in");
        back.addActionListener(e -> {
            Auth.loginStatus.setText(" ");
            Layout.cards.show(Layout.root, "login");
        });

        GroupLayout layout = new GroupLayout(card);
        card.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel u = new JLabel("Username");
        JLabel p = new JLabel("Password");
        JLabel p2 = new JLabel("Repeat password");
        JLabel em = new JLabel("Email");

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(heading)
                .addComponent(u, GroupLayout.Alignment.LEADING)
                .addComponent(Auth.regUser)
                .addComponent(p, GroupLayout.Alignment.LEADING)
                .addComponent(Auth.regPass)
                .addComponent(p2, GroupLayout.Alignment.LEADING)
                .addComponent(Auth.regPass2)
                .addComponent(em, GroupLayout.Alignment.LEADING)
                .addComponent(Auth.regEmail)
                .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
                .addComponent(back)
                .addComponent(Auth.regStatus));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(heading)
                .addGap(8)
                .addComponent(u)
                .addComponent(Auth.regUser, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addComponent(p)
                .addComponent(Auth.regPass, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addComponent(p2)
                .addComponent(Auth.regPass2, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addComponent(em)
                .addComponent(Auth.regEmail, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                .addComponent(back)
                .addComponent(Auth.regStatus));

        page.add(card);
        return page;
    }
}
