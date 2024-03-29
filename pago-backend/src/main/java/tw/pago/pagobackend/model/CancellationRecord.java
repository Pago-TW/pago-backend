package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CancelReasonCategoryEnum;

@NoArgsConstructor
@Data
public class CancellationRecord {
  private String cancellationRecordId;
  private String orderId;
  private String userId;
  private CancelReasonCategoryEnum cancelReason;
  private String note;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
  @JsonProperty("isCancelled")
  private Boolean isCancelled;

}
