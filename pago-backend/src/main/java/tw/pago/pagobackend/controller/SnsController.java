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
import tw.pago.pagobackend.service.OtpService;

@RestController
@AllArgsConstructor
public class SnsController {
    
    private final OtpService otpService;

    // @PostMapping("/otp/send-sns") //TODO remove after confirmation
    // public ResponseEntity<?> sendSns(@RequestBody SnsRequestDto snsRequestDto) {
    //     Otp otp = otpService.requestOtp(snsRequestDto.getPhone());

    //     return ResponseEntity.status(HttpStatus.CREATED).body(otp);
    // }

    // @PostMapping("/otp/validate") //TODO remove after confirmation
    // public ResponseEntity<?> validateOtp(@RequestBody ValidatePhoneRequestDto validatePhoneRequestDto) {
    //     boolean isValid = otpService.validateOtp(validatePhoneRequestDto);

    //     return ResponseEntity.status(HttpStatus.OK).body(isValid);
    // }

    
}
