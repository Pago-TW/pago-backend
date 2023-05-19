package tw.pago.pagobackend.service;

import tw.pago.pagobackend.model.User;

public interface PhoneVerificationService {
    void verifyPhone(String userId, String phone);

    boolean isUserVerifiedPhone(String userId);

    boolean isUserVerofoedPhone(User user);
}
