package tw.pago.pagobackend.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.MessageTypeEnum;

@NoArgsConstructor
@Data
public class SendMessageRequestDto {
  private String messageId;


  private String chatroomId;

  @NotNull
  private String senderId;
  @NotNull
  private String content;
  @NotNull
  private MessageTypeEnum messageType; // TEXT, FILE


}
