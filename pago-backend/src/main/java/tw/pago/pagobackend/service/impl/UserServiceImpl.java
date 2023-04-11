package tw.pago.pagobackend.service.impl;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.dto.UserResponseDto;
import tw.pago.pagobackend.dto.UserReviewDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UuidGenerator uuidGenerator;
  private final UserDao userDao;
  private final ModelMapper modelMapper;
  private final ReviewService reviewService;

  @Override
  @Deprecated
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
    userRegisterRequestDto.setProvider(UserAuthProviderEnum.LOCAL);

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
  public UserResponseDto getUserResponseDtoByUser(User user) {

    UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);

    // Get averageRating & totalReview (FOR_SHOPPER)
    ReviewRatingResultDto shopperReviewRatingResultDto = reviewService.calculateAverageRating(user.getUserId(), ReviewTypeEnum.FOR_SHOPPER);
    // Get averageRating & totalReview (FOR_CONSUMER)
    ReviewRatingResultDto consumerReviewRatingResultDto = reviewService.calculateAverageRating(user.getUserId(), ReviewTypeEnum.FOR_CONSUMER);

    // Convert result to UserReviewDTO
    UserReviewDto shopperUserReviewDto = modelMapper.map(shopperReviewRatingResultDto, UserReviewDto.class);
    UserReviewDto consumerUserReviewDto = modelMapper.map(consumerReviewRatingResultDto, UserReviewDto.class);
    shopperUserReviewDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);
    consumerUserReviewDto.setReviewType(ReviewTypeEnum.FOR_CONSUMER);

    // Set shopperUserReviewDto & consumerUserReviewDto To UserResponseDto
    userResponseDto.setShopperReview(shopperUserReviewDto);
    userResponseDto.setConsumerReview(consumerUserReviewDto);


    return userResponseDto;
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
  public void updateUser(UpdateUserRequestDto updateUserRequestDto) {

    // Get old data by id
    User oldUser = userDao.getUserById(updateUserRequestDto.getUserId());

//    if (oldUser.getUserId() == null) {
//      throw new UsernameNotFoundException("The User you want to update not found");
//    }

    // Check frontend update data, or set old data
    updateUserRequestDto.fillEmptyFieldsWithOldData(oldUser);

    // update user
    userDao.updateUser(updateUserRequestDto);
  }

  @Override
  public void processOAuth2PostLogin(String userEmail) {
    User existUser = userDao.getUserByEmail(userEmail);

    if (existUser == null) {
      String newUserId = uuidGenerator.getUuid();
      UserRegisterRequestDto newUser = UserRegisterRequestDto.builder()
          .userId(newUserId)
          .account(userEmail)
          .email(userEmail)
          .provider(UserAuthProviderEnum.GOOGLE)
          .enabled(true)
          .build();

      userDao.createUser(newUser);
    }
  }
}
