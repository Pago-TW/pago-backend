package tw.pago.pagobackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.service.OrderService;

@RestController
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/users/{userId}/orders")
  public ResponseEntity<Order> createOrder(@PathVariable Integer userId,
      @RequestBody @Valid CreateOrderRequestDto createOrderRequestDto) {

    Integer orderId = orderService.createOrder(userId, createOrderRequestDto);

    Order order = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }


  @PutMapping("/users/{userId}/orders/{orderId}")
  public ResponseEntity<Order> updateOrderById(@PathVariable Integer userId, @PathVariable Integer orderId, @RequestBody @Valid
      UpdateOrderRequestDto updateOrderRequestDto) {

    // Check if the Order to be updated exists
    Order order = orderService.getOrderById(orderId);
    if (order == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(order);
    }

    // Update Order
    updateOrderRequestDto.setShopperId(userId);
    updateOrderRequestDto.setOrderId(orderId);
    orderService.updateOrderById(updateOrderRequestDto);

    Order updatedOrder = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
  }

  @DeleteMapping("/users/{userId}/orders/{orderId}")
  public ResponseEntity<Object> deleteOrderById(@PathVariable Integer orderId) {

    orderService.deleteOrderById(orderId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
