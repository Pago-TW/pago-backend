package tw.pago.pagobackend.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetToken {
    private String passwordResetTokenId;
    private String userId;
    private String token;
    private LocalDateTime expiryDate;
    private LocalDateTime createDate;
}
