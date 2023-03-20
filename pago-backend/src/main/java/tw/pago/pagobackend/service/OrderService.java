package tw.pago.pagobackend.service;

import java.math.BigDecimal;
import java.util.List;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;

public interface OrderService {

  Order createOrder (String userId, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  // Order getUserOrderById (String orderId, String userId);

  void updateOrderAndOrderItemByOrderId(Order order,
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);

  void deleteOrderById(String orderId);

  List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto);

  Integer countOrder(ListQueryParametersDto listQueryParametersDto);

  BigDecimal calculateOrderTotalAmount(Order order);

  // void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
