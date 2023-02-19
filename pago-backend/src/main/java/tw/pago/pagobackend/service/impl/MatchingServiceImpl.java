package tw.pago.pagobackend.service.impl;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.constant.MatchingStatusEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dao.MatchingDao;
import tw.pago.pagobackend.dto.ChooseTravelerDto;
import tw.pago.pagobackend.dto.UpdateMatchingRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Matching;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.service.MatchingService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
public class MatchingServiceImpl implements MatchingService {

  @Autowired
  private MatchingDao matchingDao;

  @Autowired
  private UuidGenerator uuidGenerator;

  @Autowired
  private OrderService orderService;

  @Override
  public Matching chooseTraveler(ChooseTravelerDto chooseTravelerDto) {

    String matchingUuid = uuidGenerator.getUuid();
    MatchingStatusEnum matchingInitialStatus = MatchingStatusEnum.TO_BE_PURCHASED;
    chooseTravelerDto.setMatchingId(matchingUuid);
    chooseTravelerDto.setMatchingStatus(matchingInitialStatus);

    // Create Matching
    matchingDao.createMatching(chooseTravelerDto);

    Matching matching = matchingDao.getMatching(matchingUuid);

    // Update Order Status
    UpdateOrderRequestDto updateOrderRequestDto = new UpdateOrderRequestDto();

    Order order = orderService.getOrderById(chooseTravelerDto.getOrderId());
    updateOrderRequestDto = setOrderToUpdateOrderRequestDto(order,
        updateOrderRequestDto);

    orderService.updateOrder(updateOrderRequestDto);

    return matching;
  }

  @Override
  public Matching getMatching(String matchingId) {
    return matchingDao.getMatching(matchingId);
  }


  @Override
  public UpdateOrderRequestDto setOrderToUpdateOrderRequestDto(Order order, UpdateOrderRequestDto updateOrderRequestDto) {
    updateOrderRequestDto.setOrderId(order.getOrderId());
    updateOrderRequestDto.setShopperId(order.getShopperId());
    updateOrderRequestDto.setPackaging(order.getPackaging());
    updateOrderRequestDto.setVerification(order.getVerification());
    updateOrderRequestDto.setDestination(order.getDestination());
    updateOrderRequestDto.setTravelerFee(order.getTravelerFee());
    updateOrderRequestDto.setCurrency(order.getCurrency());
    updateOrderRequestDto.setPlatformFeePercent(order.getPlatformFeePercent());
    updateOrderRequestDto.setTariffFeePercent(order.getTariffFeePercent());
    updateOrderRequestDto.setLatestReceiveItemDate(order.getLatestReceiveItemDate());
    updateOrderRequestDto.setNote(order.getNote());
    updateOrderRequestDto.setOrderStatus(order.getOrderStatus());

    return updateOrderRequestDto;
  }

  @Override
  public Matching updateMatching(String orderId,
      UpdateMatchingRequestDto updateMatchingRequestDto) {

    // Update Matching
    matchingDao.updateMatching(updateMatchingRequestDto);

    // Update Order Status
    Order order = orderService.getOrderById(orderId);
    UpdateOrderRequestDto updateOrderRequestDto = new UpdateOrderRequestDto();
    updateOrderRequestDto = setOrderToUpdateOrderRequestDto(order,
        updateOrderRequestDto);

    updateOrderRequestDto.setOrderStatus(OrderStatusEnum.valueOf(updateMatchingRequestDto.getMatchingStatus().toString()));
    orderService.updateOrder(updateOrderRequestDto);

    return null;
  }
}
