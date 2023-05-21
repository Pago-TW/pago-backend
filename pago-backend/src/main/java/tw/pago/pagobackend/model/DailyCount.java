package tw.pago.pagobackend.model;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DailyCount {
  private String dailyCountId;
  private String userId;
  private Integer smsCount;
  private Integer emailCount;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;
}
