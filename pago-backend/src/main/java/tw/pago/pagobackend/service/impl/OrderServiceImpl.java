package tw.pago.pagobackend.service.impl;

import static tw.pago.pagobackend.constant.OrderStatusEnum.CANCELLED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.DELIVERED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.FINISHED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.REQUESTED;
import static tw.pago.pagobackend.constant.OrderStatusEnum.TO_BE_CANCELLED;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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
import tw.pago.pagobackend.constant.PostponeReasonCategoryEnum;
import tw.pago.pagobackend.dao.CancellationRecordDao;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dao.PostponeRecordDao;
import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.BidCreatorDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CalculateOrderAmountRequestDto;
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
  private static final BigDecimal PERCENT_TO_DECIMAL = BigDecimal.valueOf(0.01);

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
      PostponeRecordDao postponeRecordDao
      ) {
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
    createOrderRequestDto.setOrderStatus(REQUESTED);

    orderDao.createOrderItem(createOrderRequestDto);
    orderDao.createOrder(userId, createOrderRequestDto);

    // Check if files are not null and not empty
    if (files != null && !files.isEmpty()) {
      System.out.println("file not null");
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
    }

    Order order = getOrderById(orderUuid);

    return order;
  }

  @Override
  public Order getOrderById(String orderId) {
    Order order = orderDao.getOrderById(orderId);
    OrderStatusEnum orderStatus = order.getOrderStatus();

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
    if (!orderStatus.equals(REQUESTED)) {
      // Get the shopper for the order
      OrderChosenShopperDto orderChosenShopperDto = getOrderChosenShopper(order);

      // Set shopper to order
      order.setShopper(orderChosenShopperDto);
    }


    // If the orderStatus is TO_BE_CANCELLED or TO_BE_POSTPONED, check is loginUser is Applicant
    if (orderStatus.equals(TO_BE_CANCELLED) || orderStatus.equals(TO_BE_POSTPONED)) {
      order.setIsApplicant(isCurrentLoginUserApplicant(order));
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

    if (!order.getOrderStatus().equals(REQUESTED)) {
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
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto, boolean sendStatusUpdateEmail) {

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

    // If the order status has been changed AND status update email should be sent, send the email notification
    if (orderStatusChanged && sendStatusUpdateEmail) {
      sendOrderUpdateEmail(oldOrder, updateOrderAndOrderItemRequestDto);
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

      if (!order.getOrderStatus().equals(REQUESTED)) {
        // Get the shopper for the order
        OrderChosenShopperDto orderChosenShopperDto = getOrderChosenShopper(order);

        // Set shopper to order
        order.setShopper(orderChosenShopperDto);
      }

      // If the orderStatus is TO_BE_CANCELLED or TO_BE_POSTPONED, check is loginUser is Applicant
      if (order.getOrderStatus().equals(TO_BE_CANCELLED) || order.getOrderStatus().equals(TO_BE_POSTPONED)) {
        order.setIsApplicant(isCurrentLoginUserApplicant(order));
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

      if (!order.getOrderStatus().equals(REQUESTED)) {
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
  public List<MatchingShopperResponseDto> getMatchingShopperList(Order order, ListQueryParametersDto listQueryParametersDto) {

    List<Trip> matchingTripList = tripDao.getMatchingTripListForOrder(listQueryParametersDto);

    // Use a Map to collect all matching trips for each user
    Map<String, List<Trip>> userIdToTripsMap = matchingTripList.stream()
        .collect(Collectors.groupingBy(Trip::getShopperId));

    List<MatchingShopperResponseDto> matchingShopperResponseDtoList = new ArrayList<>();
    for (Map.Entry<String, List<Trip>> entry : userIdToTripsMap.entrySet()) {
      String userId = entry.getKey();
      User user = userDao.getUserById(userId);
      MatchingShopperResponseDto matchingShopperResponseDto = modelMapper.map(user, MatchingShopperResponseDto.class);

      // Convert the Trip list in the entry to a MatchingTripForOrderDto list
      List<MatchingTripForOrderDto> matchingTripListForOrder = entry.getValue().stream()
          .map(trip -> modelMapper.map(trip, MatchingTripForOrderDto.class))
          .collect(Collectors.toList());

      // Set the matchingTripListForOrder as the trips attribute of the matchingShopperResponseDto
      matchingShopperResponseDto.setTrips(matchingTripListForOrder);


        // Add the matchingShopperResponseDto to the matchingShopperResponseDtoList
        matchingShopperResponseDtoList.add(matchingShopperResponseDto);

    }

    return matchingShopperResponseDtoList;
  }



  @Transactional
  @Override
  public void deleteOrderById(String orderId) {

    Order order = orderDao.getOrderById(orderId);
    String orderItemId = order.getOrderItemId();

    // Check whether the orderStatus is REQUESTED
    if (!order.getOrderStatus().equals(REQUESTED)) {
      throw new AccessDeniedException("Order: "+ orderId +", OrderStatus is not REQUESTED, you have no permission to delete this order");
    }

    // Delete order and orderItem
    orderDao.deleteOrderItemById(orderItemId);
    orderDao.deleteOrderById(orderId);

    // delete file
    String objectType = "order";
    fileService.deleteFilesByObjectIdnType(orderId, objectType);
  }

  @Override
  @Transactional
  public void deleteOrderByOrder(Order order) {
    String orderId = order.getOrderId();
    String orderItemId = order.getOrderItemId();

    // Check whether the orderStatus is REQUESTED
    if (!order.getOrderStatus().equals(REQUESTED)) {
      throw new AccessDeniedException("Order: "+ orderId +", OrderStatus is not REQUESTED, you have no permission to delete this order");
    }

    // Delete order and orderItem
    orderDao.deleteOrderItemById(orderItemId);
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
    // Check for null values
    if (order == null) {
      throw new IllegalArgumentException("Order cannot be null");
    }

    if (order.getOrderItem() == null) {
      throw new IllegalArgumentException("Order item cannot be null");
    }

    // Initialize variables
    Map<String, BigDecimal> orderEachAmountMap = new HashMap<>();
    BigDecimal orderItemUnitPrice = order.getOrderItem().getUnitPrice();
    BigDecimal orderItemQuantity = BigDecimal.valueOf(order.getOrderItem().getQuantity());
    BigDecimal orderPlatformFeePercent = BigDecimal.valueOf(order.getPlatformFeePercent())
        .multiply(PERCENT_TO_DECIMAL);
    BigDecimal orderTariffFeePercent = BigDecimal.valueOf(order.getTariffFeePercent())
        .multiply(PERCENT_TO_DECIMAL);

    // Check for invalid values
    if (orderItemUnitPrice == null || orderItemUnitPrice.compareTo(BigDecimal.ZERO) <= 0 ||
        orderItemQuantity.compareTo(BigDecimal.ZERO) <= 0 ||
        orderPlatformFeePercent.compareTo(BigDecimal.ZERO) <= 0 ||
        orderTariffFeePercent.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Values must be non-null and greater than 0");
    }

    // Calculate fees and total amount
    BigDecimal itemTotalAmount = orderItemUnitPrice.multiply(orderItemQuantity);
    BigDecimal tariffFee = itemTotalAmount.multiply(orderTariffFeePercent);
    BigDecimal platformFee = itemTotalAmount.multiply(orderPlatformFeePercent);
    BigDecimal travelerFee = order.getTravelerFee();

    // Set minimum fee amounts
    if (tariffFee.compareTo(BigDecimal.TEN) < 0) {
      tariffFee = BigDecimal.TEN;
    }

    if (platformFee.compareTo(BigDecimal.TEN) < 0) {
      platformFee = BigDecimal.TEN;
    }

    BigDecimal orderTotalAmount = itemTotalAmount.add(tariffFee).add(platformFee).add(travelerFee);

    // Apply rounding to the second digit after the decimal point (Banker's rounding)
    CurrencyEnum currency = order.getCurrency();
    int decimalScale = CurrencyUtil.getDecimalScale(currency);

    tariffFee = tariffFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    platformFee = platformFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    orderTotalAmount = orderTotalAmount.setScale(decimalScale, RoundingMode.HALF_EVEN);

    // Set return values
    orderEachAmountMap.put("tariffFee", tariffFee);
    orderEachAmountMap.put("platformFee", platformFee);
    orderEachAmountMap.put("orderTotalAmount", orderTotalAmount);

    return orderEachAmountMap;
  }



  @Override
  public CalculateOrderAmountResponseDto calculateOrderEachAmountDuringCreation(
      CalculateOrderAmountRequestDto calculateOrderAmountRequestDto) {

    // Set platform and tariff fee percentages
    calculateOrderAmountRequestDto.setPlatformFeePercent(PLATFORM_FEE_PERCENT);
    calculateOrderAmountRequestDto.setTariffFeePercent(TARIFF_FEE_PERCENT);

    // Initialize variables
    BigDecimal orderItemUnitPrice = calculateOrderAmountRequestDto.getCreateOrderItemDto().getUnitPrice();
    BigDecimal orderItemQuantity = BigDecimal.valueOf(calculateOrderAmountRequestDto.getCreateOrderItemDto().getQuantity());
    BigDecimal orderPlatformFeePercent = BigDecimal.valueOf(calculateOrderAmountRequestDto.getPlatformFeePercent())
        .multiply(PERCENT_TO_DECIMAL);
    BigDecimal orderTariffFeePercent = BigDecimal.valueOf(calculateOrderAmountRequestDto.getTariffFeePercent())
        .multiply(PERCENT_TO_DECIMAL);

    // Check for invalid values
    if (orderItemUnitPrice == null || orderItemUnitPrice.compareTo(BigDecimal.ZERO) <= 0 ||
        orderItemQuantity.compareTo(BigDecimal.ZERO) <= 0 ||
        orderPlatformFeePercent.compareTo(BigDecimal.ZERO) <= 0 ||
        orderTariffFeePercent.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Values must be non-null and greater than 0");
    }



    // Calculation
    BigDecimal itemTotalAmount = orderItemUnitPrice.multiply(orderItemQuantity);
    BigDecimal tariffFee = itemTotalAmount.multiply(orderTariffFeePercent);
    BigDecimal platformFee = itemTotalAmount.multiply(orderPlatformFeePercent);
    BigDecimal travelerFee = calculateOrderAmountRequestDto.getTravelerFee();

    // Set minimum fee amounts
    if (tariffFee.compareTo(BigDecimal.TEN) < 0) {
      tariffFee = BigDecimal.TEN;
    }

    if (platformFee.compareTo(BigDecimal.TEN) < 0) {
      platformFee = BigDecimal.TEN;
    }

    BigDecimal orderTotalAmount = itemTotalAmount.add(tariffFee).add(platformFee).add(travelerFee);

    // Apply rounding to the second digit after the decimal point (Banker's rounding)
    CurrencyEnum currency = calculateOrderAmountRequestDto.getCurrency();
    int decimalScale = CurrencyUtil.getDecimalScale(currency);

    tariffFee = tariffFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    platformFee = platformFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    orderTotalAmount = orderTotalAmount.setScale(decimalScale, RoundingMode.HALF_EVEN);


    // Set values to the response DTO
    CalculateOrderAmountResponseDto calculateOrderAmountResponseDto = new CalculateOrderAmountResponseDto();
    calculateOrderAmountResponseDto.setTariffFee(tariffFee);
    calculateOrderAmountResponseDto.setPlatformFee(platformFee);
    calculateOrderAmountResponseDto.setTotalAmount(orderTotalAmount);
    calculateOrderAmountResponseDto.setTravelerFee(travelerFee);
    calculateOrderAmountResponseDto.setCurrency(currency);

    return calculateOrderAmountResponseDto;
  }


  @Override
  public CalculateOrderAmountResponseDto calculateOrderEachAmountDuringChooseBid(String bidId) {
    // Retrieve necessary data
    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);
    Order order = getOrderById(bidResponseDto.getOrderId());
    OrderItem orderItem = order.getOrderItem();


    // Initialize variables
    Map<String, BigDecimal> orderEachAmountMap = new HashMap<>();
    BigDecimal orderItemUnitPrice = orderItem.getUnitPrice();
    BigDecimal orderItemQuantity = BigDecimal.valueOf(orderItem.getQuantity());
    BigDecimal orderPlatformFeePercent = BigDecimal.valueOf(order.getPlatformFeePercent())
        .multiply(PERCENT_TO_DECIMAL);
    BigDecimal orderTariffFeePercent = BigDecimal.valueOf(order.getTariffFeePercent())
        .multiply(PERCENT_TO_DECIMAL);

    // Check for invalid values
    if (orderItemUnitPrice == null || orderItemUnitPrice.compareTo(BigDecimal.ZERO) <= 0 ||
        orderItemQuantity.compareTo(BigDecimal.ZERO) <= 0 ||
        orderPlatformFeePercent.compareTo(BigDecimal.ZERO) <= 0 ||
        orderTariffFeePercent.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Values must be non-null and greater than 0");
    }

    // Calculation
    BigDecimal itemTotalAmount = orderItemUnitPrice.multiply(orderItemQuantity);
    BigDecimal tariffFee = itemTotalAmount.multiply(orderTariffFeePercent);
    BigDecimal platformFee = itemTotalAmount.multiply(orderPlatformFeePercent);
    BigDecimal travelerFee = bidResponseDto.getBidAmount();

    // Set minimum fee amounts
    if (tariffFee.compareTo(BigDecimal.TEN) < 0) {
      tariffFee = BigDecimal.TEN;
    }

    if (platformFee.compareTo(BigDecimal.TEN) < 0) {
      platformFee = BigDecimal.TEN;
    }

    BigDecimal orderTotalAmount = itemTotalAmount.add(tariffFee).add(platformFee).add(travelerFee);

    // Apply rounding
    CurrencyEnum currency = order.getCurrency();
    int decimalScale = CurrencyUtil.getDecimalScale(currency);

    tariffFee = tariffFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    platformFee = platformFee.setScale(decimalScale, RoundingMode.HALF_EVEN);
    orderTotalAmount = orderTotalAmount.setScale(decimalScale, RoundingMode.HALF_EVEN);

    // Prepare response
    CalculateOrderAmountResponseDto calculateOrderAmountResponseDto = new CalculateOrderAmountResponseDto();
    calculateOrderAmountResponseDto.setTariffFee(tariffFee);
    calculateOrderAmountResponseDto.setPlatformFee(platformFee);
    calculateOrderAmountResponseDto.setTotalAmount(orderTotalAmount);
    calculateOrderAmountResponseDto.setTravelerFee(travelerFee);
    calculateOrderAmountResponseDto.setCurrency(order.getCurrency());
    calculateOrderAmountResponseDto.setBidder(bidResponseDto.getCreator());

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
      throw new DuplicateKeyException("This order has been requested cancelled");
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

    // Change OrderStatus to TO_BE_CANCELLED
    UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
        .orderStatus(TO_BE_CANCELLED)
        .build();

    // Pass false to prevent the order from being updated again
    updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, false);

    // Send email to notify the other party to reply
    sendCancelRequestEmail(order, cancellationRecord);

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

    if (createPostponeRecordRequestDto.getPostponeReason().equals(PostponeReasonCategoryEnum.OTHER)) {
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

    // Pass false to avoid sending email
    updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, false);

    // Send email to notify the other party to reply
    sendPostponeRequestEmail(order, postponeRecord);

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

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, false);
      postponeRecordDao.updatePostponeRecord(updatePostponeRecordRequestDto);
    } else {
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
          .orderStatus(originalOrderStatus)
          .build();

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, false);
      postponeRecordDao.updatePostponeRecord(updatePostponeRecordRequestDto);
    }

    PostponeRecord postponeRecord = getPostponeRecordByOrderId(order.getOrderId());

    // Send email to notify the other party about the reply
    String updatedOrderStatus = originalOrderStatus.getDescription();
    sendReplyPostponeOrderEmail(order, updatePostponeRecordRequestDto, postponeRecord, updatedOrderStatus);
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
    String updatedOrderStatus;
    // Check orderStatus is TO_BE_CANCELLED
    if (!(order.getOrderStatus().equals(TO_BE_CANCELLED))) {
      throw new AccessDeniedException("OrderStatus is not TO_BE_CANCELLED, so you have no permission to request a cancellation.");
    }


    if (updateCancellationRecordRequestDto.getIsCancelled() == true) {
      OrderStatusEnum orderStatus = CANCELLED;
      updatedOrderStatus = orderStatus.getDescription();
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
          .orderStatus(orderStatus)
          .build();

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, false);
      cancellationRecordDao.updateCancellationRecord(updateCancellationRecordRequestDto);
    } else {
      OrderStatusEnum orderStatus = TO_BE_PURCHASED;
      updatedOrderStatus = orderStatus.getDescription();
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
          .orderStatus(orderStatus)
          .build();

      updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, false);
      cancellationRecordDao.updateCancellationRecord(updateCancellationRecordRequestDto);
    }

    CancellationRecord cancellationRecord = getCancellationRecordByOrderId(order.getOrderId());
    // Send email to notify the other party about the reply
    sendReplyCancelOrderEmail(order, updateCancellationRecordRequestDto, cancellationRecord, updatedOrderStatus);

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
      return null;
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
        return newOrderStatus == TO_BE_DELIVERED || newOrderStatus == TO_BE_CANCELLED || newOrderStatus == TO_BE_POSTPONED;
      case TO_BE_DELIVERED:
        return newOrderStatus == DELIVERED || newOrderStatus == TO_BE_POSTPONED;
      case DELIVERED:
        return newOrderStatus == FINISHED;
      case FINISHED:
        return false;
      case CANCELLED:
        return false;
      case TO_BE_CANCELLED:
        return newOrderStatus == CANCELLED || newOrderStatus == TO_BE_PURCHASED;
      case TO_BE_POSTPONED:
        return newOrderStatus == TO_BE_PURCHASED || newOrderStatus == TO_BE_DELIVERED;
      default:
        throw new IllegalStatusTransitionException("Unexpected current status: " + oldOrderStatus);
    }
  }


  private boolean isCurrentLoginUserApplicant(Order order) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    if (currentLoginUserId == null) {
      return false;
    }

    switch (order.getOrderStatus()) {
      case TO_BE_CANCELLED:
        CancellationRecord cancellationRecord = cancellationRecordDao.getCancellationRecordByOrderId(order.getOrderId());
        return cancellationRecord.getUserId().equals(currentLoginUserId);
      case TO_BE_POSTPONED:
        PostponeRecord postponeRecord = postponeRecordDao.getPostponeRecordByOrderId(order.getOrderId());
        return postponeRecord.getUserId().equals(currentLoginUserId);
      default:
        return false;
    }
  }

  public void sendPostponeRequestEmail(Order order, PostponeRecord postponeRecord) {
    String contentTitle = "訂單延期申請通知";
    String postponingUserId = postponeRecord.getUserId();
    String orderCreatorId = order.getConsumerId();
    String orderId = order.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", orderId);
    String serialNumber = order.getSerialNumber();
    String recipientId;
  
    // Check if the postponing user is the order creator or the bidder
    if (postponingUserId.equals(orderCreatorId)) {
      // If the postponing user is the order creator, send the email to the bidder
      recipientId = order.getShopper().getUserId();
    } else {
      // If the postponing user is the bidder, send the email to the order creator
      recipientId = orderCreatorId;
    }
  
    // Get postpone requester user information
    User postponeRequesterUser = userDao.getUserById(postponingUserId);
    String postponeRequesterFirstName = postponeRequesterUser.getFirstName();
    
    // Retrieve recipient user information (email)
    User recipientUser = userDao.getUserById(recipientId);
    // Get the recipient email
    String recipientUserEmail = recipientUser.getEmail();
    // Get the order item name
    String orderItemName = order.getOrderItem().getName();
    // Get the user name
    String username = recipientUser.getFirstName();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String postponeNote = postponeRecord.getNote();
    String noteLine = (postponeNote != null) ? String.format("延期原因描述：%s", postponeNote) : "";
    
    String emailBody = String.format("於 %s ， %s 想要為「<a href=\"%s\"  target=\"_blank\">%s</a>」的訂單申請延期，請至 Pago 網站確認並回覆。<br><br>" +
        "以下為 %s 申請延期之詳細資訊：<br>" +
        "延期原因：%s<br>%s<br><br>訂單編號：%s",
        date, postponeRequesterFirstName, orderUrl, orderItemName, postponeRequesterFirstName, postponeRecord.getPostponeReason().getDescription(), noteLine, serialNumber);

    // Prepare the email content
    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(recipientUserEmail);
    emailRequest.setSubject("【Pago " + contentTitle + "】" + orderItemName);
    emailRequest.setBody(emailBody);
    emailRequest.setContentTitle(contentTitle);
    emailRequest.setRecipientName(username);

    // Send the email notification
    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent! (postponeRequest)");
  }

  public void sendCancelRequestEmail(Order order, CancellationRecord cancellationRecord) {
    String contentTitle = "訂單取消申請通知";
    String cancellingUserId = cancellationRecord.getUserId();
    String orderId = order.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", orderId);
    String serialNumber = order.getSerialNumber();
    String orderCreatorId = order.getConsumerId();
    String recipientId;
  
    // Check if the cancelling user is the order creator or the bidder
    if (cancellingUserId.equals(orderCreatorId)) {
      // If the cancelling user is the order creator, send the email to the bidder
      recipientId = order.getShopper().getUserId();
    } else {
      // If the cancelling user is the bidder, send the email to the order creator
      recipientId = orderCreatorId;
    }
  
    // Get cancel requester user information
    User cancelRequesterUser = userDao.getUserById(cancellingUserId);
    String cancelRequesterFirstName = cancelRequesterUser.getFirstName();
    
    // Retrieve recipient user information (email)
    User recipientUser = userDao.getUserById(recipientId);
    // Get the recipient email
    String recipientUserEmail = recipientUser.getEmail();
    // Get the order item name
    String orderItemName = order.getOrderItem().getName();
    // Get the user name
    String username = recipientUser.getFirstName();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String cancelNote = cancellationRecord.getNote();
    String noteLine = (cancelNote != null) ? String.format("取消原因描述：%s", cancelNote) : "";
    
    String emailBody = String.format("於 %s ， %s 想要將「<a href=\"$s\" target=\"_blank\">%s</a>」的訂單取消，請至 Pago 網站確認並回覆。<br><br>" +
        "以下為 %s 申請取消之詳細資訊：<br>" + "取消原因：%s<br>%s<br><br>訂單編號：",
        date, cancelRequesterFirstName, orderUrl, orderItemName, cancelRequesterFirstName, cancellationRecord.getCancelReason().getDescription(), noteLine, serialNumber);

    // Prepare the email content
    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(recipientUserEmail);
    emailRequest.setSubject("【Pago " + contentTitle + "】" + orderItemName);
    emailRequest.setBody(emailBody);
    emailRequest.setContentTitle(contentTitle);
    emailRequest.setRecipientName(username);

    // Send the email notification
    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent! (cancelRequest)");
  }

  public void sendOrderUpdateEmail(Order oldOrder, UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {
    String contentTitle = "訂單更新通知";
    String consumerId = oldOrder.getConsumerId();
    String oldOrderId = oldOrder.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", oldOrderId);
    User consumer = userDao.getUserById(consumerId);
  
    // Get the current login user's email
    String currentLoginUserEmail = consumer.getEmail();
    // Get the order item name
    String orderItemName = updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getName();
    // Get the user name
    String username = consumer.getFirstName();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String emailBody = String.format("您的訂單 「<a href=\"%s\" target=\"_blank\">%s</a>」 ，已於 %s 更新為「<b>%s</b>」<br><br>", orderItemName, date,
        updateOrderAndOrderItemRequestDto.getOrderStatus().getDescription());

    // Prepare the email content
    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(currentLoginUserEmail);
    emailRequest.setSubject("【Pago " + contentTitle + "】" + orderItemName);
    emailRequest.setBody(emailBody);
    emailRequest.setContentTitle(contentTitle);
    emailRequest.setRecipientName(username);

    // Send the email notification
    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent! (orderUpdate)");
  }

  public void sendReplyPostponeOrderEmail(Order order, UpdatePostponeRecordRequestDto updatePostponeRecordRequestDto, 
      PostponeRecord postponeRecord, String updatedOrderStatus) {
    String contentTitle = "訂單延期申請通知";
    String recipientId = postponeRecord.getUserId();
    User recipientUser = userDao.getUserById(recipientId);
    String recipientUserEmail = recipientUser.getEmail();
    String orderId = order.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", orderId);
    String serialNumber = order.getSerialNumber();
    String orderItemName = order.getOrderItem().getName();
    String username = recipientUser.getFirstName();
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);
    String result = updatePostponeRecordRequestDto.getIsPostponed() ? "通過" : "被拒絕";
  
    String emailBody = String.format("於 %s，「<a href=\"%s\" target=\"_blank\">%s</a>」的訂單延期申請已 <b>%s<b>。<br>現在的訂單狀態為「<b>%s</b>」，請至 Pago 網站進行確認並查看詳情<br><br>訂單編號：%s",
        date, orderUrl, orderItemName, result, updatedOrderStatus, serialNumber);
  
    // Prepare the email content
    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(recipientUserEmail);
    emailRequest.setSubject("【Pago " + contentTitle + "】" + orderItemName);
    emailRequest.setBody(emailBody);
    emailRequest.setContentTitle(contentTitle);
    emailRequest.setRecipientName(username);
  
    // Send the email notification
    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent! (postponeReply)");
  }

  public void sendReplyCancelOrderEmail(Order order, UpdateCancellationRecordRequestDto updateCancellationRecordRequestDto, 
      CancellationRecord cancellationRecord, String updatedOrderStatus) {
    String contentTitle = "訂單取消申請通知";
    String recipientId = cancellationRecord.getUserId();
    User recipientUser = userDao.getUserById(recipientId);
    String recipientUserEmail = recipientUser.getEmail();
    String orderId = order.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", orderId);
    String serialNumber = order.getSerialNumber();

    String orderItemName = order.getOrderItem().getName();
    String username = recipientUser.getFirstName();
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String result = updateCancellationRecordRequestDto.getIsCancelled() ? "通過" : "被拒絕";

    String emailBody = String.format("於 %s ，「<a href=\"%s\" target=\"_blank\">%s</a>」的訂單取消申請已%s。現在的訂單狀態為 「<b>%s</b>」 ，請至 Pago 網站進行確認並查看詳情<br><br>訂單編號：",
            date, orderUrl, orderItemName, result, updatedOrderStatus, serialNumber);

    // Prepare the email content
    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(recipientUserEmail);
    emailRequest.setSubject("【Pago " + contentTitle + "】" + orderItemName);
    emailRequest.setBody(emailBody);
    emailRequest.setContentTitle(contentTitle);
    emailRequest.setRecipientName(username);

    // Send the email notification
    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent! (cancelReply)");
  }
}
