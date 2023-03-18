package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;

public interface UserDao {
  void createUser(UserRegisterRequestDto userRegisterRequestDto);

  User getUserById(String userId);

  User getUserByEmail(String email);
}
