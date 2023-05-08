package tw.pago.pagobackend.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationUserMapping {
  String notificationUserMappingId;
  String userId;
  String notificationId;
  boolean isRead;
  LocalDateTime createDate;
  LocalDateTime updateDate;

}
