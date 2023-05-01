package tw.pago.pagobackend.service.impl;


import java.util.Date;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tw.pago.pagobackend.constant.AccountStatusEnum;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.JwtAuthenticationResponseDto;
import tw.pago.pagobackend.dto.JwtDto;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.AuthService;
import tw.pago.pagobackend.util.EntityPropertyUtil;
import tw.pago.pagobackend.util.JwtTokenProvider;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
public class AuthServiceImpl implements AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Autowired
  private ModelMapper modelMapper;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtTokenProvider jwtTokenProvider;
  @Autowired
  private UserDao userDao;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UuidGenerator uuidGenerator;

  @Override
  public JwtAuthenticationResponseDto login(UserLoginRequestDto userLoginRequestDto) {

    // Retrieve the user by email
    User user = userDao.getUserByEmail(userLoginRequestDto.getEmail());

    // Authenticate the user with the given email and password
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userLoginRequestDto.getEmail(),
            userLoginRequestDto.getPassword()));

    // Set the authenticated user in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate a JWT token for the authenticated user
    String jwtToken = jwtTokenProvider.generateJwtToken(authentication);
    JwtDto jwtDto = new JwtDto();
    jwtDto.setAccessToken(jwtToken);
    jwtDto.setTokenType("Bearer");

    // Update the user's last login time
    UpdateUserRequestDto updateUserRequestDto = UpdateUserRequestDto.builder()
        .lastLogin(new Date())
        .build();

    // Copy the properties from the user to the updateUserRequestDto object
    String[] presentPropertyNames = EntityPropertyUtil.getPresentPropertyNames(updateUserRequestDto);
    BeanUtils.copyProperties(user, updateUserRequestDto, presentPropertyNames);
    userDao.updateUser(updateUserRequestDto);

    // Convert the user object to a user DTO
    UserDto userDto = modelMapper.map(user, UserDto.class);

    // Build the JWT authentication response DTO with the token and user DTO
    JwtAuthenticationResponseDto jwtAuthenticationResponseDto = JwtAuthenticationResponseDto.builder()
        .token(jwtDto)
        .user(userDto)
        .build();

    return jwtAuthenticationResponseDto;
  }



  @Override
  public User register(UserRegisterRequestDto userRegisterRequestDto) {

    // Get User By Email, will be used to check isExist?
    User user = userDao.getUserByEmail(userRegisterRequestDto.getEmail());

    // Check email isExist? return BAD_REQUEST : Register
    if (user != null) {
      log.warn("該email: {} 已被註冊", userRegisterRequestDto.getEmail());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    String userId = uuidGenerator.getUuid();
    userRegisterRequestDto.setUserId(userId);
    userRegisterRequestDto.setAccountStatus(AccountStatusEnum.ACTIVE);
    userRegisterRequestDto.setProvider(UserAuthProviderEnum.LOCAL);
    userRegisterRequestDto.setGender(userRegisterRequestDto.getGender() == null ?
        GenderEnum.PREFER_NOT_TO_SAY : userRegisterRequestDto.getGender());

    // Hash user's register password
    String hashedPassword = passwordEncoder.encode(userRegisterRequestDto.getPassword());
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
}
