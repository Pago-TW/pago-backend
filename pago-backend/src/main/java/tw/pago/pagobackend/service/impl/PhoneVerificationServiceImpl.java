package tw.pago.pagobackend.service.impl;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dao.PhoneVerificationDao;
import tw.pago.pagobackend.dto.PhoneVerificationDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.model.PhoneVerification;
import tw.pago.pagobackend.service.PhoneVerificationService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class PhoneVerificationServiceImpl implements PhoneVerificationService {
    
    private final UuidGenerator uuidGenerator;
    private final PhoneVerificationDao phoneVerificationDao;

    @Override
    public void verifyPhone(String userId, String phone) {
        PhoneVerification phoneVerification = phoneVerificationDao.getPhoneVerificationByUserId(userId);
        if (phoneVerification != null) {
            throw new BadRequestException(phone + " is already verified (this should not happen and something must have went wrong)");
        }

        String verificationId = uuidGenerator.getUuid();

        PhoneVerificationDto phoneVerificationDto = PhoneVerificationDto.builder()
            .verificationId(verificationId)
            .userId(userId)
            .phone(phone)
            .isPhoneVerified(true)
            .build();
            
        phoneVerificationDao.createPhoneVerification(phoneVerificationDto);
        System.out.println("Phone verification created (...verifyPhone)"); 
    }
}
