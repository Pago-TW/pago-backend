package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.model.PhoneVerification;

public interface PhoneVerificationService {
    boolean verifyPhone(String userId, ValidatePhoneRequestDto validatePhoneRequestDto);
    PhoneVerification getPhoneVerificationByUserId(String userId);

}
