package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateOrderItemDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;

public interface OrderService {

  Integer createOrder (Integer userId, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (Integer orderItemId);

  void updateOrderById(UpdateOrderRequestDto updateOrderRequestDto);

  void deleteOrderById(Integer orderId);
}
