package tw.pago.pagobackend.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetToken {
    @JsonIgnore
    private String passwordResetTokenId;
    @JsonIgnore
    private String userId;
    @JsonIgnore
    private String token;
    private ZonedDateTime expiryDate;
    private ZonedDateTime createDate;
}
