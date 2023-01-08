package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateOrderItemDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.model.Order;

public interface OrderService {

  Integer createOrder (Integer userId, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (Integer orderItemId);
}
