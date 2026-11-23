package email.automation.backend.actions;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import email.automation.backend.utils.ChatGPT;
import email.automation.ui.utils.EmailEditor;
import email.automation.ui.utils.Notice;

public class AIActions {
    public static void optimizeBody() {
            String body = EmailEditor.bodyArea.getText().trim();
            if (body.isEmpty()) {
                Notice.show("Write a body to optimize.");
                return;
            }
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return ChatGPT.ask(body);
                }

                @Override
                protected void done() {
                    try {
                        EmailEditor.bodyArea.setText(get());
                        Notice.show("Body updated with AI optimization.");
                    } catch (Exception ex) {
                        Notice.show(ex);
                    }
                }
            }.execute();
        }

    public static void askAi() {
            String question = JOptionPane.showInputDialog(null, "Ask ChatGPT:", "Ask AI", JOptionPane.QUESTION_MESSAGE);
            if (question == null || question.isBlank()) return;
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return ChatGPT.ask(
                            "Only return answer to the question, do not add any other than the exact answer.",
                            question.trim());
                }

                @Override
                protected void done() {
                    try {
                        String answer = get();
                        EmailEditor.bodyArea.append((EmailEditor.bodyArea.getText().isEmpty() ? "" : "\n\n") + answer);
                        Notice.show("AI answer appended to body.");
                    } catch (Exception ex) {
                        Notice.show(ex);
                    }
                }
            }.execute();
        }
}