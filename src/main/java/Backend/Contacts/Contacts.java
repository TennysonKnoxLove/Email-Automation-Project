package Backend.Contacts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Backend.Utils.DB;

public class Contacts {

    public static class Contact {
        public final int id;
        public final String firstName;
        public final String lastName;
        public final String email;

        public Contact(int id, String firstName, String lastName, String email) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        @Override
        public String toString() {
            return firstName + " " + lastName;
        }
    }

    public static List<Contact> getContacts(String owner) throws SQLException {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT id, firstname, lastname, email FROM contacts WHERE owner = ? ORDER BY lastname, firstname";
        try (Connection connection = DB.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, owner);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Contact(
                            rs.getInt("id"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getString("email")));
                }
            }
        }
        return list;
    }

    public static boolean addContact(String owner, String firstName, String lastName, String email) throws SQLException {
        if (owner == null || firstName == null || lastName == null || email == null
                || firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
            return false;
        }
        firstName = firstName.trim();
        lastName = lastName.trim();
        email = email.trim();

        String sql = "INSERT INTO contacts (firstname, lastname, email, owner) VALUES (?, ?, ?, ?)";
        try (Connection connection = DB.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, owner);
            ps.executeUpdate();
            return true;
        }
    }

    public static boolean removeContact(int id, String owner) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ? AND owner = ?";
        try (Connection connection = DB.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, owner);
            return ps.executeUpdate() > 0;
        }
    }
}
