package Backend.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpCall {
    public static HttpURLConnection open(String url, String method, Map<String, String> headers) throws IOException, URISyntaxException {
        HttpURLConnection conn = (HttpURLConnection) new URI(url).toURL().openConnection();
        String verb = method.toUpperCase();
        conn.setRequestMethod(verb);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        if (verb.equals("POST") || verb.equals("PUT") || verb.equals("PATCH")) {
            conn.setDoOutput(true);
        }
        return conn;
    }
}
