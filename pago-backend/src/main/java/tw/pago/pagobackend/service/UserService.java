package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;

public interface UserService {
  Integer register(UserRegisterRequestDto userRegisterRequestDto);

  User getUserById(Integer userId);

  User login(UserLoginRequestDto userLoginRequestDto);
}
