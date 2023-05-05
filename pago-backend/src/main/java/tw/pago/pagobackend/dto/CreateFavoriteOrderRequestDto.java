package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateFavoriteOrderRequestDto {
  private String userFavoriteOrderId;
  private String orderId;
  private String userId;


}
