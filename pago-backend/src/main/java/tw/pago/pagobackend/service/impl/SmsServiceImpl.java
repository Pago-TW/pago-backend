package tw.pago.pagobackend.service.impl;

import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dto.SmsRequestDto;
import tw.pago.pagobackend.exception.TooManyRequestsException;
import tw.pago.pagobackend.service.DailyCountService;
import tw.pago.pagobackend.service.SmsService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Service
@AllArgsConstructor
public class SmsServiceImpl implements SmsService {
    
    private final AmazonSNS amazonSNS;
    private final DailyCountService dailyCountService;

    @Override
    public void sendSms(SmsRequestDto smsRequestDto) {
        String recipientUserId = smsRequestDto.getRecipientUserId();

        if (dailyCountService.isReachedDailySmsLimit(recipientUserId)) {
            throw new TooManyRequestsException("You have reached the daily SMS limit. Please try again tomorrow");
        }

        dailyCountService.incrementSmsCount(recipientUserId);
        
        PublishRequest publishRequest = new PublishRequest()
        .withMessage(smsRequestDto.getMessage())
        .withPhoneNumber(smsRequestDto.getInternationalPhoneNumber());

        PublishResult publishResult = amazonSNS.publish(publishRequest);

        // System.out.println(publishResult);
        System.out.println("Message sent to " + smsRequestDto.getInternationalPhoneNumber() + "(...sendSms)");
        // System.out.println("Send sms to " + smsRequestDto.getPhone() + " : " + smsRequestDto.getMessage());
    }
}
