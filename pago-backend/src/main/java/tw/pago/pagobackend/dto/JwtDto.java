package tw.pago.pagobackend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtDto {
  private String tokenType = "Bearer";
  private String accessToken;


}
