package email.automation.backend.actions;

import java.sql.SQLException;
import java.util.List;

import email.automation.backend.contacts.Contacts;
import email.automation.ui.utils.Auth;
import email.automation.ui.utils.Contact;
import email.automation.ui.utils.EmailEditor;
import email.automation.ui.utils.Notice;

public class ContactActions {
    public static void refreshContacts() {
        EmailEditor.contactModel.clear();
        if (Auth.currentUser == null) return;
        try {
            List<Contacts.Contact> contacts = Contacts.getContacts(Auth.currentUser);
            for (Contacts.Contact c : contacts) {
                EmailEditor.contactModel.addElement(c);
            }
        } catch (SQLException e) {
            Notice.show("Could not load contacts: " + e.getMessage());
        }
    }

    public static void addContact() {
        String first = Contact.firstNameField.getText().trim();
        String last = Contact.lastNameField.getText().trim();
        String email = Contact.contactEmailField.getText().trim();
        try {
            if (Contacts.addContact(Auth.currentUser, first, last, email)) {
                Contact.firstNameField.setText("");
                Contact.lastNameField.setText("");
                Contact.contactEmailField.setText("");
                refreshContacts();
            } else {
                Notice.show("Could not add contact — check fields.");
            }
        } catch (SQLException e) {
            Notice.show("Could not add contact: " + e.getMessage());
        }
    }

    public static void removeContact() {
        Contacts.Contact selected = EmailEditor.contactList.getSelectedValue();
        if (selected == null) {
            Notice.show("Select a contact to remove.");
            return;
        }
        try {
            if (Contacts.removeContact(selected.id, Auth.currentUser)) {
                refreshContacts();
                Notice.show("Contact removed.");
            } else {
                Notice.show("Could not remove contact.");
            }
        } catch (SQLException e) {
            Notice.show("Could not remove contact: " + e.getMessage());
        }
    }
}
