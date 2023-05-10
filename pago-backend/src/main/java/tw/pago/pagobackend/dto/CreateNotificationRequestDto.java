package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.ActionTypeEnum;
import tw.pago.pagobackend.constant.NotificationTypeEnum;

@Data
@NoArgsConstructor
public class CreateNotificationRequestDto {
  private String notificationId;
  private String content;
  private NotificationTypeEnum notificationType;
  private String imageUrl;
  private String redirectUrl;
  private ActionTypeEnum actionType;
}
