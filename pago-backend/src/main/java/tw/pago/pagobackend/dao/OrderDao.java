package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.model.Trip;

public interface OrderDao {

  void createOrder(String userId, CreateOrderRequestDto createOrderRequestDto);

  void createOrderItem(CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  Order getOrderByUserIdAndOrderId(String userId, String orderId);

  OrderItem getOrderItemById (String orderItemId);

  void updateOrderAndOrderItemByOrderId(Order order, UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);

  void deleteOrderById(String orderId);

  List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto);

  List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  Integer countOrder(ListQueryParametersDto listQueryParametersDto);

  Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);



  // void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
