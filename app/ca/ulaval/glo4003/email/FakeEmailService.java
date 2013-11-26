package ca.ulaval.glo4003.email;

import ca.ulaval.glo4003.domain.EmailService;

public class FakeEmailService extends EmailService {
    @Override
    public void sendEmail(String to, String from, String message) {
        // It's a fake service
    }
}
