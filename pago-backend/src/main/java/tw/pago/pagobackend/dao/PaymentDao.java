package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreatePaymentRequestDto;
import tw.pago.pagobackend.dto.UpdatePaymentRequestDto;
import tw.pago.pagobackend.model.Payment;

public interface PaymentDao {

  void createPayment(CreatePaymentRequestDto createPaymentRequestDto);

  Payment getPaymentById(String paymentId);

  void updatePayment(UpdatePaymentRequestDto updatePaymentRequestDto);

}
