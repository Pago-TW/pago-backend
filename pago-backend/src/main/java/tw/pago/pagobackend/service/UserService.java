package tw.pago.pagobackend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.dto.UserResponseDto;
import tw.pago.pagobackend.model.User;

public interface UserService {
  @Deprecated User register(UserRegisterRequestDto userRegisterRequestDto);

  User getUserById(String userId);

  User getUserByEmail(String email);

  UserResponseDto getUserResponseDtoByUser(User user);

  User login(UserLoginRequestDto userLoginRequestDto);

  void updateUser(UpdateUserRequestDto updateUserRequestDto);

  void updateUserAvatar(String userId, List<MultipartFile> files);

  void processOAuth2PostLogin(String userEmail);

  List<User> searchUsers(String query);

  boolean isPhoneAlreadyRegistered(String phone);
}
