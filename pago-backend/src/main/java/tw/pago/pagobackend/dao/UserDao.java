package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;

public interface UserDao {
  Integer createUser(UserRegisterRequestDto userRegisterRequestDto);

  User getUserById(Integer userId);

  User getUserByEmail(String email);
}
