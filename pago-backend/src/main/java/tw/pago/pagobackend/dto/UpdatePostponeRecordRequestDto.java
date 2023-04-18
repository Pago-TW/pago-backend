package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePostponeRecordRequestDto {
  private String postponeRecordId;
  private String orderId;
  private String userId;
  private LocalDate updateDate;
  @JsonProperty("isPostponed")
  private Boolean isPostponed;

}
