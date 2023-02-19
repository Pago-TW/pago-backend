package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;

public interface OrderDao {

  void createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto);

  void createOrderItem(CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  OrderItem getOrderItemById (String orderItemId);

  void updateOrderAndOrderItemByOrderId(UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);

  void deleteOrderById(String orderId);

  void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
