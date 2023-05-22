package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.ChangePasswordRequestDto;
import tw.pago.pagobackend.dto.JwtAuthenticationResponseDto;
import tw.pago.pagobackend.dto.NewPasswordDto;
import tw.pago.pagobackend.dto.PasswordRequestDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.PasswordResetToken;
import tw.pago.pagobackend.model.User;

public interface AuthService {

  JwtAuthenticationResponseDto login(UserLoginRequestDto userLoginRequestDto);

  User register(UserRegisterRequestDto userRegisterRequestDto);

  PasswordResetToken requestPasswordReset(PasswordRequestDto passwordRequestDto);

  String fetchEmailFromToken(String token);

  void resetPassword(NewPasswordDto newPasswordDto);

  void changePassword(User user, ChangePasswordRequestDto changePasswordRequestDto);
}
