package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.ActionTypeEnum;
import tw.pago.pagobackend.constant.NotificationTypeEnum;

@Data
@NoArgsConstructor
public class Notification {
  private String notificationId;
  private String content;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;
  private NotificationTypeEnum notificationType;
  private String imageUrl;
  private String redirectUrl;
  private ActionTypeEnum actionType;

  // for program use
  @JsonProperty("isRead")
  private boolean isRead;

}
