package tw.pago.pagobackend.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Chatroom {
  private String chatroomId;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;

}
