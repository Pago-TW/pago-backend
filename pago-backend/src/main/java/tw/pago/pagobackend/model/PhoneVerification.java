package tw.pago.pagobackend.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneVerification {
    private String verificationId;
    private String userId;
    private String phone;
    private boolean isPhoneVerified;
    private LocalDateTime createDate;
}
