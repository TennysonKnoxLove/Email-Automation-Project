package email.automation.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import email.automation.backend.actions.AIActions;
import email.automation.backend.actions.ContactActions;
import email.automation.backend.contacts.Contacts;
import email.automation.backend.SendEmail;
import email.automation.ui.components.Components;
import email.automation.ui.utils.Contact;
import email.automation.ui.utils.EmailEditor;
import email.automation.ui.utils.Page.Theme;

public class MainPanel {
    public static JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.BG);

        JPanel sidebar = new JPanel(new BorderLayout(0, 8));
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(16, 14, 16, 14));

        JLabel listTitle = Components.sectionTitle("Email list", 16f, Theme.SIDEBAR_TEXT);
        sidebar.add(listTitle, BorderLayout.NORTH);

        if (EmailEditor.contactModel == null) {
            EmailEditor.contactModel = new DefaultListModel<>();
        }
        EmailEditor.contactList = Components.contactList(EmailEditor.contactModel);
        EmailEditor.contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Contacts.Contact c = EmailEditor.contactList.getSelectedValue();
                if (c != null) {
                    EmailEditor.toField.setText(c.email);
                }
            }
        });
        sidebar.add(new JScrollPane(EmailEditor.contactList), BorderLayout.CENTER);

        JPanel addBox = new JPanel(new GridBagLayout());
        addBox.setOpaque(false);
        Contact.firstNameField = Components.darkField();
        Contact.lastNameField = Components.darkField();
        Contact.contactEmailField = Components.darkField();
        GridBagConstraints side = new GridBagConstraints();
        side.gridx = 0;
        side.gridy = 0;
        side.weightx = 1;
        side.fill = GridBagConstraints.HORIZONTAL;
        side.anchor = GridBagConstraints.WEST;
        side.insets = new Insets(0, 0, 6, 0);
        addBox.add(Components.sidebarLabeled("First name", Contact.firstNameField), side);
        side.gridy++;
        addBox.add(Components.sidebarLabeled("Last name", Contact.lastNameField), side);
        side.gridy++;
        side.insets = new Insets(0, 0, 8, 0);
        addBox.add(Components.sidebarLabeled("Email", Contact.contactEmailField), side);

        JPanel contactButtons = new JPanel(new GridLayout(1, 2, 6, 0));
        contactButtons.setOpaque(false);
        JButton addContactBtn = Components.secondaryButton("Add");
        addContactBtn.addActionListener(e -> ContactActions.addContact());
        JButton removeContactBtn = Components.secondaryButton("Remove");
        removeContactBtn.addActionListener(e -> ContactActions.removeContact());
        contactButtons.add(addContactBtn);
        contactButtons.add(removeContactBtn);
        side.gridy++;
        side.insets = new Insets(0, 0, 0, 0);
        addBox.add(contactButtons, side);
        sidebar.add(addBox, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Theme.BG);
        content.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        EmailEditor.welcomeLabel = Components.sectionTitle("Welcome", 18f);
        topBar.add(EmailEditor.welcomeLabel, BorderLayout.WEST);
        JButton logoutBtn = Components.secondaryButton("Log out");
        logoutBtn.addActionListener(e -> email.automation.backend.Auth.doLogout());
        topBar.add(logoutBtn, BorderLayout.EAST);
        content.add(topBar, BorderLayout.NORTH);

        JPanel compose = new JPanel(new BorderLayout(0, 10));
        compose.setBackground(Theme.PANEL);
        compose.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel composeTitle = Components.sectionTitle("Compose email", 16f);
        compose.add(composeTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 8, 0);

        EmailEditor.toField = Components.field();
        EmailEditor.subjectField = Components.field();
        EmailEditor.bodyArea = Components.bodyArea();

        fields.add(Components.labeledBlock("To", EmailEditor.toField), gbc);
        gbc.gridy++;
        fields.add(Components.labeledBlock("Subject", EmailEditor.subjectField), gbc);
        gbc.gridy++;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        fields.add(Components.labeledBlock("Body", new JScrollPane(EmailEditor.bodyArea)), gbc);

        compose.add(fields, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton optimizeBtn = Components.secondaryButton("AI optimize");
        optimizeBtn.addActionListener(e -> AIActions.optimizeBody());
        JButton askBtn = Components.secondaryButton("Ask AI");
        askBtn.addActionListener(e -> AIActions.askAi());
        JButton sendBtn = Components.primaryButton("Send email");
        sendBtn.addActionListener(e -> SendEmail.sendEmail());
        actions.add(optimizeBtn);
        actions.add(askBtn);
        actions.add(sendBtn);
        bottom.add(actions, BorderLayout.NORTH);
        compose.add(bottom, BorderLayout.SOUTH);

        content.add(compose, BorderLayout.CENTER);
        main.add(sidebar, BorderLayout.WEST);
        main.add(content, BorderLayout.CENTER);
        return main;
    }
}
