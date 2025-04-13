package application;

import javax.mail.Session;  // ✅ This is the correct Session




import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.PasswordAuthentication;


public class EmailNotifier {
    public static void sendReminder(String recipientEmail, String subject, String body) {

        final String senderEmail = "syedas.btech23@rvu.edu.in";  // Replace with your Gmail
        final String password = "ocrvowonlownxwzi";         // Use App Password (not Gmail password)

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, password);
                    }
                }
        );

        try {
            Message message = new MimeMessage(session);
            ((MimeMessage) message).setFrom(new InternetAddress(senderEmail));
            ((MimeMessage) message).setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Email sent to " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}