package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreatePaymentRequestDto;
import tw.pago.pagobackend.dto.UpdatePaymentRequestDto;
import tw.pago.pagobackend.model.Payment;

public interface PaymentService {

  String ecpayCheckout(String bidId);

  String paymentCallback();

  void createPayment(CreatePaymentRequestDto createPaymentRequestDto);

  void updatePayment(UpdatePaymentRequestDto updatePaymentRequestDto);

  Payment getPaymentById(String paymentId);

}
