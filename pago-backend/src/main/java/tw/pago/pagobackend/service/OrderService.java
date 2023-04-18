package tw.pago.pagobackend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.dto.CalculateOrderAmountResponseDto;
import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.CreateFavoriteOrderRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.MatchingShopperResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;

public interface OrderService {

  Order createOrder (String userId, List<MultipartFile> files, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  OrderResponseDto getOrderResponseDtoById(String orderId);

  Order getOrderByUserIdAndOrderId(String userId, String orderId);

  OrderResponseDto getOrderResponseDtoByOrder(Order order);

  String getChosenBidderIdByOrderId(String orderId);


  void updateOrderAndOrderItemByOrderId(Order oldOrder, UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);

  void deleteOrderById(String orderId);

  void createFavoriteOrder(CreateFavoriteOrderRequestDto createFavoriteOrderRequestDto);

  List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto);

  List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  List<OrderResponseDto> getOrderResponseDtoList(ListQueryParametersDto listQueryParametersDto);

  List<OrderResponseDto> getOrderResponseDtoListByOrderList(List<Order> orderList);

  List<MatchingShopperResponseDto> getMatchingShopperList(ListQueryParametersDto listQueryParametersDto);

  Integer countOrder(ListQueryParametersDto listQueryParametersDto);


  Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  Map<String, BigDecimal> calculateOrderEachAmount(Order order);

  CalculateOrderAmountResponseDto calculateOrderEachAmountDuringCreation(CreateOrderRequestDto createOrderRequestDto);

  String generateOrderSerialNumber(CreateOrderRequestDto createOrderRequestDto);

  CancellationRecord requestCancelOrder(CreateCancellationRecordRequestDto createCancellationRecordRequestDto) throws BadRequestException;

  // void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
