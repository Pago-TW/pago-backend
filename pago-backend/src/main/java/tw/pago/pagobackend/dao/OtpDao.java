package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.model.Otp;

public interface OtpDao {
    void createOtp(Otp otp);
    Otp getOtpByOtpCode(String otpCode);
    Otp getOtpByPhone(String phone);
    void deleteOtpById(String otpId);
}
