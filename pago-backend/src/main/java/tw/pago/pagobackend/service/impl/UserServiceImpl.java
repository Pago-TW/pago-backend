package tw.pago.pagobackend.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.UserService;

@Component
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private UserDao userDao;

  @Override
  public Integer register(UserRegisterRequestDto userRegisterRequestDto) {

    // Get User By Email, will be used to check isExist?
    User user = userDao.getUserByEmail(userRegisterRequestDto.getEmail());

    // Check email isExist? return BAD_REQUEST : Register
    if (user != null) {
      log.warn("該email: {} 已被註冊", userRegisterRequestDto.getEmail());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    // Hash user's register password (MD5)
    String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequestDto.getPassword().getBytes());
    userRegisterRequestDto.setPassword(hashedPassword);

    // register
    return userDao.createUser(userRegisterRequestDto);
  }

  @Override
  public User getUserById(Integer userId) {
    return userDao.getUserById(userId);
  }

  @Override
  public User login(UserLoginRequestDto userLoginRequestDto) {
    User user = userDao.getUserByEmail(userLoginRequestDto.getEmail());


    // Check User Exist
    if (user == null) {
      log.warn("該email: {} 尚未註冊", userLoginRequestDto.getEmail());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }


    // Hash user's login password (MD5)
    String hashedpassword = DigestUtils.md5DigestAsHex(userLoginRequestDto.getPassword().getBytes());

    // Confirm user's password correct
    if (user.getPassword().equals(hashedpassword)) {
      return user;
    } else {
      log.warn("email {} 的密碼不正確", userLoginRequestDto.getEmail());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }
}
