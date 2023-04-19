package tw.pago.pagobackend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.PaymentDao;
import tw.pago.pagobackend.dto.CreatePaymentRequestDto;
import tw.pago.pagobackend.dto.UpdatePaymentRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Payment;
import tw.pago.pagobackend.service.PaymentService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {


  private final UuidGenerator uuidGenerator;
  private final PaymentDao paymentDao;


  @Override
  public String ecpayCheckout(Order order) {
    return null;
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
  public Payment gePaymentById(String paymentId) {
    return null;
  }
}
