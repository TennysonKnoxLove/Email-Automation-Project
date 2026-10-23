package email.automation.ui.utils;

import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import email.automation.ui.utils.Page.Layout;

public class Notice {
    public static void show(String message) {
        JOptionPane.showMessageDialog(Layout.root, message);
    }

    public static void show(Throwable error) {
        Throwable shown = error;
        if (error instanceof ExecutionException && error.getCause() != null) {
            shown = error.getCause();
        }
        StringWriter trace = new StringWriter();
        shown.printStackTrace(new PrintWriter(trace));
        JTextArea area = new JTextArea(trace.toString(), 18, 72);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(Layout.root, new JScrollPane(area));
    }
}
