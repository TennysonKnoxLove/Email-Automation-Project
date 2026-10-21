package Backend.Utils;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.net.URISyntaxException;

public class Email {
MimeMessage mimeMessage = null;

public static void sendUser(String[] emailRecipients, String emailSubject, String emailContent) throws AddressException, MessagingException, IOException, InterruptedException, URISyntaxException {
Server.setupServerProperties();
Email mail = new Email();
mail.draftEmail(emailRecipients, emailSubject, ChatGPT.ask(emailContent));
mail.sendEmail();

}
    private void sendEmail() throws NoSuchProviderException, MessagingException, IOException  {
        String fromUser = Env.requiredEnv("SMTP_FROM");
        String fromUserPassword = Env.requiredEnv("SMTP_PASSWORD");
        String emailHost = Env.requiredEnv("SMTP_HOST");

        Transport transport = Server.newSession.getTransport(
            "smtp"); 
        transport.connect(emailHost, fromUser, fromUserPassword); 
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
    }

    private MimeMessage draftEmail(String[] emailRec, String emailSub, String emailCont) throws MessagingException, AddressException, IOException  {
        mimeMessage = new MimeMessage(Server.newSession);
        for(int i = 0; i< emailRec.length; i++) {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRec[i]));
        }

        mimeMessage.setSubject(emailSub);
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailCont, "text/html");
        MimeMultipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(bodyPart);
        mimeMessage.setContent(multiPart);
        System.out.println("Email sent.");
        return mimeMessage;

    }
}