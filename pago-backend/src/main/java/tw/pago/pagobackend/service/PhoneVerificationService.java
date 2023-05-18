package tw.pago.pagobackend.service;

public interface PhoneVerificationService {
    void verifyPhone(String userId, String phone);
}
