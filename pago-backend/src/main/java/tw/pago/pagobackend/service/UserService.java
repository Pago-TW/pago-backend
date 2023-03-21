package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;

public interface UserService {
  User register(UserRegisterRequestDto userRegisterRequestDto);

  User getUserById(String userId);

  User login(UserLoginRequestDto userLoginRequestDto);

  void updateUser(UpdateUserRequestDto updateUserRequestDto);

  void processOAuth2PostLogin(String userEmail);
}
