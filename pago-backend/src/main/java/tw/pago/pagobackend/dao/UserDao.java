package tw.pago.pagobackend.dao;

import java.util.List;

import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;

public interface UserDao {
  void createUser(UserRegisterRequestDto userRegisterRequestDto);

  User getUserById(String userId);

  User getUserByEmail(String email);

  User getUserByPhone(String phone);

  void updateUser(UpdateUserRequestDto updateUserRequestDto);

  List<User> searchUsers(String query);

  boolean isPhoneAlreadyRegistered(String phone);
}
