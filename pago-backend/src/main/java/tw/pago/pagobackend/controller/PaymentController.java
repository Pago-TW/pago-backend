package tw.pago.pagobackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.UpdatePaymentRequestDto;
import tw.pago.pagobackend.exception.IllegalStatusTransitionException;
import tw.pago.pagobackend.model.Payment;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.PaymentService;

@RestController
@AllArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final BidService bidService;

  @PostMapping("/ecpay-checkout/callback")
  public String paymentCallback(
      @RequestParam(value = "MerchantTradeNo", required = true) String merchantTradeNo,
      @RequestParam(value = "PaymentDate", required = false) String paymentDate,
      @RequestParam(value = "PaymentType",required = false) String paymentType,
      @RequestParam(value = "PaymentTypeChargeFee", required = false) String paymentTypeChargeFee,
      @RequestParam(value = "RtnCode", required = true) String rtnCode,
      @RequestParam(value = "RtnMsg", required = false) String rtnMsg,
      @RequestParam(value = "SimulatePaid", required = false) String simulatePaid,
      @RequestParam(value = "StoreID", required = false) String storeId,
      @RequestParam(value = "TradeAmt", required = false) String tradeAmt,
      @RequestParam(value = "TradeDate", required = false) String tradeDate,
      @RequestParam(value = "TradeNo", required = false) String tradeNo,
      @RequestParam(value = "CheckMacValue", required = false) String checkMacValue
  ) {
    System.out.println("ECpay-checkout Callback");

    if (rtnCode.equals("1")) {
      UpdatePaymentRequestDto updatePaymentRequestDto = new UpdatePaymentRequestDto();
      updatePaymentRequestDto.setPaymentId(merchantTradeNo);
      updatePaymentRequestDto.setIsPaid(true);

      paymentService.updatePayment(updatePaymentRequestDto);

      Payment payment = paymentService.getPaymentById(merchantTradeNo);

      try {
        bidService.chooseBid(payment.getOrderId(), payment.getBidId());

      } catch (IllegalStatusTransitionException e) {
        return e.getMessage();
      }

    } else {
      return null;
    }


    return "1|OK";
  }

}
