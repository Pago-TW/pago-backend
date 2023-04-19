package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreatePaymentRequestDto {
  private String paymentId;
  private String orderId;
  private Boolean isPaid;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;


}
