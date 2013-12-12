package ca.ulaval.glo4003.email;

import ca.ulaval.glo4003.domain.EmailService;
import com.google.inject.Inject;
import com.typesafe.config.ConfigFactory;

import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SMTPEmailService extends EmailService {

    private String smtpHost;
    private String smtpPort;

    @Inject
    public SMTPEmailService(@Named("SmtpHost") String smtpHost, @Named("SmtpPort") String smtpPort) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    @Override
    public void sendEmail(String to, String from, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

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
