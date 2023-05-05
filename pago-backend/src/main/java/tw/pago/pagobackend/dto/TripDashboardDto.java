package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TripDashboardDto {
  private int requested;
  private int toBePurchased;
  private int toBeDelivered;

}
