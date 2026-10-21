package Backend.Utils;

import java.io.IOException;

public class Env {
    public static String requiredEnv(String name) throws IOException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IOException("Set " + name + " in the environment.");
        }
        return value;
    }
}
