package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.services.FakeEmailService;
import org.junit.Test;

public class FakeEmailServiceTest {

    private final String EMAIL_FROM = "from@arcbees.com";
    private final String EMAIL_TO = "to@arcbees.com";
    private final String EMAIL_MESSAGE = "I am a bee.";

    @Test
    public void doesNotThrow() {
        FakeEmailService emailService = new FakeEmailService();

        emailService.sendEmail(EMAIL_TO, EMAIL_FROM, EMAIL_MESSAGE);
    }
}