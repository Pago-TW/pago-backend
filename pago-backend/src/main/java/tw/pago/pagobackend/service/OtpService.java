package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.model.Otp;

public interface OtpService {
    Otp generateOtp(String internationalPhoneNumber);
    Otp requestOtp(String phone);
    boolean validateOtp(ValidatePhoneRequestDto validatePhoneRequestDto);
}
