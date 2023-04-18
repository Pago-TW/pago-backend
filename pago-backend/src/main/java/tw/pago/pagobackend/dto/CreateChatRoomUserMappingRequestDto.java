package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateChatRoomUserMappingRequestDto {
  private String chatroomUserMappingId;
  private String chatroomId;
  private String userId;
  private LocalDate createDate;
  private LocalDate updateDate;

}
