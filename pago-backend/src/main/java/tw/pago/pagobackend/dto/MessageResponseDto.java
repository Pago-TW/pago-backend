package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.MessageTypeEnum;

@NoArgsConstructor
@Data
public class MessageResponseDto {
  private String senderId;
  private String chatroomId;
  private String content;
  private String senderName;
  private LocalDateTime sendDate;
  private MessageTypeEnum messageType;

}
