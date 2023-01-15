package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;

public interface OrderService {

  Order createOrder (Integer userId, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  void updateOrderById(UpdateOrderRequestDto updateOrderRequestDto);

  void deleteOrderById(Integer orderId);
}
