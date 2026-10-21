package Backend.Actions;

import java.sql.SQLException;
import java.util.List;

import Backend.Contacts.Contacts;
import UI.Utils.Auth;
import UI.Utils.Contact;
import UI.Utils.EmailEditor;

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
            EmailEditor.mainStatus.setText("Could not load contacts: " + e.getMessage());
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
                EmailEditor.mainStatus.setText("Contact added.");
            } else {
                EmailEditor.mainStatus.setText("Could not add contact — check fields.");
            }
        } catch (SQLException e) {
            EmailEditor.mainStatus.setText("Could not add contact: " + e.getMessage());
        }
    }

    public static void removeContact() {
        Contacts.Contact selected = EmailEditor.contactList.getSelectedValue();
        if (selected == null) {
            EmailEditor.mainStatus.setText("Select a contact to remove.");
            return;
        }
        try {
            if (Contacts.removeContact(selected.id, Auth.currentUser)) {
                refreshContacts();
                EmailEditor.mainStatus.setText("Contact removed.");
            } else {
                EmailEditor.mainStatus.setText("Could not remove contact.");
            }
        } catch (SQLException e) {
            EmailEditor.mainStatus.setText("Could not remove contact: " + e.getMessage());
        }
    }
}
