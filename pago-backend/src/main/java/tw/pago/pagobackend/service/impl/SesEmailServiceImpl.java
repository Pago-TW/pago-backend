package tw.pago.pagobackend.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;

import tw.pago.pagobackend.dto.EmailRequestDto;
import tw.pago.pagobackend.service.SesEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SesEmailServiceImpl implements SesEmailService {

    @Autowired
    private AmazonSimpleEmailService amazonSimpleEmailService;

    private static final String FROM_EMAIL = "pagonotifications@gmail.com";
    private static final String FROM_NAME = "Pago";

    @Override
    public void sendEmail(EmailRequestDto emailRequestDto) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(emailRequestDto.getTo()))
                .withMessage(new Message().withBody(new Body().withText(new Content().withCharset("UTF-8")
                        .withData(emailRequestDto.getBody())))
                        .withSubject(new Content().withCharset("UTF-8").withData(emailRequestDto.getSubject())))
                .withSource("\"" + FROM_NAME + "\" <" + FROM_EMAIL + ">");

        amazonSimpleEmailService.sendEmail(request);
    }
}
