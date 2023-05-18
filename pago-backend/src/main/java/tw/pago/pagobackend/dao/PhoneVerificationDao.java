package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.PhoneVerificationDto;
import tw.pago.pagobackend.model.PhoneVerification;

public interface PhoneVerificationDao {
    PhoneVerification getPhoneVerificationByUserId(String userId);
    void createPhoneVerification(PhoneVerificationDto phoneVerificationDto);
}
