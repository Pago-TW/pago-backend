package tw.pago.pagobackend.dto;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateDailyCountRequestDto {
  private String dailyCountId;
  private String userId;
  private Integer smsCount;
  private Integer emailCount;
}
