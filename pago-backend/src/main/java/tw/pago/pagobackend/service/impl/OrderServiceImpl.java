package tw.pago.pagobackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.service.OrderService;

@Component
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderDao orderDao;

  @Transactional
  @Override
  public Integer createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto) {

    Integer orderItemId = orderDao.createOrderItem(createOrderRequestDto);
    Integer orderId = orderDao.createOrder(userId, createOrderRequestDto, orderItemId);

    return orderId;
  }

  @Override
  public Order getOrderById(Integer orderId) {
    Order order = orderDao.getOrderById(orderId);

    Integer orderItemId = order.getOrderItemId();
    OrderItem orderItem = orderDao.getOrderItemById(orderItemId);

    order.setOrderItem(orderItem);

    return order;
  }


  @Override
  public void updateOrderById(UpdateOrderRequestDto updateOrderRequestDto) {
    orderDao.updateOrderById(updateOrderRequestDto);
  }
}
