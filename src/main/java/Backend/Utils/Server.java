package Backend.Utils;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;

public class Server {
    public static Session newSession;

    public static void setupServerProperties() throws MessagingException {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        newSession = Session.getDefaultInstance(properties, null);
    }
}