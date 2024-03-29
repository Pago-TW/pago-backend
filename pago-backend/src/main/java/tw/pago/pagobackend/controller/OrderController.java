package tw.pago.pagobackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.Validator;

import lombok.AllArgsConstructor;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.CalculateOrderAmountRequestDto;
import tw.pago.pagobackend.dto.CalculateOrderAmountResponseDto;
import tw.pago.pagobackend.dto.CancellationRecordResponseDto;
import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.CreateFavoriteOrderRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.CreatePostponeRecordRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.MatchingShopperListDto;
import tw.pago.pagobackend.dto.MatchingShopperResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.PostponeRecordResponseDto;
import tw.pago.pagobackend.dto.UpdateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.dto.UpdatePostponeRecordRequestDto;
import tw.pago.pagobackend.exception.AccessDeniedException;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.exception.DuplicateKeyException;
import tw.pago.pagobackend.exception.IllegalStatusTransitionException;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.PostponeRecord;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.PaymentService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Validated
@RestController
@AllArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final TripService tripService;
  private final PaymentService paymentService;
  private final BidService bidService;
  private final Validator validator;

  @PostMapping("/orders")
  public ResponseEntity<OrderResponseDto> createOrder(
      @RequestParam(value = "file", required = false) List<MultipartFile> files,
      @RequestParam("data") String createOrderRequestDtoString) throws JsonMappingException, JsonProcessingException {

    // If no files are provided or all files are empty, initialize an empty list
    if (files == null || files.stream().allMatch(file -> file.isEmpty())) {
      files = new ArrayList<>();
    }

    // Convert data to DTO
    ObjectMapper objectMapper = new ObjectMapper();
    CreateOrderRequestDto createOrderRequestDto = objectMapper.readValue(createOrderRequestDtoString,
        CreateOrderRequestDto.class);

    // Perform validation
    Set<ConstraintViolation<CreateOrderRequestDto>> violations = validator.validate(createOrderRequestDto);
    if (!violations.isEmpty()) {
      throw new BadRequestException(violations.toString());
    }

    // Get currentLogin User
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    Order order = orderService.createOrder(currentLoginUserId, files, createOrderRequestDto);
    OrderResponseDto orderResponseDto = orderService.getOrderResponseDtoByOrder(order);

    // Order order = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
  }

  @PatchMapping("/orders/{orderId}")
  public ResponseEntity<?> updateOrder(
      @PathVariable String orderId,
      @RequestBody @Valid UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {

    // Get currentLoginUser
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Get orderCreatorId
    Order order = orderService.getOrderById(orderId);
    String orderCreatorId = order.getConsumerId();

    // Get chosenBidderId
    String chosenBidderId = orderService.getChosenBidderIdByOrderId(orderId);

    // Check permission, only order creator or shopper can update Order
    if (!(currentLoginUserId.equals(orderCreatorId) || currentLoginUserId.equals(chosenBidderId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Update Order
    try {
      updateOrderAndOrderItemRequestDto.setConsumerId(currentLoginUserId);
      updateOrderAndOrderItemRequestDto.setOrderId(orderId);
      orderService.updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, true);

      Order updatedOrder = orderService.getOrderById(orderId);

      return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);

    } catch (IllegalStatusTransitionException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

  }

  @DeleteMapping("/orders/{orderId}")
  public ResponseEntity<Object> deleteOrderById(@PathVariable String orderId) {

    Order order = orderService.getOrderById(orderId);

    // Permission checking
    if (!order.getConsumerId().equals(currentUserInfoProvider.getCurrentLoginUserId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    try {
      orderService.deleteOrderByOrder(order);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

  }

  @GetMapping("/orders/{orderId}")
  public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable String orderId) {

    Order order = orderService.getOrderById(orderId);
    OrderResponseDto orderResponseDto = orderService.getOrderResponseDtoByOrder(order);

    return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
  }

  @GetMapping("/users/{userId}/orders/{orderId}")
  public ResponseEntity<OrderResponseDto> getOrderByUserIdAndOrderId(@PathVariable String userId,
      @PathVariable String orderId) {

    Order order = orderService.getOrderByUserIdAndOrderId(userId, orderId);
    OrderResponseDto orderResponseDto = orderService.getOrderResponseDtoByOrder(order);

    return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);

  }

  @GetMapping("/orders")
  public ResponseEntity<ListResponseDto<OrderResponseDto>> getOrderList(
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) String tripId,
      @RequestParam(required = false) OrderStatusEnum status,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) CountryCode fromCountry,
      @RequestParam(required = false) CityCode fromCity,
      @RequestParam(required = false) CountryCode toCountry,
      @RequestParam(required = false) CityCode toCity,
      @RequestParam(required = false) Boolean isPackagingRequired,
      @RequestParam(required = false) @Min(0) BigDecimal minTravelerFee,
      @RequestParam(required = false) @Min(1) BigDecimal maxTravelerFee,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date  latestReceiveItemDate,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(userId)
        .tripId(tripId)
        .orderStatus(status)
        .search(search)
        .fromCountry(fromCountry)
        .fromCity(fromCity)
        .toCountry(toCountry)
        .toCity(toCity)
        .isPackagingRequired(isPackagingRequired)
        .minTravelerFee(minTravelerFee)
        .maxTravelerFee(maxTravelerFee)
        .orderLatestReceiveItemDate(latestReceiveItemDate)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    List<OrderResponseDto> orderResponseDtoList = orderService.getOrderResponseDtoList(listQueryParametersDto);

    Integer total = orderService.countOrder(listQueryParametersDto);

    ListResponseDto<OrderResponseDto> orderListResponseDto = ListResponseDto.<OrderResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(orderResponseDtoList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(orderListResponseDto);
  }

  @GetMapping("/orders/{orderId}/matching-shoppers")
  public ResponseEntity<?> getMatchingShopperList( // TODO 已出價的要排除掉
      @PathVariable String orderId,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "arrival_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    Order order = orderService.getOrderById(orderId);

    Date latestReceiveItemDate = order.getLatestReceiveItemDate();
    LocalDate latestReceiveItemLocalDate = latestReceiveItemDate.toInstant().atZone(ZoneId.systemDefault())
        .toLocalDate();
    Date orderCreateDate = order.getCreateDate();
    LocalDate orderCreateLocalDate = orderCreateDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .orderId(orderId)
        .consumerId(order.getConsumerId())
        .destinationCountry(order.getDestinationCountry())
        .destinationCity(order.getDestinationCity())
        .purchaseCountry(order.getOrderItem().getPurchaseCountry())
        .purchaseCity(order.getOrderItem().getPurchaseCity())
        .latestReceiveItemDate(latestReceiveItemLocalDate)
        .orderCreateDate(orderCreateLocalDate)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    MatchingShopperListDto matchingShopperListDto = orderService.getMatchingShopperList(order,
        listQueryParametersDto);

    List<MatchingShopperResponseDto> matchingShopperList = matchingShopperListDto.getMatchingShopperResponseDtoList();
    Integer total = matchingShopperListDto.getTotalMatchingShoppers();

    OrderResponseDto orderResponseDto = orderService.getOrderResponseDtoByOrder(order);

    ListResponseDto<MatchingShopperResponseDto> matchingShoppers = ListResponseDto.<MatchingShopperResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .order(orderResponseDto)
        .data(matchingShopperList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(matchingShoppers);

  }

  @PostMapping("/favorites")
  public ResponseEntity<?> createFavoriteOrder(
      @RequestBody CreateFavoriteOrderRequestDto createFavoriteOrderRequestDto) {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    createFavoriteOrderRequestDto.setUserId(currentLoginUserId);

    orderService.createFavoriteOrder(createFavoriteOrderRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body("Add Success");
  }

  @PostMapping("/calculate-order-amount")
  public ResponseEntity<Object> calculateOrderAmount(
      @RequestBody CalculateOrderAmountRequestDto calculateOrderAmountRequestDto) {

    CalculateOrderAmountResponseDto calculateOrderAmountResponseDto;
    if (calculateOrderAmountRequestDto.getBidId() != null) {
      try {
        calculateOrderAmountResponseDto = orderService.calculateOrderEachAmountDuringChooseBid(
            calculateOrderAmountRequestDto.getBidId());
      } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
      }
    } else {
      try {
        calculateOrderAmountResponseDto = orderService.calculateOrderEachAmountDuringCreation(
            calculateOrderAmountRequestDto);
      } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
      }

    }
    return ResponseEntity.status(HttpStatus.OK).body(calculateOrderAmountResponseDto);

  }

  @PostMapping("/orders/{orderId}/cancellation-record")
  public ResponseEntity<?> requestCancelOrder(@PathVariable String orderId,
      @RequestBody @Valid CreateCancellationRecordRequestDto createCancellationRecordRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Permission checking
    Order order = orderService.getOrderById(orderId);
    String consumerId = order.getConsumerId();
    String shopperId = order.getShopper() != null ? order.getShopper().getUserId() : null;
    if (shopperId == null) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("The order does not have a shopper assigned.");
    }

    if (!(currentLoginUserId.equals(consumerId) || currentLoginUserId.equals(shopperId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no Permission.");
    }

    createCancellationRecordRequestDto.setUserId(currentLoginUserId);
    createCancellationRecordRequestDto.setOrderId(orderId);
    try {
      CancellationRecord cancellationRecord = orderService.requestCancelOrder(order,
          createCancellationRecordRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(cancellationRecord);
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (DuplicateKeyException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

  }

  @GetMapping("/orders/{orderId}/cancellation-record")
  public ResponseEntity<CancellationRecordResponseDto> getCancellationRecord(@PathVariable String orderId) {

    CancellationRecord cancellationRecord = orderService.getCancellationRecordByOrderId(orderId);
    CancellationRecordResponseDto cancellationRecordResponseDto = orderService.getCancellationRecordResponseDtoByCancellationRecord(cancellationRecord);

    return ResponseEntity.status(HttpStatus.OK).body(cancellationRecordResponseDto);
  }

  @PatchMapping("/orders/{orderId}/cancellation-record")
  public ResponseEntity<?> replyCancelOrder(@PathVariable String orderId,
      @RequestBody @Valid UpdateCancellationRecordRequestDto updateCancellationRecordRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Permission checking
    Order order = orderService.getOrderById(orderId);
    String consumerId = order.getConsumerId();
    String shopperId = order.getShopper() != null ? order.getShopper().getUserId() : null;
    if (shopperId == null) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("The order does not have a shopper assigned.");
    }

    if (!(currentLoginUserId.equals(consumerId) || currentLoginUserId.equals(shopperId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no Permission.");
    }

    try {
      updateCancellationRecordRequestDto.setOrderId(orderId);
      orderService.replyCancelOrder(order, updateCancellationRecordRequestDto);
      CancellationRecord updateedCancellationRecord = orderService.getCancellationRecordByOrderId(orderId);
      return ResponseEntity.status(HttpStatus.OK).body(updateedCancellationRecord);

    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

  }

  @GetMapping("/orders/{orderId}/postpone-record")
  public ResponseEntity<PostponeRecordResponseDto> getPostponeRecord(@PathVariable String orderId) {
    PostponeRecord postponeRecord = orderService.getPostponeRecordByOrderId(orderId);
    PostponeRecordResponseDto postponeRecordResponseDto = orderService.getPostponeRecordResponseDtoByPostponeRecord(postponeRecord);

    return ResponseEntity.status(HttpStatus.OK).body(postponeRecordResponseDto);
  }

  @PostMapping("/orders/{orderId}/postpone-record")
  public ResponseEntity<?> requestPostponeOrder(@PathVariable String orderId,
      @RequestBody @Valid CreatePostponeRecordRequestDto createPostponeRecordRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Permission checking
    Order order = orderService.getOrderById(orderId);
    String cosumerId = order.getConsumerId();
    String shopperId = order.getShopper() != null ? order.getShopper().getUserId() : null;
    if (shopperId == null) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("The order does not have a shopper assigned.");
    }

    if (!(currentLoginUserId.equals(cosumerId) || currentLoginUserId.equals(shopperId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no Permission.");
    }

    createPostponeRecordRequestDto.setUserId(currentLoginUserId);
    createPostponeRecordRequestDto.setOrderId(orderId);
    try {
      PostponeRecord postponeRecord = orderService.requestPostponeOrder(order, createPostponeRecordRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(postponeRecord);
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (DuplicateKeyException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

  }

  @PatchMapping("/orders/{orderId}/postpone-record")
  public ResponseEntity<?> replyPostponeOrder(@PathVariable String orderId,
      @RequestBody @Valid UpdatePostponeRecordRequestDto updatePostponeRecordRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Permission checking
    Order order = orderService.getOrderById(orderId);
    String cosumerId = order.getConsumerId();
    String shopperId = order.getShopper() != null ? order.getShopper().getUserId() : null;
    if (shopperId == null) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("The order does not have a shopper assigned.");
    }

    if (!(currentLoginUserId.equals(cosumerId) || currentLoginUserId.equals(shopperId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no Permission.");
    }

    try {
      updatePostponeRecordRequestDto.setOrderId(orderId);
      orderService.replyPostponeOrder(order, updatePostponeRecordRequestDto);
      PostponeRecord updatedPostponeRecord = orderService.getPostponeRecordByOrderId(orderId);

      return ResponseEntity.status(HttpStatus.OK).body(updatedPostponeRecord);
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

  }

  // @PostMapping("/ecpay-checkout/callback")
  // public String paymentCallback(
  // @RequestParam(value = "MerchantTradeNo", required = true) String
  // merchantTradeNo,
  // @RequestParam(value = "PaymentDate", required = false) String paymentDate,
  // @RequestParam(value = "PaymentType",required = false) String paymentType,
  // @RequestParam(value = "PaymentTypeChargeFee", required = false) String
  // paymentTypeChargeFee,
  // @RequestParam(value = "RtnCode", required = true) String rtnCode,
  // @RequestParam(value = "RtnMsg", required = false) String rtnMsg,
  // @RequestParam(value = "SimulatePaid", required = false) String simulatePaid,
  // @RequestParam(value = "StoreID", required = false) String storeId,
  // @RequestParam(value = "TradeAmt", required = false) String tradeAmt,
  // @RequestParam(value = "TradeDate", required = false) String tradeDate,
  // @RequestParam(value = "TradeNo", required = false) String tradeNo,
  // @RequestParam(value = "CheckMacValue", required = false) String checkMacValue
  // ) {
  // System.out.println("ECpay-checkout Callback");
  //
  // if (rtnCode.equals("1")) {
  // UpdatePaymentRequestDto updatePaymentRequestDto = new
  // UpdatePaymentRequestDto();
  // updatePaymentRequestDto.setPaymentId(merchantTradeNo);
  // updatePaymentRequestDto.setIsPaid(true);
  //
  // paymentService.updatePayment(updatePaymentRequestDto);
  //
  // Payment payment = paymentService.getPaymentById(merchantTradeNo);
  //
  // try {
  // bidService.chooseBid(payment.getOrderId(), payment.getBidId());
  //
  // } catch (IllegalStatusTransitionException e) {
  // return e.getMessage();
  // }
  //
  // } else {
  // return null;
  // }
  //
  //
  // return "1|OK";
  // }

}
