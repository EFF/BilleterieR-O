package ca.ulaval.glo4003.domain;

public abstract class EmailService {

    private static final String NOREPLY_EMAIL = "noreply@glo4003.com";

    public abstract void sendEmail(String to, String from, String message);

    public void sendSystemEmail(String to, String message) {
        sendEmail(to, NOREPLY_EMAIL, message);
    }

}
