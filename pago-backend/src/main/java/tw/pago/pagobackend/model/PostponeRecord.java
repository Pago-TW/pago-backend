package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.PostponeReasonCategoryEnum;

@Data
@NoArgsConstructor
public class PostponeRecord {
  private String postponeRecordId;
  private String orderId;
  private String userId;
  private PostponeReasonCategoryEnum postponeReason;
  private String note;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
  @JsonProperty("isPostponed")
  private Boolean isPostponed;
  private OrderStatusEnum originalOrderStatus;

}
