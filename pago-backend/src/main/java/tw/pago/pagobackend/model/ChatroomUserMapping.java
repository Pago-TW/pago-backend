package tw.pago.pagobackend.model;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatroomUserMapping {
  private String chatroomUserMappingId;
  private String chatroomId;
  private String userId;
  private String lastReadMessageId;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;

}
