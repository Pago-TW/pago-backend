package tw.pago.pagobackend.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionTabViewDto {
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private String tabViewName;

}
