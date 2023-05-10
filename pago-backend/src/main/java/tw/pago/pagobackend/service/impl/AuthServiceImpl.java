package tw.pago.pagobackend.service.impl;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tw.pago.pagobackend.constant.AccountStatusEnum;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.AuthDao;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.EmailRequestDto;
import tw.pago.pagobackend.dto.JwtAuthenticationResponseDto;
import tw.pago.pagobackend.dto.JwtDto;
import tw.pago.pagobackend.dto.NewPasswordDto;
import tw.pago.pagobackend.dto.PasswordRequestDto;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.model.PasswordResetToken;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.AuthService;
import tw.pago.pagobackend.service.SesEmailService;
import tw.pago.pagobackend.util.EntityPropertyUtil;
import tw.pago.pagobackend.util.JwtTokenProvider;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
public class AuthServiceImpl implements AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Value("${base.url}")
  private final String BASE_URL = null;

  @Autowired
  private ModelMapper modelMapper;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtTokenProvider jwtTokenProvider;
  @Autowired
  private UserDao userDao;
  @Autowired
  private AuthDao authDao;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UuidGenerator uuidGenerator;
  @Autowired
  private SesEmailService sesEmailService;

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

    // Get User By Email and Phone, will be used to check isExist?
    User userByEmail = userDao.getUserByEmail(userRegisterRequestDto.getEmail());
    User userByPhone = userDao.getUserByPhone(userRegisterRequestDto.getPhone());

    // Check email or phone isExist? return BAD_REQUEST : Register
    if (userByEmail != null || userByPhone != null) {
      StringBuilder message = new StringBuilder("Registration failed: ");
      if (userByEmail != null) {
          message.append("The email '").append(userRegisterRequestDto.getEmail()).append("' is already registered. ");
      }
      if (userByPhone != null) {
          message.append("The phone '").append(userRegisterRequestDto.getPhone()).append("' is already registered.");
      }
      throw new BadRequestException(message.toString());
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

    User user = userDao.getUserById(userId);

    if (user == null) {
      throw new UsernameNotFoundException(userId);
    } else {
      return user;
    }
  }

  @Override
  @Transactional
  public PasswordResetToken requestPasswordReset(PasswordRequestDto passwordRequestDto) {
    User user = userDao.getUserByEmail(passwordRequestDto.getEmail());
    if (user == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"); // TODO 不能給 Google 登陸的用戶使用忘記密碼功能
    }

    String token = uuidGenerator.getUuid();
    String passwordResetTokenId = uuidGenerator.getUuid();

    PasswordResetToken passwordResetToken = PasswordResetToken.builder()
        .passwordResetTokenId(passwordResetTokenId)
        .userId(user.getUserId())
        .token(token)
        .expiryDate(LocalDateTime.now().plusHours(24)) // Set token to expire in 24 hours
        .build();

    authDao.createToken(passwordResetToken);

    String passwordUrl = BASE_URL + "/auth/reset-password/" + token;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String passwordResetRequestCreateDate = formatter.format(LocalDateTime.now());
    String contentTitle = "重設密碼";
    String recipientUserEmail = user.getEmail();
    String username = user.getFirstName();
    String emailBody = String.format("您已於 <b>%s</b> 提出重設密碼的請求，請點擊下方連結進行密碼重設。<br><br><p><a href=\"%s\">重設密碼連結</a></p>" +
    "<br><br>如果您並未申請重設密碼，請忽略此電子郵件", passwordResetRequestCreateDate, passwordUrl);

    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(recipientUserEmail);
    emailRequest.setSubject("【Pago " + contentTitle + "】");
    emailRequest.setBody(emailBody);
    emailRequest.setContentTitle(contentTitle);
    emailRequest.setRecipientName(username);

    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent! (requestPasswordReset)");

    return authDao.getPasswordResetTokenByToken(token);
  }

  @Override
  public String fetchEmailFromToken(String token) {
    PasswordResetToken passwordResetToken = authDao.getPasswordResetTokenByToken(token);
    String userId = passwordResetToken.getUserId();
    User user = userDao.getUserById(userId);
    String email = user.getEmail();


    return email;
  }

  @Override
  public void resetPassword(NewPasswordDto newPasswordDto) {

    PasswordResetToken passwordResetToken = authDao.getPasswordResetTokenByToken(newPasswordDto.getToken());

    if (passwordResetToken == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found");
    }

    if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
    }

    User user = userDao.getUserById(passwordResetToken.getUserId());

    String hashedPassword = passwordEncoder.encode(newPasswordDto.getNewPassword());
    UpdateUserRequestDto updateUserRequestDto = UpdateUserRequestDto.builder()
        .password(hashedPassword)
        .build();

    String[] presentPropertyNames = EntityPropertyUtil.getPresentPropertyNames(updateUserRequestDto);
    BeanUtils.copyProperties(user, updateUserRequestDto, presentPropertyNames);
    userDao.updateUser(updateUserRequestDto);

    authDao.deletePasswordResetTokenById(passwordResetToken.getPasswordResetTokenId());
  }
}
