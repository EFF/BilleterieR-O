package ca.ulaval.glo4003.services;

public interface EmailService {

    void SendEmail(String to, String from, String message);

}
