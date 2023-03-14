package tw.pago.pagobackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderDao orderDao;
  @Autowired
  private UuidGenerator uuidGenerator;

  @Transactional
  @Override
  public Order createOrder(String userId, CreateOrderRequestDto createOrderRequestDto) {

    String orderItemUuid = uuidGenerator.getUuid();
    String orderUuid = uuidGenerator.getUuid();

    Double platformFeePercent = 4.5;
    Double tariffFeePercent = 2.5;

    createOrderRequestDto.getCreateOrderItemDto().setOrderItemId(orderItemUuid);
    createOrderRequestDto.setOrderId(orderUuid);
    createOrderRequestDto.setPlatformFeePercent(platformFeePercent);
    createOrderRequestDto.setTariffFeePercent(tariffFeePercent);
    createOrderRequestDto.setOrderStatus(OrderStatusEnum.REQUESTED);

    orderDao.createOrderItem(createOrderRequestDto);
    orderDao.createOrder(userId, createOrderRequestDto);

    Order order = getOrderById(orderUuid);

    return order;
  }

  @Override
  public Order getOrderById(String orderId) {
    Order order = orderDao.getOrderById(orderId);

    String orderItemId = order.getOrderItemId();
    OrderItem orderItem = orderDao.getOrderItemById(orderItemId);

    order.setOrderItem(orderItem);

    return order;
  }


  @Override
  public void updateOrderAndOrderItemByOrderId(Order order,
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {
    orderDao.updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto);
  }

  @Override
  public void deleteOrderById(String orderId) {
    orderDao.deleteOrderById(orderId);
  }

  // @Override
  // public void updateOrder(UpdateOrderRequestDto updateOrderRequestDto) {
  //   orderDao.updateOrder(updateOrderRequestDto);
  // }
}
