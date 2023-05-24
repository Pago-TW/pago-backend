package tw.pago.pagobackend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.CalculateOrderAmountRequestDto;
import tw.pago.pagobackend.dto.CalculateOrderAmountResponseDto;
import tw.pago.pagobackend.dto.CancellationRecordResponseDto;
import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.CreateFavoriteOrderRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.CreatePostponeRecordRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.MatchingShopperListDto;
import tw.pago.pagobackend.dto.MatchingShopperResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.UpdateCancellationRecordRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdatePostponeRecordRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.PostponeRecord;
import tw.pago.pagobackend.model.Trip;

public interface OrderService {

  Order createOrder (String userId, List<MultipartFile> files, CreateOrderRequestDto createOrderRequestDto);

  Order getOrderById (String orderId);

  OrderResponseDto getOrderResponseDtoById(String orderId);

  Order getOrderByUserIdAndOrderId(String userId, String orderId);

  OrderResponseDto getOrderResponseDtoByOrder(Order order);

  String getChosenBidderIdByOrderId(String orderId);


  void updateOrderAndOrderItemByOrderId(Order oldOrder, UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto, boolean sendStatusUpdateEmail);

  void updateOrderStatusByOrderId(String orderId, OrderStatusEnum updatedOrderStatus);

  void deleteOrderById(String orderId);

  void deleteOrderByOrder(Order order);

  void createFavoriteOrder(CreateFavoriteOrderRequestDto createFavoriteOrderRequestDto);

  List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto);

  List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  List<OrderResponseDto> getOrderResponseDtoList(ListQueryParametersDto listQueryParametersDto);

  List<OrderResponseDto> getOrderResponseDtoListByOrderList(List<Order> orderList);

  MatchingShopperListDto getMatchingShopperList(Order order, ListQueryParametersDto listQueryParametersDto);

  Integer countOrder(ListQueryParametersDto listQueryParametersDto);


  Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

  Map<String, BigDecimal> calculateOrderEachAmount(Order order);

  CalculateOrderAmountResponseDto calculateOrderEachAmountDuringCreation(
      CalculateOrderAmountRequestDto calculateOrderAmountRequestDto);

  CalculateOrderAmountResponseDto calculateOrderEachAmountDuringChooseBid(String bidId);

  String generateOrderSerialNumber(CreateOrderRequestDto createOrderRequestDto);

  CancellationRecord requestCancelOrder(Order order, CreateCancellationRecordRequestDto createCancellationRecordRequestDto) throws BadRequestException;

  CancellationRecord getCancellationRecordById(String cancellationRecordId);

  CancellationRecord getCancellationRecordByOrderId(String orderId);

  CancellationRecordResponseDto getCancellationRecordResponseDtoByCancellationRecord(CancellationRecord cancellationRecord);

  PostponeRecord requestPostponeOrder(Order order, CreatePostponeRecordRequestDto createPostponeRecordRequestDto);

  void replyPostponeOrder(Order order, UpdatePostponeRecordRequestDto updatePostponeRecordRequestDto);

  PostponeRecord getPostponeRecordByOrderId(String orderId);

  void replyCancelOrder(Order order, UpdateCancellationRecordRequestDto updateCancellationRecordRequestDto);

  void sendPostponeRequestEmail(Order order, PostponeRecord postponeRecord);

  void sendCancelRequestEmail(Order order, CancellationRecord cancellationRecord);

  void sendOrderUpdateEmail(Order oldOrder, UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto);
  
  void sendReplyPostponeOrderEmail(Order order, UpdatePostponeRecordRequestDto updatePostponeRecordRequestDto, PostponeRecord postponeRecord, String updatedOrderStatus);

  void sendReplyCancelOrderEmail(Order order, UpdateCancellationRecordRequestDto updateCancellationRecordRequestDto, CancellationRecord cancellationRecord, String updatedOrderStatus);
  
  List<Order> searchOrders(String query);
  // void updateOrder(UpdateOrderRequestDto updateOrderRequestDto);
}
