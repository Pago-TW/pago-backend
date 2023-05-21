package tw.pago.pagobackend.dto;

import java.time.LocalDateTime;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneVerificationDto {
    private String verificationId;
    private String userId;
    private String phone;
    private boolean isPhoneVerified;
    private ZonedDateTime createDate;
}
