package tw.pago.pagobackend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.OrderItemDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.FileService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class OrderServiceImpl implements OrderService {

  private static final String OBJECT_TYPE = "order";


  @Autowired
  private OrderDao orderDao;
  @Autowired
  private UuidGenerator uuidGenerator;
  @Autowired
  private FileService fileService;


  @Transactional
  @Override
  public Order createOrder(String userId, List<MultipartFile> files, CreateOrderRequestDto createOrderRequestDto) {

    String orderItemUuid = uuidGenerator.getUuid();
    String orderUuid = uuidGenerator.getUuid();

    Double platformFeePercent = 4.5;
    Double tariffFeePercent = 2.5;

    createOrderRequestDto.getCreateOrderItemDto().setOrderItemId(orderItemUuid);
    createOrderRequestDto.setOrderId(orderUuid);
    createOrderRequestDto.setPlatformFeePercent(platformFeePercent);
    createOrderRequestDto.setTariffFeePercent(tariffFeePercent);
    createOrderRequestDto.setOrderStatus(OrderStatusEnum.REQUESTED);

    orderDao.createOrderItem(createOrderRequestDto);
    orderDao.createOrder(userId, createOrderRequestDto);

    //upload file
    CreateFileRequestDto createFileRequestDto = new CreateFileRequestDto();
    createFileRequestDto.setFileCreator(userId);
    createFileRequestDto.setObjectId(orderUuid);
    createFileRequestDto.setObjectType("order");

    List<URL> uploadedUrls = fileService.uploadFile(files, createFileRequestDto);
    // print out all uploadedurls
    for (URL url: uploadedUrls) {
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


    //get file url
    List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(orderId, OBJECT_TYPE);
    order.getOrderItem().setFileUrls(fileUrls);

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

    // Set the destination country and city names, and the order item in the OrderResponseDto
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

    //get file url
    List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(orderId, OBJECT_TYPE);
    order.getOrderItem().setFileUrls(fileUrls);

    return order;
  }

  @Override
  public OrderResponseDto getOrderResponseDtoByOrder(Order order) {
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

    // Set the destination country and city names, and the order item in the OrderResponseDto
    orderResponseDto.setOrderItem(orderItemDto);
    orderResponseDto.setDestinationCountryName(orderDestinationCountryName);
    orderResponseDto.setDestinationCityName(orderDestinationCityName);

    return orderResponseDto;
  }

  @Override
  public void updateOrderAndOrderItemByOrderId(Order order,
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {
    orderDao.updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);
  }

  @Override
  public List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto) {

    List<Order> orderList = orderDao.getOrderList(listQueryParametersDto);

    // calculate each order amount
    for (Order order: orderList) {
      Map<String, BigDecimal> orderEachAmountMap = calculateOrderEachAmount(order);
      order.setTariffFee(orderEachAmountMap.get("tariffFee"));
      order.setPlatformFee(orderEachAmountMap.get("platformFee"));
      order.setTotalAmount(orderEachAmountMap.get("orderTotalAmount"));
      //get file url
      List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(order.getOrderId(), OBJECT_TYPE);
      order.getOrderItem().setFileUrls(fileUrls);
    }
    return orderList;
  }

  @Override
  public List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto,
      Trip trip) {
    List<Order> matchingOrderListForTrip = orderDao.getMatchingOrderListForTrip(listQueryParametersDto, trip);

    // calculate each order amount
    for (Order order: matchingOrderListForTrip) {
      Map<String, BigDecimal> orderEachAmountMap = calculateOrderEachAmount(order);
      order.setTariffFee(orderEachAmountMap.get("tariffFee"));
      order.setPlatformFee(orderEachAmountMap.get("platformFee"));
      order.setTotalAmount(orderEachAmountMap.get("orderTotalAmount"));
      //get file url
      List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(order.getOrderId(), OBJECT_TYPE);
      order.getOrderItem().setFileUrls(fileUrls);
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

  @Transactional
  @Override
  public void deleteOrderById(String orderId) {
    orderDao.deleteOrderById(orderId);

    //delete file
    String objectType = "order";
    fileService.deleteFilesByObjectIdnType(orderId, objectType);
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
    BigDecimal orderPlatformFeePercent = BigDecimal.valueOf(order.getPlatformFeePercent()).multiply(BigDecimal.valueOf(0.01));
    BigDecimal orderTariffFeePercent = BigDecimal.valueOf(order.getTariffFeePercent()).multiply(BigDecimal.valueOf(0.01));

    // Calculation
    BigDecimal itemTotalAmount = orderItemUnitPrice.multiply(orderItemQuantity); // 1
    BigDecimal tariffFee = itemTotalAmount.multiply(orderTariffFeePercent); // 2
    BigDecimal platformFee = itemTotalAmount.multiply(orderPlatformFeePercent); // 3
    BigDecimal travelerFee = order.getTravelerFee(); // 4

    BigDecimal orderTotalAmount = itemTotalAmount.add(tariffFee).add(platformFee).add(travelerFee); // total = 1 + 2 + 3 + 4

    // Set only the second digit after the decimal point (Banker 's rounding)
    tariffFee = tariffFee.setScale(2, RoundingMode.HALF_EVEN);
    platformFee = platformFee.setScale(2, RoundingMode.HALF_EVEN);
    orderTotalAmount = orderTotalAmount.setScale(2, RoundingMode.HALF_EVEN);

    // Set return value
    orderEachAmountMap.put("tariffFee", tariffFee);
    orderEachAmountMap.put("platformFee", platformFee);
    orderEachAmountMap.put("orderTotalAmount", orderTotalAmount);


    return orderEachAmountMap;
  }

  // @Override
  // public void updateOrder(UpdateOrderRequestDto updateOrderRequestDto) {
  //   orderDao.updateOrder(updateOrderRequestDto);
  // }
}
