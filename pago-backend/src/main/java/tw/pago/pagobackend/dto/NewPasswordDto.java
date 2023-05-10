package tw.pago.pagobackend.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewPasswordDto {
    @NotNull
    private String newPassword;

    private String token;
}
