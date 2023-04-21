package tw.pago.pagobackend.model;


import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatroomUserMapping {
  private String chatroomUserMappingId;
  private String chatroomId;
  private String userId;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;

}
