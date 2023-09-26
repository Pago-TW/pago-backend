package tw.pago.pagobackend.service.impl;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.OtpDao;
import tw.pago.pagobackend.dto.SmsRequestDto;
import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.model.Otp;
import tw.pago.pagobackend.service.OtpService;
import tw.pago.pagobackend.service.SmsService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UuidGenerator uuidGenerator;
    private final SmsService smsService;
    // private final PhoneVerificationService phoneVerificationService; // TODO remove after testing
    private final CurrentUserInfoProvider currentUserInfoProvider;
    private final OtpDao otpDao;

    @Transactional
    @Override
    public Otp requestOtp(String phone) {
        String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
        String internationalPhoneNumber = "+886" + phone.substring(1);
        Otp existingOtp = otpDao.getOtpByPhone(internationalPhoneNumber);
        if (existingOtp != null) {
            ZonedDateTime latestResetDateTime = existingOtp.getCreateDate();
            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        
            Duration duration = Duration.between(latestResetDateTime, currentDateTime);
            long differenceInSeconds = duration.getSeconds();
            
            long cooldownInSeconds = 3 * 60; // 3 minutes in seconds
        
            long secondsRemaining = cooldownInSeconds - differenceInSeconds;

            // TODO REMOVE THIS AFTER TESTING
//            if (differenceInSeconds < cooldownInSeconds) {
//                System.out.println("It's been less than 3 minutes since the last reset request");
//              // It's been less than 3 minutes since the last reset request
//              throw new TooManyRequestsException("You can request another SNS in " + secondsRemaining + " seconds.", latestResetDateTime, secondsRemaining);
//            }
            otpDao.deleteOtpById(existingOtp.getOtpId());
        }

        Otp otp = generateOtp(internationalPhoneNumber);
        otpDao.createOtp(otp);

        String message = "您的 Pago 驗證碼為： " + otp.getOtpCode();

        SmsRequestDto smsRequestDto = SmsRequestDto.builder()
            .internationalPhoneNumber(internationalPhoneNumber)
            .message(message)
            .recipientUserId(currentLoginUserId)
            .build();
        smsService.sendSms(smsRequestDto);

        return otp;

    }

    @Override
    public Otp generateOtp(String internationalPhoneNumber) {

        String otpId = uuidGenerator.getUuid();
        String otpCode = String.valueOf(((int) (Math.random() * (1000000 - 100000))) + 100000);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

        return Otp.builder()
            .otpId(otpId)
            .internationalPhoneNumber(internationalPhoneNumber)
            .otpCode(otpCode)
            .expiryDate(now.plusMinutes(15))
            .createDate(now)
            .build();
    }

    @Transactional
    @Override
    public boolean validateOtp(ValidatePhoneRequestDto validatePhoneRequestDto) {
        String phone = validatePhoneRequestDto.getPhone();
        String internationalPhoneNumber = "+886" + phone.substring(1);
        String otpCode = validatePhoneRequestDto.getOtpCode();
        // String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId(); // TODO remove after testing

        Otp otp = otpDao.getOtpByOtpCode(otpCode);

        if (otp == null) {
            return false;
        }

        if (otp.getExpiryDate().isBefore(ZonedDateTime.now(ZoneId.of("UTC")))) {
            return false;
        }

        if (otp.getOtpCode().equals(otpCode) && otp.getInternationalPhoneNumber().equals(internationalPhoneNumber)) {
            otpDao.deleteOtpById(otp.getOtpId());
            // phoneVerificationService.verifyPhone(currentLoginUserId, phone); // TODO 如果本身沒手機號碼，加號碼上去 // TODO remove after testing
            return true;
        } else {
            return false;
        }
    }
}
