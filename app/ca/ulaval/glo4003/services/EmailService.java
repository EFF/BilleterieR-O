package ca.ulaval.glo4003.services;

public interface EmailService {

    void sendEmail(String to, String from, String message);

}
