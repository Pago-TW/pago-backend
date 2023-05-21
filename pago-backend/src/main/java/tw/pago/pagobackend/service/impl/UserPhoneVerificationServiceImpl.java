package tw.pago.pagobackend.service.impl;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.PhoneVerificationDao;
import tw.pago.pagobackend.model.PhoneVerification;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.UserPhoneVerificationService;

@Service
@AllArgsConstructor
public class UserPhoneVerificationServiceImpl implements UserPhoneVerificationService {
  private final PhoneVerificationDao phoneVerificationDao;

  @Override
  public boolean isUserVerifiedPhone(String userId) {
    PhoneVerification phoneVerification = phoneVerificationDao.getPhoneVerificationByUserId(userId);

    boolean isPhoneVerified = Optional.ofNullable(phoneVerification)
        .map(PhoneVerification::isPhoneVerified)
        .orElse(false);

    return isPhoneVerified;
  }

  @Override
  public boolean isUserVerifiedPhone(User user) {
    String userId = user.getUserId();
    PhoneVerification phoneVerification = phoneVerificationDao.getPhoneVerificationByUserId(userId);

    boolean isPhoneVerified = Optional.ofNullable(phoneVerification)
        .map(PhoneVerification::isPhoneVerified)
        .orElse(false);

    return isPhoneVerified;
  }
}
