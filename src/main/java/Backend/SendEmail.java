package Backend;

import javax.swing.SwingWorker;

import Backend.Utils.Email;
import UI.Utils.EmailEditor;

public class SendEmail {
    public static void sendEmail() {
        String to = EmailEditor.toField.getText().trim();
        String subject = EmailEditor.subjectField.getText().trim();
        String body = EmailEditor.bodyArea.getText().trim();
        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            EmailEditor.mainStatus.setText("To, subject, and body are required.");
            return;
        }
        EmailEditor.mainStatus.setText("Sending…");
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
                    EmailEditor.mainStatus.setText("Email sent.");
                } else {
                    EmailEditor.mainStatus.setText("Send failed: " + error + " (set SMTP + ChatGPT keys)");
                }
            }
        }.execute();
    }
}