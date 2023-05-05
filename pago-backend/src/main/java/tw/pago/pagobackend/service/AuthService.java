package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.JwtAuthenticationResponseDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;

public interface AuthService {

  JwtAuthenticationResponseDto login(UserLoginRequestDto userLoginRequestDto);

  User register(UserRegisterRequestDto userRegisterRequestDto);

}
