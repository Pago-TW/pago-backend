package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.MessageTypeEnum;
import tw.pago.pagobackend.model.User;

@NoArgsConstructor
@Data
public class MessageResponseDto {
  private String content;
  private String senderName;
  private LocalDate sendDate;
  private MessageTypeEnum messageType;

}
