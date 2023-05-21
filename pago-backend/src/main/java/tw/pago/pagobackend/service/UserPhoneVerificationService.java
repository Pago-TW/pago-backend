package tw.pago.pagobackend.service;

import tw.pago.pagobackend.model.User;

public interface UserPhoneVerificationService {

  boolean isUserVerifiedPhone(String userId);

  boolean isUserVerifiedPhone(User user);

}
