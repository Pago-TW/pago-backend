package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.MessageTypeEnum;
import tw.pago.pagobackend.model.User;

@NoArgsConstructor
@Data
public class SendMessageRequestDto {
  private String messageId;
  private String chatRoomId;
  private String senderId;
  private String content;
  private MessageTypeEnum messageType;


}
