package tw.pago.pagobackend.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.EmailRequestDto;
import tw.pago.pagobackend.exception.TooManyRequestsException;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.DailyCountService;
import tw.pago.pagobackend.service.SesEmailService;

@Service
@AllArgsConstructor
public class SesEmailServiceImpl implements SesEmailService {
    private static final String FROM_EMAIL = "pagonotifications@gmail.com";
    private static final String FROM_NAME = "Pago";

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final ResourceLoader resourceLoader;
    private final DailyCountService dailyCountService;
    private final UserDao userDao;


    @Override
    public void sendEmail(EmailRequestDto emailRequestDto) {
        try {
            String recipientEmail = emailRequestDto.getTo();
            String recipientUserId = emailRequestDto.getRecipientUserId();

            if (recipientUserId == null) {
                User recipient = userDao.getUserByEmail(recipientEmail);
                recipientUserId = recipient.getUserId();
            }

            if (dailyCountService.isReachedDailyEmailLimit(recipientUserId)) {
                throw new TooManyRequestsException("You have reached the daily Email limit. Please try again tomorrow");
            }

            Resource resource = resourceLoader.getResource("classpath:templates/emailTemplate.html");
            String emailTemplate = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            String contentTitle = emailRequestDto.getContentTitle();
            String recipientName = emailRequestDto.getRecipientName();
            String emailBody = emailRequestDto.getBody();
            String htmlBody = emailTemplate
                .replace("{{content_title}}", contentTitle)
                .replace("{{recipient_name}}", recipientName)
                .replace("{{email_body}}", emailBody);


            SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(emailRequestDto.getTo()))
                .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8")
                        .withData(htmlBody)))
                    .withSubject(new Content().withCharset("UTF-8").withData(emailRequestDto.getSubject())))
                .withSource("\"" + FROM_NAME + "\" <" + FROM_EMAIL + ">");

            amazonSimpleEmailService.sendEmail(request);
            dailyCountService.incrementEmailCount(recipientUserId);
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template", e);
        }
    }
}
