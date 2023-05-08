package tw.pago.pagobackend.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.NotificationTypeEnum;

@Data
@NoArgsConstructor
public class Notification {
  private String notificationId;
  private String content;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
  private NotificationTypeEnum notificationType;

}
