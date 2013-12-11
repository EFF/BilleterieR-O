package ca.ulaval.glo4003.unittests.email;

import ca.ulaval.glo4003.domain.EmailService;
import ca.ulaval.glo4003.email.SMTPEmailService;
import org.junit.Test;

public class SMTPEmailServiceTest {

    private final String EMAIL_FROM = "from@arcbees.com";
    private final String EMAIL_TO = "to@arcbees.com";
    private final String EMAIL_MESSAGE = "I am a bee.";

    @Test
    public void doesNotThrow() {
        EmailService emailSvc = new SMTPEmailService("localhost", "666");

        emailSvc.sendEmail(EMAIL_TO, EMAIL_FROM, EMAIL_MESSAGE);
    }
}
