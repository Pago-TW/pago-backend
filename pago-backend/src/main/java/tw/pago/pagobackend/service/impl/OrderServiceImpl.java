package tw.pago.pagobackend.service.impl;

import static tw.pago.pagobackend.constant.OrderStatusEnum.CANCELED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.DELIVERED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.FINISHED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.TO_BE_CANCELED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.TO_BE_DELIVERED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.TO_BE_POSTPONED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.TO_BE_PURCHASED;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tw.pago.pagobackend.constant.CancelReasonCategoryEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dao.CancellationRecordDao;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dao.PostponeRecordDao;
import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.BidCreatorDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CalculateOrderAmountResponseDto;
import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.CreateFavoriteOrderRequestDto;
import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.CreatePostponeRecordRequestDto;
import tw.pago.pagobackend.dto.EmailRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.MatchingShopperResponseDto;
import tw.pago.pagobackend.dto.MatchingTripForOrderDto;
import tw.pago.pagobackend.dto.OrderChosenShopperDto;
import tw.pago.pagobackend.dto.OrderItemDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.UpdateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderItemDto;
import tw.pago.pagobackend.dto.UpdatePostponeRecordRequestDto;
import tw.pago.pagobackend.exception.AccessDeniedException;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.exception.DuplicateKeyException;
import tw.pago.pagobackend.exception.IllegalStatusTransitionException;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.model.PostponeRecord;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.FileService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.SesEmailService;
import tw.pago.pagobackend.util.CurrencyUtil;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.EntityPropertyUtil;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

  private static final String OBJECT_TYPE = "order";
  private static final Double PLATFORM_FEE_PERCENT = 4.5;
  private static final Double TARIFF_FEE_PERCENT = 2.5;

  private OrderDao orderDao;
  private UuidGenerator uuidGenerator;
  private FileService fileService;
  private SesEmailService sesEmailService;
  private CurrentUserInfoProvider currentUserInfoProvider;
  private TripDao tripDao;
  private UserDao userDao;
  private ModelMapper modelMapper;
  private BidService bidService;
  private CancellationRecordDao cancellationRecordDao;
  private PostponeRecordDao postponeRecordDao;

  @Autowired
  public OrderServiceImpl(OrderDao orderDao,
      UuidGenerator uuidGenerator,
      FileService fileService,
      SesEmailService sesEmailService,
      CurrentUserInfoProvider currentUserInfoProvider,
      TripDao tripDao,
      UserDao userDao,
      ModelMapper modelMapper,
      CancellationRecordDao cancellationRecordDao,
      PostponeRecordDao postponeRecordDao) {
    this.orderDao = orderDao;
    this.uuidGenerator = uuidGenerator;
    this.fileService = fileService;
    this.sesEmailService = sesEmailService;
    this.currentUserInfoProvider = currentUserInfoProvider;
    this.tripDao = tripDao;
    this.userDao = userDao;
    this.modelMapper = modelMapper;
    this.cancellationRecordDao = cancellationRecordDao;
    this.postponeRecordDao = postponeRecordDao;
  }

  @Autowired
  public void setBidService(@Lazy BidService bidService) {
    this.bidService = bidService;
  }

  @Transactional
  @Override
  public Order createOrder(String userId, List<MultipartFile> files, CreateOrderRequestDto createOrderRequestDto) {

    String orderItemUuid = uuidGenerator.getUuid();
    String orderUuid = uuidGenerator.getUuid();
    String orderSerialNumber = generateOrderSerialNumber(createOrderRequestDto);

    createOrderRequestDto.getCreateOrderItemDto().setOrderItemId(orderItemUuid);
    createOrderRequestDto.setOrderId(orderUuid);
    createOrderRequestDto.setSerialNumber(orderSerialNumber);
    createOrderRequestDto.setPlatformFeePercent(PLATFORM_FEE_PERCENT);
    createOrderRequestDto.setTariffFeePercent(TARIFF_FEE_PERCENT);
    createOrderRequestDto.setOrderStatus(OrderStatusEnum.REQUESTED);

    orderDao.createOrderItem(createOrderRequestDto);
    orderDao.createOrder(userId, createOrderRequestDto);

    // upload file
    CreateFileRequestDto createFileRequestDto = new CreateFileRequestDto();
    createFileRequestDto.setFileCreator(userId);
    createFileRequestDto.setObjectId(orderUuid);
    createFileRequestDto.setObjectType("order");

    List<URL> uploadedUrls = fileService.uploadFile(files, createFileRequestDto);
    // print out all uploadedurls
    for (URL url : uploadedUrls) {
      System.out.println(url);
      System.out.println("Successfully uploaded!");
    }
    // System.out.println("file uploaded");

    Order order = getOrderById(orderUuid);

    return order;
  }

  @Override
  public Order getOrderById(String orderId) {
    Order order = orderDao.getOrderById(orderId);

    // Calculate Fee
    Map<String, BigDecimal> orderEachAmountMap = calculateOrderEachAmount(order);
    order.setTariffFee(orderEachAmountMap.get("tariffFee"));
    order.setPlatformFee(orderEachAmountMap.get("platformFee"));
    order.setTotalAmount(orderEachAmountMap.get("orderTotalAmount"));

    // get file url
    List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(orderId, OBJECT_TYPE);
    order.getOrderItem().setFileUrls(fileUrls);

    // Check if the current logged-in user has placed a bid for the order
    boolean isCurrentLoginUserPlaceBid = isCurrentLoginUserPlaceBid(orderId);
    order.setBidder(isCurrentLoginUserPlaceBid);


    // If the orderStatus is not REQUESTED, it means the order creator has already chosen a shopper(bid)
    if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
      // Get the shopper for the order
      OrderChosenShopperDto orderChosenShopperDto = getOrderChosenShopper(order);

      // Set shopper to order
      order.setShopper(orderChosenShopperDto);
    }

    return order;
  }

  @Override
  public OrderResponseDto getOrderResponseDtoById(String orderId) {
    // Retrieve the order & orderItem by its ID
    Order order = getOrderById(orderId);
    OrderItem orderItem = order.getOrderItem();

    // Get the country and city names for the orderItem
    String orderItemPurchaseCountryName = orderItem.getPurchaseCountry().getName();
    String orderItemPurchaseCityName = orderItem.getPurchaseCity().getEnglishName();

    // Set the country and city names for the ordeItem
    orderItem.setPurchaseCountryName(orderItemPurchaseCountryName);
    orderItem.setPurchaseCityName(orderItemPurchaseCityName);

    // Create a new OrderItemDto and copy properties from the orderItem
    OrderItemDto orderItemDto = new OrderItemDto();
    BeanUtils.copyProperties(orderItem, orderItemDto);

    // Create a new OrderResponseDto and copy properties from the order
    OrderResponseDto orderResponseDto = new OrderResponseDto();
    BeanUtils.copyProperties(order, orderResponseDto);

    // Get the country and city names for the order's destination
    String orderDestinationCountryName = order.getDestinationCountry().getName();
    String orderDestinationCityName = order.getDestinationCity().getEnglishName();

    // Set the destination country and city names, and the order item in the
    // OrderResponseDto
    orderResponseDto.setOrderItem(orderItemDto);
    orderResponseDto.setDestinationCountryName(orderDestinationCountryName);
    orderResponseDto.setDestinationCityName(orderDestinationCityName);

    return orderResponseDto;
  }

  @Override
  public Order getOrderByUserIdAndOrderId(String userId, String orderId) {

    Order order = orderDao.getOrderByUserIdAndOrderId(userId, orderId);

    // Calculate Fee
    Map<String, BigDecimal> orderEachAmountMap = calculateOrderEachAmount(order);
    order.setTariffFee(orderEachAmountMap.get("tariffFee"));
    order.setPlatformFee(orderEachAmountMap.get("platformFee"));
    order.setTotalAmount(orderEachAmountMap.get("orderTotalAmount"));

    // get file url
    List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(orderId, OBJECT_TYPE);
    order.getOrderItem().setFileUrls(fileUrls);

    // Check if the current logged-in user has placed a bid for the order
    boolean isCurrentLoginUserPlaceBid = isCurrentLoginUserPlaceBid(orderId);
    order.setBidder(isCurrentLoginUserPlaceBid);

    if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
      // Get the shopper for the order
      OrderChosenShopperDto orderChosenShopperDto = getOrderChosenShopper(order);

      // Set shopper to order
      order.setShopper(orderChosenShopperDto);
    }

    return order;
  }

  @Override
  public OrderResponseDto getOrderResponseDtoByOrder(Order order) {
    OrderItem orderItem = order.getOrderItem();

    // Get the country and city names for the orderItem
    String orderItemPurchaseCountryName = orderItem.getPurchaseCountry().getName();
    String orderItemPurchaseCityName = orderItem.getPurchaseCity().getEnglishName();

    // Set the country and city names for the orderItem
    orderItem.setPurchaseCountryName(orderItemPurchaseCountryName);
    orderItem.setPurchaseCityName(orderItemPurchaseCityName);

    // Create a new OrderItemDto and copy properties from the orderItem
    OrderItemDto orderItemDto = new OrderItemDto();
    BeanUtils.copyProperties(orderItem, orderItemDto);

    // Create a new OrderResponseDto and copy properties from the order
    OrderResponseDto orderResponseDto = new OrderResponseDto();
    BeanUtils.copyProperties(order, orderResponseDto);

    // Get the country and city names for the order's destination
    String orderDestinationCountryName = order.getDestinationCountry().getName();
    String orderDestinationCityName = order.getDestinationCity().getEnglishName();

    // Set the destination country and city names, and the order item in the
    // OrderResponseDto
    orderResponseDto.setOrderItem(orderItemDto);
    orderResponseDto.setDestinationCountryName(orderDestinationCountryName);
    orderResponseDto.setDestinationCityName(orderDestinationCityName);

    return orderResponseDto;
  }

  @Override
  public String getChosenBidderIdByOrderId(String orderId) {

    String chosenBidderId = orderDao.getChosenBidderIdByOrderId(orderId);

    System.out.println("ChosenBidder: " + chosenBidderId);

    return chosenBidderId;
  }

  @Override
  public void updateOrderAndOrderItemByOrderId(Order oldOrder,
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {

    // Get oldOrder
    if (oldOrder == null) {
      System.out.println("No such order");
      return;
    }


    // Store the old order status
    OrderStatusEnum oldOrderStatus = oldOrder.getOrderStatus();
    if (updateOrderAndOrderItemRequestDto.getOrderStatus() != null){
      OrderStatusEnum newOrderStatus = updateOrderAndOrderItemRequestDto.getOrderStatus();
    }


    OrderItem oldOrderItem = oldOrder.getOrderItem();

    String[] presentPropertyNamesForOrderDto = EntityPropertyUtil
        .getPresentPropertyNames(updateOrderAndOrderItemRequestDto);
    BeanUtils.copyProperties(oldOrder, updateOrderAndOrderItemRequestDto, presentPropertyNamesForOrderDto);

    UpdateOrderItemDto updateOrderItemDto = updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto();
    if (updateOrderItemDto == null) {
      updateOrderItemDto = new UpdateOrderItemDto();
      BeanUtils.copyProperties(oldOrderItem, updateOrderItemDto);
    } else {
    String[] presentPropertyNamesForOrdetItemDto = EntityPropertyUtil
        .getPresentPropertyNames(updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto());
    BeanUtils.copyProperties(oldOrderItem, updateOrderItemDto, presentPropertyNamesForOrdetItemDto);
    }

    updateOrderAndOrderItemRequestDto.setUpdateOrderItemDto(updateOrderItemDto);

    // if status will be modified
    if (!oldOrderStatus.equals(updateOrderAndOrderItemRequestDto.getOrderStatus())) {
      OrderStatusEnum newOrderStatus = updateOrderAndOrderItemRequestDto.getOrderStatus();
      // Check if the Status Transition is legal
      if (isValidOrderStatusTransition(oldOrderStatus, newOrderStatus)) {
        orderDao.updateOrderAndOrderItemByOrderId(updateOrderAndOrderItemRequestDto);
      } else {
        throw new IllegalStatusTransitionException("Invalid status transition from " + oldOrderStatus.toString() + " to " + newOrderStatus.toString());
      }
      // if status won't be modified, directly update Order
    } else {
      orderDao.updateOrderAndOrderItemByOrderId(updateOrderAndOrderItemRequestDto);
    }



    // Check if the order status has been modified
    boolean orderStatusChanged = !Objects.equals(oldOrderStatus, updateOrderAndOrderItemRequestDto.getOrderStatus());

    // If the order status has been changed, send the email notification
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    User currentLoginUser = userDao.getUserById(currentLoginUserId);
    if (orderStatusChanged) {
      System.out.println("status updated");
      // Get the current login user's email
      String currentLoginUserEmail = currentLoginUser.getEmail();
      // Get the order item name
      String orderItemName = updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getName();
      // Get the user name
      String username = currentLoginUser.getFirstName();
      // Get current date
      Date now = new Date();
      String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

      String emailBody = String.format("親愛的%s您好，感謝您使用Pago的服務\n" +
          "您的訂單 %s，已於%s更新為「%s」", username, orderItemName, date,
          updateOrderAndOrderItemRequestDto.getOrderStatus().getDescription());

      // Prepare the email content
      EmailRequestDto emailRequest = new EmailRequestDto();
      emailRequest.setTo(currentLoginUserEmail);
      emailRequest.setSubject("【Pago 訂單狀態更新通知】" + orderItemName);
      emailRequest.setBody(emailBody);

      // Send the email notification
      sesEmailService.sendEmail(emailRequest);
      System.out.println("......Email sent!");
    }
  }

  @Override
  public List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto) {

    List<Order> orderList = orderDao.getOrderList(listQueryParametersDto);

    // calculate each order amount
    for (Order order : orderList) {
      Map<String, BigDecimal> orderEachAmountMap = calculateOrderEachAmount(order);
      order.setTariffFee(orderEachAmountMap.get("tariffFee"));
      order.setPlatformFee(orderEachAmountMap.get("platformFee"));
      order.setTotalAmount(orderEachAmountMap.get("orderTotalAmount"));
      // get file url
      List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(order.getOrderId(), OBJECT_TYPE);
      order.getOrderItem().setFileUrls(fileUrls);

      // Check if the current logged-in user has placed a bid for the order
      boolean isCurrentLoginUserPlaceBid = isCurrentLoginUserPlaceBid(order.getOrderId());
      order.setBidder(isCurrentLoginUserPlaceBid);

      if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
        // Get the shopper for the order
        OrderChosenShopperDto orderChosenShopperDto = getOrderChosenShopper(order);

        // Set shopper to order
        order.setShopper(orderChosenShopperDto);
      }
    }


    return orderList;
  }

  @Override
  public List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto,
      Trip trip) {
    List<Order> matchingOrderListForTrip = orderDao.getMatchingOrderListForTrip(listQueryParametersDto, trip);

    // calculate each order amount
    for (Order order : matchingOrderListForTrip) {
      Map<String, BigDecimal> orderEachAmountMap = calculateOrderEachAmount(order);
      order.setTariffFee(orderEachAmountMap.get("tariffFee"));
      order.setPlatformFee(orderEachAmountMap.get("platformFee"));
      order.setTotalAmount(orderEachAmountMap.get("orderTotalAmount"));
      // get file url
      List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(order.getOrderId(), OBJECT_TYPE);
      order.getOrderItem().setFileUrls(fileUrls);

      // Check if the current logged-in user has placed a bid for the order
      boolean isCurrentLoginUserPlaceBid = isCurrentLoginUserPlaceBid(order.getOrderId());
      order.setBidder(isCurrentLoginUserPlaceBid);

      if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
        // Get the shopper for the order
        OrderChosenShopperDto orderChosenShopperDto = getOrderChosenShopper(order);

        // Set shopper to order
        order.setShopper(orderChosenShopperDto);
      }
    }

    return matchingOrderListForTrip;
  }

  @Override
  public List<OrderResponseDto> getOrderResponseDtoList(
      ListQueryParametersDto listQueryParametersDto) {

    List<Order> orderList = getOrderList(listQueryParametersDto);

    List<OrderResponseDto> orderResponseDtoList = orderList.stream()
        .map(this::getOrderResponseDtoByOrder)
        .collect(Collectors.toList());

    return orderResponseDtoList;
  }

  @Override
  public List<OrderResponseDto> getOrderResponseDtoListByOrderList(List<Order> orderList) {

    List<OrderResponseDto> orderResponseDtoList = orderList.stream()
        .map(this::getOrderResponseDtoByOrder)
        .collect(Collectors.toList());

    return orderResponseDtoList;
  }

  @Override
  public List<MatchingShopperResponseDto> getMatchingShopperList(ListQueryParametersDto listQueryParametersDto) {

    List<Trip> matchingTripList = tripDao.getMatchingTripForOrder(listQueryParametersDto);

    List<MatchingShopperResponseDto> matchingShopperResponseDtoList = matchingTripList.stream()
        .map(trip -> {
          User user = userDao.getUserById(trip.getShopperId());
          MatchingShopperResponseDto matchingShopperResponseDto = modelMapper.map(user, MatchingShopperResponseDto.class);

          // Set tripDto properties based on the trip object
          MatchingTripForOrderDto matchingTripForOrderDto = modelMapper.map(trip, MatchingTripForOrderDto.class);
          matchingShopperResponseDto.setTrip(matchingTripForOrderDto);




          return matchingShopperResponseDto;
        })
        .collect(Collectors.toList());



    return matchingShopperResponseDtoList;
  }

  @Transactional
  @Override
  public void deleteOrderById(String orderId) {
    orderDao.deleteOrderById(orderId);

    // delete file
    String objectType = "order";
    fileService.deleteFilesByObjectIdnType(orderId, objectType);
  }

  @Override
  public void createFavoriteOrder(CreateFavoriteOrderRequestDto createFavoriteOrderRequestDto) {
    String userFavoriteOrderUuid = uuidGenerator.getUuid();
    createFavoriteOrderRequestDto.setUserFavoriteOrderId(userFavoriteOrderUuid);

    orderDao.createFavoriteOrder(createFavoriteOrderRequestDto);
  }

  @Override
  public Integer countOrder(ListQueryParametersDto listQueryParametersDto) {
    Integer total = orderDao.countOrder(listQueryParametersDto);

    return total;
  }



  @Override
  public Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto,
      Trip trip) {
    Integer total = orderDao.countMatchingOrderForTrip(listQueryParametersDto, trip);

    return total;
  }

  @Override
  public Map<String, BigDecimal> calculateOrderEachAmount(Order order) {
    // Init Variable
    Map<String, BigDecimal> orderEachAmountMap = new HashMap<>();
    BigDecimal orderItemUnitPrice = order.getOrderItem().getUnitPrice();
    BigDecimal orderItemQuantity = BigDecimal.valueOf(order.getOrderItem().getQuantity());
    BigDecimal orderPlatformFeePercent = BigDecimal.valueOf(order.getPlatformFeePercent())
        .multiply(BigDecimal.valueOf(0.01));
    BigDecimal orderTariffFeePercent = BigDecimal.valueOf(order.getTariffFeePercent())
        .multiply(BigDecimal.valueOf(0.01));

    // Calculation
    BigDecimal itemTotalAmount = orderItemUnitPrice.multiply(orderItemQuantity); // 1
    BigDecimal tariffFee = itemTotalAmount.multiply(orderTariffFeePercent); // 2
    BigDecimal platformFee = itemTotalAmount.multiply(orderPlatformFeePercent); // 3
    BigDecimal travelerFee = order.getTravelerFee(); // 4

    BigDecimal orderTotalAmount = itemTotalAmount.add(tariffFee).add(platformFee).add(travelerFee); // total = 1 + 2 + 3
                                                                                                    // + 4

    // Set only the second digit after the decimal point (Banker 's rounding)
    CurrencyEnum currency = order.getCurrency();
    int decimalScale = CurrencyUtil.getDecimalScale(currency);

    tariffFee = tariffFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    platformFee = platformFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    orderTotalAmount = orderTotalAmount.setScale(decimalScale, RoundingMode.HALF_EVEN);

    // Set return value
    orderEachAmountMap.put("tariffFee", tariffFee);
    orderEachAmountMap.put("platformFee", platformFee);
    orderEachAmountMap.put("orderTotalAmount", orderTotalAmount);

    return orderEachAmountMap;
  }

  @Override
  public CalculateOrderAmountResponseDto calculateOrderEachAmountDuringCreation(CreateOrderRequestDto createOrderRequestDto) {
    createOrderRequestDto.setPlatformFeePercent(PLATFORM_FEE_PERCENT);
    createOrderRequestDto.setTariffFeePercent(TARIFF_FEE_PERCENT);



    // Init Variable
    Map<String, BigDecimal> orderEachAmountMap = new HashMap<>();
    BigDecimal orderItemUnitPrice = createOrderRequestDto.getCreateOrderItemDto().getUnitPrice();
    BigDecimal orderItemQuantity = BigDecimal.valueOf(createOrderRequestDto.getCreateOrderItemDto().getQuantity());
    BigDecimal orderPlatformFeePercent = BigDecimal.valueOf(createOrderRequestDto.getPlatformFeePercent())
        .multiply(BigDecimal.valueOf(0.01));
    BigDecimal orderTariffFeePercent = BigDecimal.valueOf(createOrderRequestDto.getTariffFeePercent())
        .multiply(BigDecimal.valueOf(0.01));

    // Calculation
    BigDecimal itemTotalAmount = orderItemUnitPrice.multiply(orderItemQuantity); // 1
    BigDecimal tariffFee = itemTotalAmount.multiply(orderTariffFeePercent); // 2
    BigDecimal platformFee = itemTotalAmount.multiply(orderPlatformFeePercent); // 3
    BigDecimal travelerFee = createOrderRequestDto.getTravelerFee(); // 4

    BigDecimal orderTotalAmount = itemTotalAmount.add(tariffFee).add(platformFee).add(travelerFee); // total = 1 + 2 + 3
    // + 4

    // Set only the second digit after the decimal point (Banker 's rounding)
    CurrencyEnum currency = createOrderRequestDto.getCurrency();
    int decimalScale = CurrencyUtil.getDecimalScale(currency);

    tariffFee = tariffFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    platformFee = platformFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    orderTotalAmount = orderTotalAmount.setScale(decimalScale, RoundingMode.HALF_EVEN);

    // Set return value
    orderEachAmountMap.put("tariffFee", tariffFee);
    orderEachAmountMap.put("platformFee", platformFee);
    orderEachAmountMap.put("orderTotalAmount", orderTotalAmount);

    // Set value to DTO
    CalculateOrderAmountResponseDto calculateOrderAmountResponseDto = new CalculateOrderAmountResponseDto();
    calculateOrderAmountResponseDto.setTariffFee(orderEachAmountMap.get("tariffFee"));
    calculateOrderAmountResponseDto.setPlatformFee(orderEachAmountMap.get("platformFee"));
    calculateOrderAmountResponseDto.setTotalAmount(orderEachAmountMap.get("orderTotalAmount"));
    calculateOrderAmountResponseDto.setTravelerFee(createOrderRequestDto.getTravelerFee());
    calculateOrderAmountResponseDto.setCurrency(createOrderRequestDto.getCurrency());

    return calculateOrderAmountResponseDto;
  }

  @Override
  public String generateOrderSerialNumber(CreateOrderRequestDto createOrderRequestDto) {
    LocalDate now = LocalDate.now();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMdd");
    String datePart = dateTimeFormatter.format(now);
    String destinationCityCode = createOrderRequestDto.getDestinationCity().name();
    String randomAlphaNumericPart = generateRandomAlphaNumeric();

    String serialNumber = datePart + destinationCityCode + randomAlphaNumericPart;
    return serialNumber;
  }



  @Override
  public CancellationRecord requestCancelOrder (Order order,
      CreateCancellationRecordRequestDto createCancellationRecordRequestDto) throws BadRequestException {
    if (createCancellationRecordRequestDto.getCancelReason().equals(CancelReasonCategoryEnum.OTHER)) {
      if (createCancellationRecordRequestDto.getNote() == null) {
        throw new BadRequestException("Note should not be Null.");
      }
    }

    CancellationRecord cancellationReocrdByOrderId = cancellationRecordDao.getCancellationRecordByOrderId(
        createCancellationRecordRequestDto.getOrderId());

    if (cancellationReocrdByOrderId != null) {
      throw new DuplicateKeyException("This order has been requested canceled");
    }

    if (!order.getOrderStatus().equals(TO_BE_PURCHASED)) {
      throw new AccessDeniedException("OrderStatus is not TO_BE_PURCHASED, so you have no permission to request a cancellation.");
    }

    String cancellationRecordId = uuidGenerator.getUuid();
    createCancellationRecordRequestDto.setCancellationRecordId(cancellationRecordId);
    cancellationRecordDao.createCancellationRecord(createCancellationRecordRequestDto);

    CancellationRecord cancellationRecord = cancellationRecordDao.getCancellationRecordById(cancellationRecordId);
    if (cancellationRecord == null) {
      throw new ResourceNotFoundException("Create data not found");
    }

    // Change OrderStatus to TO_BE_CANCELED
    UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
        .orderStatus(TO_BE_CANCELED)
        .build();

    updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);


    // TODO 信件通知對方要回覆是否取消訂單



    return cancellationRecord;
  }

  @Override
  public CancellationRecord getCancellationRecordById(String cancellationRecordId) {
    CancellationRecord cancellationRecord = cancellationRecordDao.getCancellationRecordById(cancellationRecordId);
    return cancellationRecord;
  }

  @Override
  public CancellationRecord getCancellationRecordByOrderId(String orderId) {
    CancellationRecord cancellationRecord = cancellationRecordDao.getCancellationRecordByOrderId(orderId);
    return cancellationRecord;
  }

  @Override
  @Transactional
  public PostponeRecord requestPostponeOrder(Order order,
      CreatePostponeRecordRequestDto createPostponeRecordRequestDto) {

    if (createPostponeRecordRequestDto.getPostponeReason().equals(CancelReasonCategoryEnum.OTHER)) {
      if (createPostponeRecordRequestDto.getNote() == null) {
        throw new BadRequestException("Note should not be Null.");
      }
    }

    PostponeRecord postponeRecordByOrderId = postponeRecordDao.getPostponeRecordByOrderId(
        createPostponeRecordRequestDto.getOrderId());
    if (postponeRecordByOrderId != null) {
      throw new DuplicateKeyException("This order has been requested postponed");
    }

    if (!(order.getOrderStatus().equals(TO_BE_PURCHASED) || order.getOrderStatus().equals(
        TO_BE_DELIVERED))) {
      throw new AccessDeniedException("OrderStatus is not TO_BE_PURCHASED or TO_BE_DELIVERED, so you have no permission to request a cancellation.");
    }


    // Create postponeRecord
    String postponeRecordId = uuidGenerator.getUuid();
    OrderStatusEnum originalOrderStatus = order.getOrderStatus();
    createPostponeRecordRequestDto.setPostponeRecordId(postponeRecordId);
    createPostponeRecordRequestDto.setOriginalOrderStatus(originalOrderStatus);
    postponeRecordDao.createPostponeRecord(createPostponeRecordRequestDto);

    PostponeRecord postponeRecord = postponeRecordDao.getPostponeRecordById(postponeRecordId);

    if (postponeRecord == null) {
      throw new ResourceNotFoundException("Create data not found");
    }

    // Change OrderStatus to TO_BE_POSTPONED
    UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
        .orderStatus(TO_BE_POSTPONED)
        .build();

    updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);


    return postponeRecord;
  }

  @Override
  @Transactional
  public void replyPostponeOrder(Order order,
      UpdatePostponeRecordRequestDto updatePostponeRecordRequestDto) {

    // Check orderStatus is TO_BE_POSTPONED
    if (!(order.getOrderStatus().equals(TO_BE_POSTPONED))) {
      throw new AccessDeniedException("OrderStatus is not TO_BE_POSTPONED, so you have no permission to request a cancellation.");
    }


    // Get originalOrderStatus then set originalOrderStatus to Order
    OrderStatusEnum originalOrderStatus = getPostponeRecordByOrderId(order.getOrderId()).getOriginalOrderStatus();

    if (updatePostponeRecordRequestDto.getIsPostponed() == true) {

      // Plus 7 days to latestReceiveItemDate
      Date latestReceiveItemDate = order.getLatestReceiveItemDate();
      LocalDate latestReceiveItemDateLocal = latestReceiveItemDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      LocalDate updatedLatestReceiveItemDateLocal = latestReceiveItemDateLocal.plusDays(7);
      Date updatedLatestReceiveItemDate = Date.from(updatedLatestReceiveItemDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());



      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
          .orderStatus(originalOrderStatus)
          .latestReceiveItemDate(updatedLatestReceiveItemDate)
          .build();

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);
      postponeRecordDao.updatePostponeRecord(updatePostponeRecordRequestDto);
    } else {
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
          .orderStatus(originalOrderStatus)
          .build();

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);
      postponeRecordDao.updatePostponeRecord(updatePostponeRecordRequestDto);
    }


    postponeRecordDao.updatePostponeRecord(updatePostponeRecordRequestDto);
  }

  @Override
  public PostponeRecord getPostponeRecordByOrderId(String orderId) {
    PostponeRecord postponeRecord = postponeRecordDao.getPostponeRecordByOrderId(orderId);
    return postponeRecord;
  }

  @Override
  @Transactional
  public void replyCancelOrder(
      Order order, UpdateCancellationRecordRequestDto updateCancellationRecordRequestDto) {
    // Check orderStatus is TO_BE_CANCELED
    if (!(order.getOrderStatus().equals(TO_BE_CANCELED))) {
      throw new AccessDeniedException("OrderStatus is not TO_BE_CANCELED, so you have no permission to request a cancellation.");
    }


    if (updateCancellationRecordRequestDto.getIsCanceled() == true) {
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
          .orderStatus(CANCELED)
          .build();

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);
      cancellationRecordDao.updateCancellationRecord(updateCancellationRecordRequestDto);
    } else {
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
          .orderStatus(TO_BE_PURCHASED)
          .build();

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);
      cancellationRecordDao.updateCancellationRecord(updateCancellationRecordRequestDto);
    }

    // TODO 信件通知對方自己的答覆

  }

  public String generateRandomAlphaNumeric() {
    Random random = new Random();
    StringBuilder randomPart = new StringBuilder(5);

    for (int i = 0; i < 5; i++) {
      int randomCharOrDigit = random.nextInt(36);
      if (randomCharOrDigit < 10) {
        randomPart.append((char) (randomCharOrDigit + '0'));
      } else {
        randomPart.append((char) (randomCharOrDigit - 10 + 'A'));
      }
    }
    return randomPart.toString();
  }

  private OrderChosenShopperDto getOrderChosenShopper(Order order) {
    Bid chosenBid = bidService.getChosenBidByOrderId(order.getOrderId());
    if (chosenBid == null) {
      return null; // TODO 需要防呆，避免非REQUESTED order找不到對應的chosenBid;
    }
    BidResponseDto chosenBidResponseDto = bidService.getBidResponseById(chosenBid.getBidId());
    BidCreatorDto shopper = chosenBidResponseDto.getCreator();

    OrderChosenShopperDto orderChosenShopperDto = modelMapper.map(shopper, OrderChosenShopperDto.class);

    // Convert Date to LocalDate
    Date latestDeliveryDate = chosenBid.getLatestDeliveryDate();
    LocalDate latestDeliveryLocalDate = latestDeliveryDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();

    orderChosenShopperDto.setLatestDeliveryDate(latestDeliveryLocalDate);

    return orderChosenShopperDto;

  }

  // @Override
  // public void updateOrder(UpdateOrderRequestDto updateOrderRequestDto) {
  // orderDao.updateOrder(updateOrderRequestDto);
  // }


  private boolean isCurrentLoginUserPlaceBid(String orderId) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    if (currentLoginUserId == null) {
      return false;
    }

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .orderId(orderId)
        .startIndex(0)
        .size(999)
        .orderBy("create_date")
        .sort("DESC")
        .build();

    List<BidResponseDto> bidResponseDtoList = bidService.getBidResponseDtoList(listQueryParametersDto);
    boolean isCurrentLoginUserPlaceBid = bidResponseDtoList.stream()
        .anyMatch(bidResponseDto -> bidResponseDto.getCreator().getUserId().equals(currentLoginUserId));

    return isCurrentLoginUserPlaceBid;
  }

  private boolean isValidOrderStatusTransition(OrderStatusEnum oldOrderStatus, OrderStatusEnum newOrderStatus) {
    switch (oldOrderStatus) {
      case REQUESTED:
        return newOrderStatus == TO_BE_PURCHASED;
      case TO_BE_PURCHASED:
        return newOrderStatus == TO_BE_DELIVERED || newOrderStatus == TO_BE_CANCELED || newOrderStatus == TO_BE_POSTPONED;
      case TO_BE_DELIVERED:
        return newOrderStatus == DELIVERED || newOrderStatus == TO_BE_POSTPONED;
      case DELIVERED:
        return newOrderStatus == FINISHED;
      case FINISHED:
        return false;
      case CANCELED:
        return false;
      case TO_BE_CANCELED:
        return newOrderStatus == CANCELED || newOrderStatus == TO_BE_PURCHASED;
      case TO_BE_POSTPONED:
        return newOrderStatus == TO_BE_PURCHASED || newOrderStatus == TO_BE_DELIVERED;
      default:
        throw new IllegalStatusTransitionException("Unexpected current status: " + oldOrderStatus);
    }
  }
}
