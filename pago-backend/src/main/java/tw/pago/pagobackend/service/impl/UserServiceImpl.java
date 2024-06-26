package tw.pago.pagobackend.service.impl;

import java.net.URL;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CompletionRatingEnum;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.CancellationRecordDao;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.dto.UserResponseDto;
import tw.pago.pagobackend.dto.UserReviewDto;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.FileService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserPhoneVerificationService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
  private static final String OBJECT_TYPE = "USER";

  private final UuidGenerator uuidGenerator;
  private final UserDao userDao;
  private final ModelMapper modelMapper;
  private final ReviewService reviewService;
  private OrderService orderService;
  private final TripService tripService;
  private BidService bidService;
  private final CancellationRecordDao cancellationRecordDao;
  private final UserPhoneVerificationService userPhoneVerificationService;
  private final FileService fileService;

  public UserServiceImpl(UuidGenerator uuidGenerator,
      UserDao userDao,
      ModelMapper modelMapper,
      ReviewService reviewService,
      TripService tripService,
      CancellationRecordDao cancellationRecordDao,
      UserPhoneVerificationService userPhoneVerificationService,
      FileService fileService) {
    this.uuidGenerator = uuidGenerator;
    this.userDao = userDao;
    this.modelMapper = modelMapper;
    this.reviewService = reviewService;
    this.tripService = tripService;
    this.cancellationRecordDao = cancellationRecordDao;
    this.userPhoneVerificationService = userPhoneVerificationService;
    this.fileService = fileService;

  }

  @Autowired
  public void setOrderService(@Lazy OrderService orderService) {
    this.orderService = orderService;
  }

  @Autowired
  public void setBidService(@Lazy BidService bidService) {
    this.bidService = bidService;
  }

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
    User user = userDao.getUserById(userId);

    // Check is user has verified phone
    boolean isUserVerifiedPhone = userPhoneVerificationService.isUserVerifiedPhone(userId);
    user.setIsPhoneVerified(isUserVerifiedPhone);

    return user;
  }

  @Override
  public String censorString(String str, int start, int end) {
    // Build a regex pattern with three capturing groups:
    // 1. The first group captures the characters before the range to be censored.
    // 2. The second group captures the characters within the range to be censored.
    // 3. The third group captures the characters after the range to be censored.
    String regex = "(.{" + start + "})(.{" + (end - start) + "})(.*)";

    // 1. $1 refers to the characters captured by the first group in the regex
    // pattern (characters before the range to be censored).
    // 2. "*".repeat(end - start) creates a string of asterisks with the same length
    // as the range to be censored.
    // 3. $3 refers to the characters captured by the third group in the regex
    // pattern (characters after the range to be censored).
    String replacement = "$1" + "*".repeat(end - start) + "$3";

    return str.replaceAll(regex, replacement);
  }

  @Override
  public User getUserByEmail(String email) {
    User user = userDao.getUserByEmail(email);
    String userId = user.getUserId();

    // Check is user has verified phone
    boolean isUserVerifiedPhone = userPhoneVerificationService.isUserVerifiedPhone(userId);
    user.setIsPhoneVerified(isUserVerifiedPhone);

    return user;
  }

  @Override
  public UserResponseDto getUserResponseDtoByUser(User user) {
    String userId = user.getUserId();

    UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);

    // Get averageRating & totalReview (FOR_SHOPPER)
    ReviewRatingResultDto shopperReviewRatingResultDto = reviewService.calculateAverageRating(user.getUserId(),
        ReviewTypeEnum.FOR_SHOPPER);
    // Get averageRating & totalReview (FOR_CONSUMER)
    ReviewRatingResultDto consumerReviewRatingResultDto = reviewService.calculateAverageRating(user.getUserId(),
        ReviewTypeEnum.FOR_CONSUMER);

    // Convert result to UserReviewDTO
    UserReviewDto shopperUserReviewDto = modelMapper.map(shopperReviewRatingResultDto, UserReviewDto.class);
    UserReviewDto consumerUserReviewDto = modelMapper.map(consumerReviewRatingResultDto, UserReviewDto.class);
    shopperUserReviewDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);
    consumerUserReviewDto.setReviewType(ReviewTypeEnum.FOR_CONSUMER);

    // Set shopperUserReviewDto & consumerUserReviewDto To UserResponseDto
    userResponseDto.setShopperReview(shopperUserReviewDto);
    userResponseDto.setConsumerReview(consumerUserReviewDto);

    // Get user completionRating
    CompletionRatingEnum completionRating = getUserCompletionRating(user);
    userResponseDto.setCompletionRating(completionRating);

    // Set isTraveling
    userResponseDto.setTraveling(tripService.isUserTraveling(userId));

    return userResponseDto;
  }

  @Override
  @Deprecated
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

    // Check frontend update data, or set old data
    updateUserRequestDto.fillEmptyFieldsWithOldData(oldUser);

    // update user
    userDao.updateUser(updateUserRequestDto);
  }

  @Override
  public void updateUserAvatar(String userId, List<MultipartFile> files) {
    String avatarUrl = "";

    // Upload avatar file
    CreateFileRequestDto createFileRequestDto = new CreateFileRequestDto();
    createFileRequestDto.setFileCreator(userId);
    createFileRequestDto.setObjectId(userId);
    createFileRequestDto.setObjectType(OBJECT_TYPE);
    List<URL> fileUrls = fileService.uploadFile(files, createFileRequestDto);

    if (!fileUrls.isEmpty()) {
      avatarUrl = fileUrls.get(0).toString();
    } else {
      throw new ResourceNotFoundException("Avatar url is empty");
    }

    // update user's avatarUrl
    UpdateUserRequestDto updateUserRequestDto = UpdateUserRequestDto.builder()
        .userId(userId)
        .avatarUrl(avatarUrl)
        .build();

    updateUser(updateUserRequestDto);

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

  public int getUserTotalOrdersInProcurementProcess(String userId) {
    final int startIndex = 0;
    final int size = 999;

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(userId)
        .bidStatus(BidStatusEnum.IS_CHOSEN)
        .startIndex(startIndex)
        .size(size)
        .orderBy("create_date")
        .sort("DESC")
        .build();

    // Count orders where the user is the consumer
    List<Order> orderList = orderService.getOrderList(listQueryParametersDto);
    long consumerOrderCount = orderList.stream()
        .filter(order -> {
          listQueryParametersDto.setOrderId(order.getOrderId());
          List<Bid> bidList = bidService.getBidList(listQueryParametersDto);
          return !bidList.isEmpty();
        })
        .count();

    listQueryParametersDto.setOrderId(null);

    // Count orders where the user is the shopper
    List<Trip> tripList = tripService.getTripList(listQueryParametersDto);
    long shopperOrderCount = tripList.stream()
        .filter(trip -> {
          listQueryParametersDto.setTripId(trip.getTripId());
          List<Bid> bidList = bidService.getBidList(listQueryParametersDto);
          return !bidList.isEmpty();
        })
        .count();

    return (int) (consumerOrderCount + shopperOrderCount);
  }

  public double calculateUserCancellationRating(int totalCancellations, int totalOrdersInProcurementProcess) {
    if (totalOrdersInProcurementProcess == 0) {
      return 0.0;
    }

    double cancellationRating = (totalCancellations / (double) totalOrdersInProcurementProcess) * 100;

    return cancellationRating;
  }

  public CompletionRatingEnum getUserCompletionRating(User user) {

    int totalOrdersInProcurementProcess = getUserTotalOrdersInProcurementProcess(user.getUserId());
    int totalCancellationRecords = cancellationRecordDao.countCancellationRecord(user.getUserId());

    System.out.println("totalOrdersInProcurementProcess: " + totalOrdersInProcurementProcess);
    System.out.println("totalCancellationRecords: " + totalCancellationRecords);
    // Check if the user is new
    if (totalOrdersInProcurementProcess == 0 && totalCancellationRecords == 0) {
      return CompletionRatingEnum.NOU;
    } else if (totalCancellationRecords == 0) {
      return CompletionRatingEnum.EXCELLENT;
    }

    double cancellationRating = calculateUserCancellationRating(totalCancellationRecords,
        totalOrdersInProcurementProcess);
    System.out.println("Cancellation: " + cancellationRating);

    if (cancellationRating >= 90) {
      return CompletionRatingEnum.EXCELLENT;
    } else if (cancellationRating >= 80) {
      return CompletionRatingEnum.VERY_GOOD;
    } else if (cancellationRating >= 60) {
      return CompletionRatingEnum.GOOD;
    } else {
      return CompletionRatingEnum.POOR;
    }
  }

  public List<User> searchUsers(String query) {
    return userDao.searchUsers(query);
  }

  public boolean isPhoneAlreadyRegistered(String phone) {
    return userDao.isPhoneAlreadyRegistered(phone);
  }
}
