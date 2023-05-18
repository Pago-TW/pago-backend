package tw.pago.pagobackend.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Otp {
    private String otpId;
    private String internationalPhoneNumber;
    private String otpCode;
    private LocalDateTime expiryDate;
    private LocalDateTime createDate;
}
