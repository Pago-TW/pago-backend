package tw.pago.pagobackend.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsRequestDto {
    @NotNull
    private String internationalPhoneNumber;

    @NotNull
    private String message;
}
