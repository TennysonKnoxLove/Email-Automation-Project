package UI.Utils;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import Backend.Contacts.Contacts;

public class EmailEditor {
    public static DefaultListModel<Contacts.Contact> contactModel;
    public static JList<Contacts.Contact> contactList;
    public static JTextField toField;
    public static JTextField subjectField;
    public static JTextArea bodyArea;
    public static JLabel mainStatus;
    public static JLabel welcomeLabel;
}
