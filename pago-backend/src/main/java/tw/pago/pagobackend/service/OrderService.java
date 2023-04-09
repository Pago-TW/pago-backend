package tw.pago.pagobackend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;

public interface OrderService {

  Order createOrder (String userId, List<MultipartFile> files, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  OrderResponseDto getOrderResponseDtoById(String orderId);

  Order getOrderByUserIdAndOrderId(String userId, String orderId);

  OrderResponseDto getOrderResponseDtoByOrder(Order order);

  String getChosenBidderIdByOrderId(String orderId);


  void updateOrderAndOrderItemByOrderId(Order oldOrder, UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);

  void deleteOrderById(String orderId);

  List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto);

  List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  List<OrderResponseDto> getOrderResponseDtoList(ListQueryParametersDto listQueryParametersDto);

  List<OrderResponseDto> getOrderResponseDtoListByOrderList(List<Order> orderList);

  Integer countOrder(ListQueryParametersDto listQueryParametersDto);

  Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  Map<String, BigDecimal> calculateOrderEachAmount(Order order);

  String generateOrderSerialNumber(CreateOrderRequestDto createOrderRequestDto);

  // void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
