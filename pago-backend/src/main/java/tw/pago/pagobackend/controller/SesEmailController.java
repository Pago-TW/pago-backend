package tw.pago.pagobackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.pago.pagobackend.service.SesEmailService;

@RestController
public class SesEmailController {

    @Autowired
    private SesEmailService sesEmailService;

    @PostMapping("/send-test-email")
    public ResponseEntity<String> sendTestEmail() {
        System.out.println("Sending test email...");
        String from = "pagonotifications@gmail.com";
        String to = ""; // Enter your test email address here
        String subject = "Test email";
        String body = "Hello, this is a test email.";
        
        sesEmailService.sendEmail(from, to, subject, body);
        return ResponseEntity.ok("Test email sent.");
    }
}
