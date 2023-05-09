package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class PasswordRequestDto {
    @NotBlank
    @Email
    private String email;
}
