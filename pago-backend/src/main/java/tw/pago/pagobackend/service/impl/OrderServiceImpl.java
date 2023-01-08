package tw.pago.pagobackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.service.OrderService;

@Component
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderDao orderDao;


  @Override
  public Integer createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto) {

    Integer orderItemId = orderDao.createOrderItem(createOrderRequestDto);
    System.out.println("orderItemId: " + orderItemId);
    System.out.println("userId: " + userId);
    Integer orderId = orderDao.createOrder(userId, createOrderRequestDto ,orderItemId);

    return orderId;
  }
}
