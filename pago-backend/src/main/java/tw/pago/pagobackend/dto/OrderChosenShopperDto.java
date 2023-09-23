package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderChosenShopperDto {
  private String userId;
  private String fullName;
  private String avatarUrl;
  @JsonProperty("isTraveling")
  private boolean isTraveling;
  private LocalDate latestDeliveryDate;
}
