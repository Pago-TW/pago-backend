package tw.pago.pagobackend.dao;

import java.util.List;

import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.CreateFavoriteOrderRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.model.Trip;

public interface OrderDao {

  void createOrder(String userId, CreateOrderRequestDto createOrderRequestDto);

  void createOrderItem(CreateOrderRequestDto createOrderRequestDto);

  void createFavoriteOrder(CreateFavoriteOrderRequestDto createFavoriteOrderRequestDto);

  Order getOrderById (String orderId);

  Order getOrderByUserIdAndOrderId(String userId, String orderId);

  String getChosenBidderIdByOrderId(String orderId);

  OrderItem getOrderItemById (String orderItemId);

  void updateOrderAndOrderItemByOrderId(UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);

  void updateOrderStatusByOrderId(String orderId, OrderStatusEnum updatedOrderStatus);

  void deleteOrderById(String orderId);

  void deleteOrderItemById(String orderItemId);

  List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto);

  List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  Integer countOrder(ListQueryParametersDto listQueryParametersDto);

  Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  List<Order> searchOrders(String query);

  // void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
