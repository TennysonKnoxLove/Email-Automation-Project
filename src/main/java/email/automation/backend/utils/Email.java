package email.automation.backend.utils;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.net.URISyntaxException;

public class Email {
MimeMessage mimeMessage = null;

public static void sendUser(String[] emailRecipients, String emailSubject, String emailContent) throws AddressException, MessagingException, IOException, InterruptedException, URISyntaxException {
    deliver(emailRecipients, emailSubject, ChatGPT.ask(emailContent));
}

public static void sendFixed(String[] emailRecipients, String emailSubject, String emailContent) throws AddressException, MessagingException, IOException {
    deliver(emailRecipients, emailSubject, emailContent);
}

private static void deliver(String[] emailRecipients, String emailSubject, String emailContent) throws AddressException, MessagingException, IOException {
    Server.setupServerProperties();
    Email mail = new Email();
    mail.draftEmail(emailRecipients, emailSubject, emailContent);
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
        System.out.println("Email sent.");
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
        return mimeMessage;

    }
}