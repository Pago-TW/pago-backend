package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatroomOtherUserDto {
  private String userId;
  private String fullName;
  private String avatarUrl;
}
