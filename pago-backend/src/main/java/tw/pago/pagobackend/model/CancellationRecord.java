package tw.pago.pagobackend.model;

import java.time.LocalDate;
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
  private LocalDate createDate;
  private LocalDate updateDate;
  private boolean isCanceled;

}
