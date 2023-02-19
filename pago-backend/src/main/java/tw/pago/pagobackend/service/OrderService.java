package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;

public interface OrderService {

  Order createOrder (Integer userId, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  void updateOrderAndOrderItemByOrderId(
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);

  void deleteOrderById(String orderId);

  void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
