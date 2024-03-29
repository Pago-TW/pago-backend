package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.MessageTypeEnum;

@Data
@NoArgsConstructor
public class ChatroomResponseDto {
  private String chatroomId;
  private String currentLoginUserId;
  private Integer totalUnreadMessage;
  private String latestMessageSenderId;
  private ZonedDateTime latestMessageSendDate;
  private String latestMessageContent;
  private MessageTypeEnum latestMessageType;
  private ChatroomOtherUserDto otherUser;
}
