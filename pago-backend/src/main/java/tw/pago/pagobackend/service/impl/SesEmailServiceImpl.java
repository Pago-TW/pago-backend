package tw.pago.pagobackend.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;

import tw.pago.pagobackend.service.SesEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SesEmailServiceImpl implements SesEmailService {

    @Autowired
    private AmazonSimpleEmailService amazonSimpleEmailService;

    @Override
    public void sendEmail(String from, String to, String subject, String body) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(new Message().withBody(new Body().withText(new Content().withCharset("UTF-8")
                        .withData(body)))
                        .withSubject(new Content().withCharset("UTF-8").withData(subject)))
                .withSource(from);

        amazonSimpleEmailService.sendEmail(request);
    }
}
