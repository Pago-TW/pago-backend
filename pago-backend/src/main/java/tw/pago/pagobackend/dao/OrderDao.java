package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;

public interface OrderDao {

  Integer createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto,
      Integer orderItemId);

  Integer createOrderItem(CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (Integer orderId);

//  OrderItem getOrderItemById (Integer orderItemId);
}
