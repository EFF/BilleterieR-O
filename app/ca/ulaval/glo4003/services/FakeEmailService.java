package ca.ulaval.glo4003.services;

public class FakeEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String from, String message) {
        // It's a fake service
    }
}
