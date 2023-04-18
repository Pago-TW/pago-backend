package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.PostponeReasonCategoryEnum;

@Data
@NoArgsConstructor
public class PostponeRecord {
  private String postponeRecordId;
  private String orderId;
  private String userId;
  private PostponeReasonCategoryEnum postponeReason;
  private String note;
  private LocalDate createDate;
  private LocalDate updateDate;
  @JsonProperty("isPostponed")
  private Boolean isPostponed;

}
