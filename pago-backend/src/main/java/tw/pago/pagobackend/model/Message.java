package tw.pago.pagobackend.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import tw.pago.pagobackend.constant.MessageTypeEnum;

@Data
@Builder
public class Message {
  private String messageId;
  private String chatRoomId;
  private String senderId;
  private String content;
  private MessageTypeEnum messageType;
  private LocalDateTime sendDate;



}
