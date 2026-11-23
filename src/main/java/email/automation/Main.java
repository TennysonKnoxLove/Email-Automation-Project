package email.automation;

import java.sql.SQLException;

import email.automation.schema.Initialize;
import email.automation.ui.AppUI;

public class Main {

    // Tables are defined in src/main/resources/schema/ and created when the program starts.
    // Email list (first/last name) shown on the left — click a contact to fill the recipient.

    public static void main(String[] args) {
        System.setProperty("apple.awt.application.name", "Email Automation");
        try {
            Initialize.apply();
        } catch (SQLException e) {
            if (missingEnvironment(e)) {
                System.err.println(e.getMessage());
                System.err.println("Load .env in this terminal, then start the app again.");
            } else if (connectionRefused(e)) {
                System.err.println("Could not create database tables: MySQL refused the connection.");
                System.err.println("Start Docker Desktop, then run: docker compose up -d");
            } else {
                System.err.println("Could not create database tables: " + e.getMessage());
                e.printStackTrace();
            }
        }
        AppUI.launch();
    }

    private static boolean missingEnvironment(Throwable error) {
        String message = error.getMessage();
        return message != null && message.startsWith("Set ") && message.contains("in the environment.");
    }

    private static boolean connectionRefused(Throwable error) {
        while (error != null) {
            if (error instanceof java.net.ConnectException) {
                return true;
            }
            error = error.getCause();
        }
        return false;
    }
}
