package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateChatRoomRequestDto {
  private String chatroomId;
  private LocalDate createDate;
  private LocalDate updateDate;

}
