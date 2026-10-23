package email.automation.backend.utils;

import java.io.*;
import java.net.*;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class ChatGPT {

    private static final String EMAIL_INSTRUCTION =
            "Don't add subject, or sender; and don't have any dialogue outside of the requested answer. Thank you.";

    public static String ask(String message) throws IOException, InterruptedException, URISyntaxException {
        return ask(message, null);
    }

    public static String ask(String message, String instruction) throws IOException, InterruptedException, URISyntaxException {
        if (instruction == null || instruction.isBlank()) {
            instruction = EMAIL_INSTRUCTION;
        }
        HttpURLConnection conn = HttpCall.open(
                "https://api.openai.com/v1/chat/completions",
                "POST",
                Map.of(
                        "Authorization", "Bearer " + Env.requiredEnv("OPENAI_API_KEY"),
                        "Content-Type", "application/json"));

        String prompt = instruction + " User Request: " + message;
        String jsonInputString = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            conn.disconnect();
            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray choices = jsonObject.getAsJsonArray("choices");
            JsonObject gptMessage = choices.get(0).getAsJsonObject().getAsJsonObject("message");
            return gptMessage.get("content").getAsString();
        }
    }
}
