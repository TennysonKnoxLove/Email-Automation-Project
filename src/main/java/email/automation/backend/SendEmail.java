package email.automation.backend;

import javax.swing.SwingWorker;

import email.automation.backend.utils.Email;
import email.automation.ui.utils.EmailEditor;
import email.automation.ui.utils.Notice;

public class SendEmail {
    public static void sendEmail() {
        String to = EmailEditor.toField.getText().trim();
        String subject = EmailEditor.subjectField.getText().trim();
        String body = EmailEditor.bodyArea.getText().trim();
        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            Notice.show("To, subject, and body are required.");
            return;
        }
        new SwingWorker<Void, Void>() {
            private String error;

            @Override
            protected Void doInBackground() {
                try {
                    Email.sendUser(new String[]{to}, subject, body);
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (error == null) {
                    Notice.show("Email sent.");
                } else {
                    Notice.show("Send failed: " + error + " (set SMTP + ChatGPT keys)");
                }
            }
        }.execute();
    }
}