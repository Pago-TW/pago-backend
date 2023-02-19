package tw.pago.pagobackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
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
  public Order createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto) {

    String orderItemUuid = uuidGenerator.getUuid();
    String orderUuid = uuidGenerator.getUuid();

    createOrderRequestDto.getCreateOrderItemDto().setOrderItemId(orderItemUuid);
    createOrderRequestDto.setOrderId(orderUuid);

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
  public void updateOrderAndOrderItemByOrderId(
      UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {
    orderDao.updateOrderAndOrderItemByOrderId(updateOrderAndOrderItemRequestDto);
  }

  @Override
  public void deleteOrderById(String orderId) {
    orderDao.deleteOrderById(orderId);
  }

  @Override
  public void updateOrder(UpdateOrderRequestDto updateOrderRequestDto) {
    orderDao.updateOrder(updateOrderRequestDto);
  }
}
