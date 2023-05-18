package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Otp {
    @JsonIgnore
    private String otpId;
    @JsonIgnore
    private String internationalPhoneNumber;
    @JsonIgnore
    private String otpCode;
    private ZonedDateTime expiryDate;
    private ZonedDateTime createDate;
}
