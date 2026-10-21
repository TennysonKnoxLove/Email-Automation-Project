import java.sql.SQLException;

import Schema.Initialize;
import UI.AppUI;

public class Main {

    // Tables are defined in src/main/resources/Schema/ and created when the program starts.
    // Email list (first/last name) shown on the left — click a contact to fill the recipient.

    public static void main(String[] args) {
        try {
            Initialize.apply();
        } catch (SQLException e) {
            System.err.println("Could not create database tables: " + e.getMessage());
            e.printStackTrace();
        }
        AppUI.launch();
    }
}
