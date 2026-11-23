package email.automation.ui.utils;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import email.automation.backend.contacts.Contacts;

public class EmailEditor {
    public static DefaultListModel<Contacts.Contact> contactModel;
    public static JList<Contacts.Contact> contactList;
    public static JTextField toField;
    public static JTextField subjectField;
    public static JTextArea bodyArea;
    public static JLabel welcomeLabel;
}
