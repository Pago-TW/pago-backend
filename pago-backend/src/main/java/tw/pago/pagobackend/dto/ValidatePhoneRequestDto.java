package tw.pago.pagobackend.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.validation.ValidPhone;

@Data
@NoArgsConstructor
public class ValidatePhoneRequestDto {
    @NotNull
    @ValidPhone
    private String phone;

    @NotNull
    private String otpCode;
}
