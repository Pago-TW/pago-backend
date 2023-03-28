package tw.pago.pagobackend.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.service.OrderService;

@Validated
@RestController
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/users/{userId}/orders")
  public ResponseEntity<Order> createOrder(@PathVariable String userId, @RequestParam("file") List<MultipartFile> files, 
      @RequestParam("data") String createOrderRequestDtoString) throws JsonMappingException, JsonProcessingException {

    // Create Order
    ObjectMapper objectMapper = new ObjectMapper();
    CreateOrderRequestDto createOrderRequestDto = objectMapper.readValue(createOrderRequestDtoString, CreateOrderRequestDto.class);

    Order order = orderService.createOrder(userId, files, createOrderRequestDto);

//    Order order = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }


  @PatchMapping("/users/{userId}/orders/{orderId}")
  public ResponseEntity<Order> updateOrderById(@PathVariable String userId,
      @PathVariable String orderId, @RequestBody @Valid
  UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {

    // Check if the Order to be updated exists
    Order order = orderService.getOrderById(orderId);
    if (order == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(order);
    }

    // Update Order
    updateOrderAndOrderItemRequestDto.setConsumerId(userId);
    updateOrderAndOrderItemRequestDto.setOrderId(orderId);
    orderService.updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);

    Order updatedOrder = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
  }

  @DeleteMapping("/users/{userId}/orders/{orderId}")
  public ResponseEntity<Object> deleteOrderById(@PathVariable String orderId) {

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
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(userId)
        .orderStatus(status)
        .search(search)
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
}
