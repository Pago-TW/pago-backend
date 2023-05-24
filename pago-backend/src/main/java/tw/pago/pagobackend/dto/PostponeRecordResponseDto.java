package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.PostponeReasonCategoryEnum;

@Data
@NoArgsConstructor
public class PostponeRecordResponseDto {
  private String postponeRecordId;
  private String orderId;
  private String userId;
  private String postponeReason; // Because front-end not need to know Enums, so we return Enum.description for frontend to display text in UI
  private String note;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
  @JsonProperty("isPostponed")
  private Boolean isPostponed;
  private OrderStatusEnum originalOrderStatus;

}
