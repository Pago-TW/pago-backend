package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.model.PasswordResetToken;

public interface AuthDao {
    void createToken(PasswordResetToken passwordResetToken);

    PasswordResetToken getPasswordResetTokenByToken(String token);

    PasswordResetToken getPasswordResetTokenByUserId(String userId);

    void deletePasswordResetTokenById(String tokenId);
}
