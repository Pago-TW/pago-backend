package tw.pago.pagobackend.service;

public interface SesEmailService {
    void sendEmail(String from, String to, String subject, String body);
}
