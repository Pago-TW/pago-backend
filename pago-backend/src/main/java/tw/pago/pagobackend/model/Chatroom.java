package tw.pago.pagobackend.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Chatroom {
  private String chatroomId;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;

}
