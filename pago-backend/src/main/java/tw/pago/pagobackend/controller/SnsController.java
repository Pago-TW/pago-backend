package tw.pago.pagobackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dto.SnsRequestDto;
import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.model.Otp;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OtpService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@AllArgsConstructor
public class SnsController {
    
    private final OtpService otpService;
    private final CurrentUserInfoProvider currentUserInfoProvider;

    @PostMapping("/otp") 
    public ResponseEntity<?> sendSns() {
        User user = currentUserInfoProvider.getCurrentLoginUser();
        String phone = user.getPhone();

        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Phone number is not available for the current user.");
        }
        
        Otp otp = otpService.requestOtp(phone);

        return ResponseEntity.status(HttpStatus.CREATED).body(otp);
    }

    // @PostMapping("/otp/validate") //TODO remove after confirmation
    // public ResponseEntity<?> validateOtp(@RequestBody ValidatePhoneRequestDto validatePhoneRequestDto) {
    //     boolean isValid = otpService.validateOtp(validatePhoneRequestDto);

    //     return ResponseEntity.status(HttpStatus.OK).body(isValid);
    // }

    
}
