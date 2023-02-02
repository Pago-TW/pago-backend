package tw.pago.pagobackend.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.constant.MatchingStatusEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dao.MatchingDao;
import tw.pago.pagobackend.dto.ChooseTravelerDto;
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
    setOrderToOrderRequetDto(order, updateOrderRequestDto);

    if (updateOrderRequestDto == null) {
      System.out.println("NULL");
    }
    orderService.updateOrderById(updateOrderRequestDto);

    return matching;
  }

  @Override
  public Matching getMatching(String matchingId) {
    return matchingDao.getMatching(matchingId);
  }


  @Override
  public void setOrderToOrderRequetDto(Order order, UpdateOrderRequestDto updateOrderRequestDto) {

    updateOrderRequestDto.setOrderId(order.getOrderId());
    updateOrderRequestDto.setShopperId(order.getShopperId());
    updateOrderRequestDto.getCreateOrderItemDto().setOrderItemId(order.getOrderItemId());
    updateOrderRequestDto.getCreateOrderItemDto().setName(order.getOrderItem().getName());
    updateOrderRequestDto.getCreateOrderItemDto().setImageUrl(order.getOrderItem().getImageUrl());
    updateOrderRequestDto.getCreateOrderItemDto()
        .setDescription(order.getOrderItem().getDescription());
    updateOrderRequestDto.getCreateOrderItemDto().setQuantity(order.getOrderItem().getQuantity());
    updateOrderRequestDto.getCreateOrderItemDto().setUnitPrice(order.getOrderItem().getUnitPrice());
    updateOrderRequestDto.getCreateOrderItemDto()
        .setPurchaseLocation(order.getOrderItem().getPurchaseLocation());
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

  }
}
