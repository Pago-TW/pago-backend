package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OtpValidationDto {
    private String otpCode;
}
