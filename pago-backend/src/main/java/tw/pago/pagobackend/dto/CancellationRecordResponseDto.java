package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CancelReasonCategoryEnum;

@Data
@NoArgsConstructor
public class CancellationRecordResponseDto {
  private String cancellationRecordId;
  private String orderId;
  private String userId;
  private String cancelReason; // Because front-end not need to know Enums, so we return Enum.description for frontend to display text in UI
  private String note;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
  @JsonProperty("isCancelled")
  private Boolean isCancelled;

}
