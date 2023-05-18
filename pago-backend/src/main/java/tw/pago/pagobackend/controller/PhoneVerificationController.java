package tw.pago.pagobackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dao.PhoneVerificationDao;
import tw.pago.pagobackend.model.PhoneVerification;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@AllArgsConstructor
public class PhoneVerificationController {
    private final PhoneVerificationDao phoneVerificationDao;
    private final CurrentUserInfoProvider currentUserInfoProvider;

    @GetMapping("/phone-verification-status")
    public ResponseEntity<?> getPhoneVerificationStatus() {
        String userId = currentUserInfoProvider.getCurrentLoginUserId();
        PhoneVerification phoneVerification = phoneVerificationDao.getPhoneVerificationByUserId(userId);
        if (phoneVerification == null) {
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }

        return ResponseEntity.status(HttpStatus.OK).body(phoneVerification.isPhoneVerified());
    }
}
