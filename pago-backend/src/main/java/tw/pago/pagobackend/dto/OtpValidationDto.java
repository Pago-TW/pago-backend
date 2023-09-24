package tw.pago.pagobackend.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OtpValidationDto {
    @NotBlank
    private String otpCode;
}
