package tw.pago.pagobackend.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordRequestDto {
  @NotNull
  private String oldPassword;
  @NotNull
  private String newPassword;
  @NotNull
  private String confirmNewPassword;
}
