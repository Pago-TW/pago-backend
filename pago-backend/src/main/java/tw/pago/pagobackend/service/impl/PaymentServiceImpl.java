package tw.pago.pagobackend.service.impl;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.PaymentDao;
import tw.pago.pagobackend.dto.CreatePaymentRequestDto;
import tw.pago.pagobackend.dto.UpdatePaymentRequestDto;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Payment;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.PaymentService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
public class PaymentServiceImpl implements PaymentService {

  @Value("${base.url}")
  private String BASE_URL;

  private final UuidGenerator uuidGenerator;
  private final PaymentDao paymentDao;
  private final BidService bidService;
  private final OrderService orderService;

  public PaymentServiceImpl(UuidGenerator uuidGenerator,
      PaymentDao paymentDao,
      @Lazy BidService bidService,
      OrderService orderService) {
    this.uuidGenerator = uuidGenerator;
    this.paymentDao = paymentDao;
    this.bidService = bidService;
    this.orderService = orderService;
  }


  @Override
  public String ecpayCheckout(String bidId) {

    Bid bid = bidService.getBidById(bidId);
    String orderTotalAmountWithChosenBid = orderService.calculateOrderEachAmountDuringChooseBid(bidId).getTotalAmount().toString();
    Order order = orderService.getOrderById(bid.getOrderId());
    
    if (order == null) {
      throw new ResourceNotFoundException("ECPay can not retrieve orderInfo, due to Order is null");
    }
    String orderId = order.getOrderId();

    String merchantTradeNo =  uuidGenerator.getUuid().substring(0, 20);
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    String formattedDateTime = now.format(dateTimeFormatter);
    String orderTotalAmount = order.getTotalAmount().toString();
    String serialNumber = order.getSerialNumber();

    // Set AllInOne obj
    AllInOne all = new AllInOne("");
    AioCheckOutALL obj = new AioCheckOutALL();
    obj.setMerchantTradeNo(merchantTradeNo);
    obj.setMerchantTradeDate(formattedDateTime);
    obj.setTotalAmount(orderTotalAmountWithChosenBid);
    obj.setTradeDesc("test Description");
    obj.setItemName("Pago Service, " + "No. " + serialNumber);
    obj.setReturnURL(BASE_URL + "/api/v1/ecpay-checkout/callback");
    obj.setNeedExtraPaidInfo("N");
    obj.setClientBackURL(BASE_URL + "/orders/" + orderId);
    String form = all.aioCheckOut(obj, null);


    // Create Payment
    CreatePaymentRequestDto createPaymentRequestDto = new CreatePaymentRequestDto();
    createPaymentRequestDto.setPaymentId(merchantTradeNo);
    createPaymentRequestDto.setOrderId(order.getOrderId());
    createPaymentRequestDto.setBidId(bidId);
    createPaymentRequestDto.setIsPaid(false);

    createPayment(createPaymentRequestDto);



    return form;

  }

  @Override
  public String paymentCallback() {
    return null;
  }

  @Override
  public void createPayment(CreatePaymentRequestDto createPaymentRequestDto) {

    paymentDao.createPayment(createPaymentRequestDto);

  }

  @Override
  public void updatePayment(UpdatePaymentRequestDto updatePaymentRequestDto) {


    paymentDao.updatePayment(updatePaymentRequestDto);
  }

  @Override
  public Payment getPaymentById(String paymentId) {


    return paymentDao.getPaymentById(paymentId);
  }
}
