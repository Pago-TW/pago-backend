package tw.pago.pagobackend.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.MessageTypeEnum;

@Data
@Builder
public class Message {
  private String messageId;
  private String chatRoomId;
  private String senderId;
  private String content;
  private MessageTypeEnum messageType;
  private LocalDate sendDate;



}
