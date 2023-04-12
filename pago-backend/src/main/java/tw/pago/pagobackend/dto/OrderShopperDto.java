package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderShopperDto {
  private String userId;
  private String fullName;
  private String avatarUrl;
  private LocalDate latestDeliveryDate;
}
