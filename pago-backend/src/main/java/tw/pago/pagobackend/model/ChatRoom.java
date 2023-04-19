package tw.pago.pagobackend.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Chatroom {
  private String chatRoomId;
  private LocalDate createDate;
  private LocalDate updateDate;

}