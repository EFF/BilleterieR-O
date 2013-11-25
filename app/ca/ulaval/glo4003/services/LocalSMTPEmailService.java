package ca.ulaval.glo4003.services;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class LocalSMTPEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String from, String message) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            msg.setSubject("GLO4003");
            msg.setText(message);
            Transport.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
