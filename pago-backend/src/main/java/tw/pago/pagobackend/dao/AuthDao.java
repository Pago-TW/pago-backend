package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.model.PasswordResetToken;

public interface AuthDao {
    void createToken(PasswordResetToken passwordResetToken);

    PasswordResetToken getPasswordResetTokenByToken(String token);

    void deletePasswordResetTokenById(String tokenId);
}
