package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateNotificationRequestDto {
  private String notificationId;
  private String content;

}
