package tw.pago.pagobackend.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Payment {
  private String paymentId;
  private String orderId;
  private String bidId;
  private Boolean isPaid;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;

}
