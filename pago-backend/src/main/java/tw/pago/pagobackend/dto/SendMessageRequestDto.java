package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.MessageTypeEnum;
import tw.pago.pagobackend.model.User;

@NoArgsConstructor
@Data
public class SendMessageRequestDto {
  private String messageId;


  private String chatRoomId;

  @NotNull
  private String senderId;
  @NotNull
  private String content;
  @NotNull
  private MessageTypeEnum messageType;


}
