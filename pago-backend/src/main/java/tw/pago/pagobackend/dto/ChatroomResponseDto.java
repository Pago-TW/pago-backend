package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatroomResponseDto {
  private String chatroomId;
  private String currentLoginUserId;
  private Integer totalUnreadMessage;
  private LocalDateTime updateDate;
  private String latestMessageContent;
  private ChatroomOtherUserDto otherUser;
}
