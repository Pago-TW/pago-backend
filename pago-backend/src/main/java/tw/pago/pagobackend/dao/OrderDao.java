package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateOrderRequestDto;

public interface OrderDao {

  Integer createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto,
      Integer orderItemId);

  Integer createOrderItem(CreateOrderRequestDto createOrderRequestDto);

}
