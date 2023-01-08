package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateOrderItemDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;

public interface OrderService {

  Integer createOrder (Integer userId, CreateOrderRequestDto createOrderRequestDto);
}
