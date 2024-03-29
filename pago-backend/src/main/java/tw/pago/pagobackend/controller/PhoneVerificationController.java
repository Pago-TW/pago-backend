package tw.pago.pagobackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dto.SnsRequestDto;
import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.model.Otp;
import tw.pago.pagobackend.model.PhoneVerification;
import tw.pago.pagobackend.service.OtpService;
import tw.pago.pagobackend.service.PhoneVerificationService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@AllArgsConstructor
public class PhoneVerificationController {
    private final CurrentUserInfoProvider currentUserInfoProvider;
    private final PhoneVerificationService phoneVerificationService;
    private final OtpService otpService;

    @GetMapping("/phone-verification-status")
    public ResponseEntity<?> getPhoneVerificationStatus() {
        String userId = currentUserInfoProvider.getCurrentLoginUserId();
        PhoneVerification phoneVerification = phoneVerificationService.getPhoneVerificationByUserId(userId);
        if (phoneVerification == null) {
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }

        return ResponseEntity.status(HttpStatus.OK).body(phoneVerification.isPhoneVerified());
    }

    @PostMapping("/phone/otp")
    public ResponseEntity<?> sendSns(@RequestBody SnsRequestDto snsRequestDto) {
        Otp otp = otpService.requestOtp(snsRequestDto.getPhone());

        return ResponseEntity.status(HttpStatus.CREATED).body(otp);
    }

    @PostMapping("/phone/validate")
    public ResponseEntity<?> validatePhone(@RequestBody ValidatePhoneRequestDto validatePhoneRequestDto) {
        String userId = currentUserInfoProvider.getCurrentLoginUserId();
        boolean isValid = phoneVerificationService.verifyPhone(userId, validatePhoneRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(isValid);
    }
}
