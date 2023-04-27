package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateChatroomUserMappingRequestDto {
  private String chatroomId;
  private String userId;
  private String lastReadMessageId;

}
