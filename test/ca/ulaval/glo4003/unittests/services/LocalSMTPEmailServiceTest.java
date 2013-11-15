package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.services.EmailService;
import ca.ulaval.glo4003.services.LocalSMTPEmailService;
import org.junit.Test;

public class LocalSMTPEmailServiceTest {

    private final String EMAIL_FROM = "from@arcbees.com";
    private final String EMAIL_TO = "to@arcbees.com";
    private final String EMAIL_MESSAGE = "I am a bee.";

    @Test
    public void doesNotThrow() {
        EmailService emailSvc = new LocalSMTPEmailService();

        emailSvc.sendEmail(EMAIL_TO, EMAIL_FROM, EMAIL_MESSAGE);
    }
}
