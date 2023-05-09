package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateNotificationUserMappingRequestDto {
  String notificationUserMappingId;
  @NotNull
  String notificationId;
  @NotNull
  String userId;
  boolean isRead;
}
