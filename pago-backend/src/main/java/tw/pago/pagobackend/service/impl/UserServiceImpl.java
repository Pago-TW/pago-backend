package tw.pago.pagobackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;
import tw.pago.pagobackend.constant.UserProviderEnum;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private UuidGenerator uuidGenerator;
  @Autowired
  private UserDao userDao;

  @Override
  public User register(UserRegisterRequestDto userRegisterRequestDto) throws UsernameNotFoundException {

    // Get User By Email, will be used to check isExist?
    User user = userDao.getUserByEmail(userRegisterRequestDto.getEmail());

    // Check email isExist? return BAD_REQUEST : Register
    if (user != null) {
      log.warn("該email: {} 已被註冊", userRegisterRequestDto.getEmail());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    String userId = uuidGenerator.getUuid();
    userRegisterRequestDto.setUserId(userId);
    userRegisterRequestDto.setProvider(UserProviderEnum.LOCAL);

    // Hash user's register password (MD5)
    String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequestDto.getPassword().getBytes());
    userRegisterRequestDto.setPassword(hashedPassword);

    // register
    userDao.createUser(userRegisterRequestDto);


    user = userDao.getUserById(userId);

    if (user == null) {
      throw new UsernameNotFoundException(userId);
    } else {
      return user;
    }

  }

  @Override
  public User getUserById(String userId) {
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

  @Override
  public void processOAuth2PostLogin(String userEmail) {
    User existUser = userDao.getUserByEmail(userEmail);

    if (existUser == null) {
      UserRegisterRequestDto newUser = UserRegisterRequestDto.builder()
          .email(userEmail)
          .provider(UserProviderEnum.GOOGLE)
          .enabled(true)
          .build();

      userDao.createUser(newUser);
    }
  }
}
