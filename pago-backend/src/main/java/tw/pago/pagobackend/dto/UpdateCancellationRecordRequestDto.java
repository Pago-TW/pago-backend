package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CancelReasonCategoryEnum;

@Data
@NoArgsConstructor
public class UpdateCancellationRecordRequestDto {
  private String cancellationRecordId;
  private String orderId;
  private CancelReasonCategoryEnum cancelReason;
  private String note;
  private LocalDateTime updateDate;

  @NotNull
  private Boolean isCancelled;


}
