package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePaymentRequestDto {
  private String paymentId;
  private Boolean isPaid;
  private LocalDateTime updateDate;

}
