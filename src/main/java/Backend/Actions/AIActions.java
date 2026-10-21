package Backend.Actions;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import Backend.Utils.ChatGPT;
import UI.Utils.EmailEditor;

public class AIActions {
    public static void optimizeBody() {
            String body = EmailEditor.bodyArea.getText().trim();
            if (body.isEmpty()) {
                EmailEditor.mainStatus.setText("Write a body to optimize.");
                return;
            }
            EmailEditor.mainStatus.setText("Optimizing with AI…");
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return ChatGPT.ask(body);
                }

                @Override
                protected void done() {
                    try {
                        EmailEditor.bodyArea.setText(get());
                        EmailEditor.mainStatus.setText("Body updated with AI optimization.");
                    } catch (Exception ex) {
                        EmailEditor.mainStatus.setText("AI optimize failed: " + ex.getMessage());
                    }
                }
            }.execute();
        }

    public static void askAi() {
            String question = JOptionPane.showInputDialog(null, "Ask ChatGPT:", "Ask AI", JOptionPane.QUESTION_MESSAGE);
            if (question == null || question.isBlank()) return;
            EmailEditor.mainStatus.setText("Asking AI…");
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
                        EmailEditor.mainStatus.setText("AI answer appended to body.");
                    } catch (Exception ex) {
                        EmailEditor.mainStatus.setText("Ask AI failed: " + ex.getMessage());
                    }
                }
            }.execute();
        }
}