package ca.ulaval.glo4003.email;

import ca.ulaval.glo4003.domain.EmailService;
import com.typesafe.config.ConfigFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SMTPEmailService extends EmailService {

    @Override
    public void sendEmail(String to, String from, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.host", ConfigFactory.load().getString("smtp.host"));
        props.put("mail.smtp.port", ConfigFactory.load().getString("smtp.port"));

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
