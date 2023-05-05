package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewOrderDto {
  private String orderId;
  private ReviewOrderItemDto orderItem;

}
