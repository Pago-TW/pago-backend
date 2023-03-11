package tw.pago.pagobackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.service.OrderService;

@RestController
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/users/{userId}/orders")
  public ResponseEntity<Order> createOrder(@PathVariable String userId,
      @RequestBody @Valid CreateOrderRequestDto createOrderRequestDto) {

    Order order = orderService.createOrder(userId, createOrderRequestDto);

//    Order order = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }


  @PatchMapping("/users/{userId}/orders/{orderId}")
  public ResponseEntity<Order> updateOrderById(@PathVariable String userId, @PathVariable String orderId, @RequestBody @Valid
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
  public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {

    Order order = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.OK).body(order);
  }
}
