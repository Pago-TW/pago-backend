package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePostponeRecordRequestDto {
  private String postponeRecordId;
  private String orderId;
  private String userId;
  private LocalDateTime updateDate;

  @NotNull
  @JsonProperty("isPostponed")
  private Boolean isPostponed;

}
