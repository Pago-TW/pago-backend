package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CancelReasonCategoryEnum;

@NoArgsConstructor
@Data
public class CreateCancellationRecordRequestDto {
  private String cancellationRecordId;
  private String orderId;
  private String userId;

  @NotNull
  private CancelReasonCategoryEnum cancelReason;
  private String note;
  private LocalDate createDate;
  private LocalDate updateDate;
  private Boolean isCanceled;


}
