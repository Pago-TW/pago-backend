package tw.pago.pagobackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.CalculateOrderAmountResponseDto;
import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.CreateFavoriteOrderRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.MatchingShopperResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.UpdateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.exception.DuplicateKeyException;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Validated
@RestController
@AllArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final TripService tripService;

  @PostMapping("/orders")
  public ResponseEntity<OrderResponseDto> createOrder(@RequestParam("file") List<MultipartFile> files,
      @RequestParam("data") String createOrderRequestDtoString) throws JsonMappingException, JsonProcessingException {

    // Convert data to DTO
    ObjectMapper objectMapper = new ObjectMapper();
    CreateOrderRequestDto createOrderRequestDto = objectMapper.readValue(createOrderRequestDtoString, CreateOrderRequestDto.class);

    // Get currentLogin User
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    Order order = orderService.createOrder(currentLoginUserId, files, createOrderRequestDto);
    OrderResponseDto orderResponseDto = orderService.getOrderResponseDtoByOrder(order);

//    Order order = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
  }


  @PatchMapping("/orders/{orderId}")
  public ResponseEntity<Order> updateOrder(
      @PathVariable String orderId, @RequestBody @Valid
  UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {


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
    updateOrderAndOrderItemRequestDto.setConsumerId(currentLoginUserId);
    updateOrderAndOrderItemRequestDto.setOrderId(orderId);
    orderService.updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);

    Order updatedOrder = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
  }

  @DeleteMapping("/orders/{orderId}")
  public ResponseEntity<Object> deleteOrderById(@PathVariable String orderId) {

    Order order = orderService.getOrderById(orderId);

    if (!order.getConsumerId().equals(currentUserInfoProvider.getCurrentLoginUserId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    orderService.deleteOrderById(orderId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

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
      @RequestParam(required = false) OrderStatusEnum status,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) CityCode from,
      @RequestParam(required = false) CityCode to,
      @RequestParam(required = false) Boolean isPackagingRequired,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(userId)
        .orderStatus(status)
        .search(search)
        .from(from)
        .to(to)
        .isPackagingRequired(isPackagingRequired)
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
  public ResponseEntity<?> getMatchingShopperList(
      @PathVariable String orderId,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "arrival_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    // Get permission checking needing value
    Order order = orderService.getOrderById(orderId);
    String orderCreatorId = order.getConsumerId();
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Check permission
    if (!currentLoginUserId.equals(orderCreatorId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    Date latestReceiveItemDate = order.getLatestReceiveItemDate();
    LocalDate latestReceiveItemLocalDate = latestReceiveItemDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Date orderCreateDate = order.getCreateDate();
    LocalDate orderCreateLocalDate = orderCreateDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .orderId(orderId)
        .to(order.getDestinationCity())
        .from(order.getOrderItem().getPurchaseCity())
        .latestReceiveItemDate(latestReceiveItemLocalDate)
        .orderCreateDate(orderCreateLocalDate)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    List<MatchingShopperResponseDto> matchingShopperList = orderService.getMatchingShopperList(listQueryParametersDto);

    Integer total = tripService.countMatchingShopper(listQueryParametersDto);

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
  public ResponseEntity<?> createFavoriteOrder(@RequestBody CreateFavoriteOrderRequestDto createFavoriteOrderRequestDto) {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    createFavoriteOrderRequestDto.setUserId(currentLoginUserId);

    orderService.createFavoriteOrder(createFavoriteOrderRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body("Add Success");
  }

  @PostMapping("/calculate-order-amount")
  public ResponseEntity<?> calculateOrderAmount(@RequestBody CreateOrderRequestDto createOrderRequestDto) {

    CalculateOrderAmountResponseDto calculateOrderAmountResponseDto = orderService.calculateOrderEachAmountDuringCreation(createOrderRequestDto);


    return ResponseEntity.status(HttpStatus.CREATED).body(calculateOrderAmountResponseDto);


  }

  @PostMapping("/orders/{orderId}/cancellation-record")
  public ResponseEntity<?> requestCancelOrder(@PathVariable String orderId, @RequestBody @Valid CreateCancellationRecordRequestDto createCancellationRecordRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Permission checking
    Order order = orderService.getOrderById(orderId);
    String cosumerId = order.getConsumerId();
    String shooperId = order.getShopper().getUserId();
    if (!(currentLoginUserId.equals(cosumerId) || currentLoginUserId.equals(shooperId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no Permission.");
    }


    createCancellationRecordRequestDto.setUserId(currentLoginUserId);
    createCancellationRecordRequestDto.setOrderId(orderId);
    try {
      CancellationRecord cancellationRecord = orderService.requestCancelOrder(createCancellationRecordRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(cancellationRecord);
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (DuplicateKeyException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

  }

  @PatchMapping("/orders/{orderId}/cancellation-record")
  public ResponseEntity<?> replyCancelOrder(@PathVariable String orderId, @RequestBody @Valid UpdateCancellationRecordRequestDto updateCancellationRecordRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Permission checking
    Order order = orderService.getOrderById(orderId);
    String cosumerId = order.getConsumerId();
    String shooperId = order.getShopper().getUserId();
    if (!(currentLoginUserId.equals(cosumerId) || currentLoginUserId.equals(shooperId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no Permission.");
    }

    updateCancellationRecordRequestDto.setOrderId(orderId);

    orderService.replyCancelOrder(updateCancellationRecordRequestDto);

    CancellationRecord updateedCancellationRecord = orderService.getCancellationRecordByOrderId(orderId);



    return ResponseEntity.status(HttpStatus.OK).body(updateedCancellationRecord);
  }

}
